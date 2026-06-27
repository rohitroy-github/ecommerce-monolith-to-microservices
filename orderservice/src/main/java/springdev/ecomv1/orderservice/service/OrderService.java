package springdev.ecomv1.orderservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import springdev.ecomv1.orderservice.client.PaymentClient;
import springdev.ecomv1.orderservice.client.ProductClient;
import springdev.ecomv1.orderservice.dto.CreateOrderRequest;
import springdev.ecomv1.orderservice.dto.OrderResponse;
import springdev.ecomv1.orderservice.dto.OrderStatusResponse;
import springdev.ecomv1.orderservice.dto.PaymentClientResponse;
import springdev.ecomv1.orderservice.dto.ProcessPaymentRequest;
import springdev.ecomv1.orderservice.dto.ProductAvailabilityResponse;
import springdev.ecomv1.orderservice.dto.ProductClientResponse;
import springdev.ecomv1.orderservice.dto.ReduceStockRequest;
import springdev.ecomv1.orderservice.dto.SellerOrderResponse;
import springdev.ecomv1.orderservice.dto.UpdateOrderStatusRequest;
import springdev.ecomv1.orderservice.entity.Order;
import springdev.ecomv1.orderservice.entity.OrderItem;
import springdev.ecomv1.orderservice.enums.OrderStatus;
import springdev.ecomv1.orderservice.exception.BadGatewayException;
import springdev.ecomv1.orderservice.exception.ConflictException;
import springdev.ecomv1.orderservice.exception.ResourceNotFoundException;
import springdev.ecomv1.orderservice.exception.ServiceUnavailableException;
import springdev.ecomv1.orderservice.repository.OrderItemRepository;
import springdev.ecomv1.orderservice.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {

        private final OrderRepository orderRepository;
        private final OrderItemRepository orderItemRepository;
        private final ProductClient productClient;
        private final PaymentClient paymentClient;

        /**
         * Workflow summary:
         * 1) Persist a PENDING order shell.
         * 2) Validate stock and fetch product pricing/seller metadata.
         * 3) Reserve stock, process payment, then transition to CONFIRMED.
         * Any downstream failure transitions the order to FAILED for traceability.
         */
        public OrderResponse createOrder(CreateOrderRequest request) {
                LocalDateTime now = LocalDateTime.now();

                // Step 1: Create a minimal order record first so all later operations can
                // reference orderId.
                Order order = Order.builder()
                                .customerId(request.getCustomerId())
                                .totalAmount(BigDecimal.ZERO)
                                .status(OrderStatus.PENDING)
                                .createdAt(now)
                                .updatedAt(now)
                                .build();

                OrderItem item = OrderItem.builder()
                                .productId(request.getProductId())
                                .quantity(request.getQuantity())
                                .price(BigDecimal.ZERO)
                                .order(order)
                                .build();

                order.getItems().add(item);

                Order savedOrder = orderRepository.save(order);

                try {
                        // Step 2: Guard against overselling before reserving inventory.
                        ProductAvailabilityResponse availability = productClient
                                        .getProductAvailability(request.getProductId());

                        int availableQuantity = availability.getQuantity() == null ? 0 : availability.getQuantity();
                        if (!availability.isAvailable() || availableQuantity < request.getQuantity()) {
                                markOrderAsFailed(savedOrder);
                                throw new ConflictException("Product stock is not sufficient");
                        }

                        // Step 3: Fetch product pricing + seller context used for financial and
                        // ownership tracking.
                        ProductClientResponse product = productClient.getProductById(request.getProductId());
                        BigDecimal productPrice = product.getPrice() == null ? BigDecimal.ZERO : product.getPrice();
                        BigDecimal totalAmount = productPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

                        // Persist seller linkage on the item so downstream services can resolve
                        // fulfillment ownership.
                        if (!savedOrder.getItems().isEmpty()) {
                                OrderItem savedItem = savedOrder.getItems().get(0);
                                savedItem.setSellerId(product.getSellerId());
                                savedItem.setPrice(productPrice);
                        }

                        // Step 4: Reserve stock only after validation and metadata fetch.
                        productClient.reduceStock(
                                        request.getProductId(),
                                        ReduceStockRequest.builder().quantity(request.getQuantity()).build());

                        // Step 5: Charge customer after inventory reservation succeeds.
                        ProcessPaymentRequest paymentRequest = ProcessPaymentRequest.builder()
                                        .orderId(savedOrder.getId())
                                        .amount(totalAmount)
                                        .build();

                        PaymentClientResponse paymentResponse;
                        try {
                                paymentResponse = paymentClient.processPayment(paymentRequest);
                        } catch (RetryableException ex) {
                                markOrderAsFailed(savedOrder);
                                throw new ServiceUnavailableException(
                                                "Payment service is currently unavailable. Please try again later.");
                        } catch (FeignException ex) {
                                markOrderAsFailed(savedOrder);
                                throw new BadGatewayException("Payment service call failed");
                        }

                        if (paymentResponse == null || paymentResponse.getStatus() == null
                                        || !"SUCCESS".equalsIgnoreCase(paymentResponse.getStatus())) {
                                markOrderAsFailed(savedOrder);
                                throw new BadGatewayException("Payment processing failed");
                        }

                        // Step 6: Finalize order state only when stock reservation and payment are both
                        // successful.
                        savedOrder.setTotalAmount(totalAmount);
                        savedOrder.setStatus(OrderStatus.CONFIRMED);
                        savedOrder.setUpdatedAt(LocalDateTime.now());
                        Order confirmedOrder = orderRepository.save(savedOrder);

                        return buildOrderResponse("Order confirmed", confirmedOrder, request.getProductId(),
                                        request.getQuantity());
                } catch (FeignException.NotFound ex) {
                        markOrderAsFailed(savedOrder);
                        throw new ResourceNotFoundException("Product not found with id: " + request.getProductId());
                } catch (RetryableException ex) {
                        markOrderAsFailed(savedOrder);
                        throw new ServiceUnavailableException(
                                        "Product service is currently unavailable. Please try again later.");
                } catch (FeignException ex) {
                        markOrderAsFailed(savedOrder);
                        throw new BadGatewayException("Product service call failed");
                } catch (ResourceNotFoundException | ConflictException | ServiceUnavailableException
                                | BadGatewayException ex) {
                        throw ex;
                } catch (Exception ex) {
                        markOrderAsFailed(savedOrder);
                        throw new BadGatewayException("Order processing failed");
                }
        }

        /**
         * Returns only the lifecycle status for a given order id.
         * This method is intentionally lightweight for polling/status-check use cases.
         */
        public OrderStatusResponse getOrderById(Long id) {
                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

                return OrderStatusResponse.builder()
                                .status(order.getStatus().name())
                                .build();
        }

        /**
         * Manually updates order status.
         * Intended for operational/admin workflows, not for primary checkout
         * orchestration.
         */
        public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

                order.setStatus(request.getStatus());
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);

                Optional<OrderItem> firstItem = order.getItems().stream().findFirst();

                return buildOrderResponse(
                                "Order status updated",
                                order,
                                firstItem.map(OrderItem::getProductId).orElse(null),
                                firstItem.map(OrderItem::getQuantity).orElse(null));
        }

        /**
         * Returns order records associated with a seller.
         * The mapping is driven by seller-tagged order items to support seller-scoped
         * views.
         */
        public List<SellerOrderResponse> getOrdersBySellerId(Long sellerId) {
                return orderItemRepository.findBySellerId(sellerId).stream()
                                .map(item -> {
                                        Order order = item.getOrder();
                                        return SellerOrderResponse.builder()
                                                        .orderId(order.getId())
                                                        .sellerId(sellerId)
                                                        .customerId(order.getCustomerId())
                                                        .productId(item.getProductId())
                                                        .quantity(item.getQuantity())
                                                        .status(order.getStatus().name())
                                                        .totalAmount(order.getTotalAmount())
                                                        .createdAt(order.getCreatedAt())
                                                        .updatedAt(order.getUpdatedAt())
                                                        .build();
                                })
                                .toList();
        }

        /**
         * Centralized failure transition to keep status updates and timestamps
         * consistent.
         */
        private void markOrderAsFailed(Order order) {
                order.setStatus(OrderStatus.FAILED);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
        }

        /**
         * Builds a stable API response shape for order operations.
         */
        private OrderResponse buildOrderResponse(String message, Order order, Long productId, Integer quantity) {
                return OrderResponse.builder()
                                .message(message)
                                .customerId(order.getCustomerId())
                                .productId(productId)
                                .quantity(quantity)
                                .status(order.getStatus().name())
                                .build();
        }
}
