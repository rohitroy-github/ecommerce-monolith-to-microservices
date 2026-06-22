package springdev.ecomv1.orderservice.service;

import feign.FeignException;
import feign.RetryableException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import springdev.ecomv1.orderservice.client.PaymentClient;
import springdev.ecomv1.orderservice.client.ProductClient;
import springdev.ecomv1.orderservice.exception.BadGatewayException;
import springdev.ecomv1.orderservice.exception.ConflictException;
import springdev.ecomv1.orderservice.exception.ResourceNotFoundException;
import springdev.ecomv1.orderservice.exception.ServiceUnavailableException;
import springdev.ecomv1.orderservice.dto.CreateOrderRequest;
import springdev.ecomv1.orderservice.dto.OrderResponse;
import springdev.ecomv1.orderservice.dto.OrderStatusResponse;
import springdev.ecomv1.orderservice.dto.PaymentClientResponse;
import springdev.ecomv1.orderservice.dto.ProcessPaymentRequest;
import springdev.ecomv1.orderservice.dto.ProductAvailabilityResponse;
import springdev.ecomv1.orderservice.dto.ProductClientResponse;
import springdev.ecomv1.orderservice.dto.ReduceStockRequest;
import springdev.ecomv1.orderservice.dto.UpdateOrderStatusRequest;
import springdev.ecomv1.orderservice.entity.Order;
import springdev.ecomv1.orderservice.entity.OrderItem;
import springdev.ecomv1.orderservice.enums.OrderStatus;
import springdev.ecomv1.orderservice.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
        private final ProductClient productClient;
        private final PaymentClient paymentClient;

        // Saves order as PENDING, orchestrates product and payment steps, then marks CONFIRMED or FAILED.
    public OrderResponse createOrder(CreateOrderRequest request) {
        LocalDateTime now = LocalDateTime.now();

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
                        ProductAvailabilityResponse availability = productClient.getProductAvailability(request.getProductId());

                        int availableQuantity = availability.getQuantity() == null ? 0 : availability.getQuantity();
                        if (!availability.isAvailable() || availableQuantity < request.getQuantity()) {
                                markOrderAsFailed(savedOrder);
                                throw new ConflictException("Product stock is not sufficient");
                        }

                        ProductClientResponse product = productClient.getProductById(request.getProductId());
                        BigDecimal productPrice = product.getPrice() == null ? BigDecimal.ZERO : product.getPrice();
                        BigDecimal totalAmount = productPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

                        productClient.reduceStock(
                                        request.getProductId(),
                                        ReduceStockRequest.builder().quantity(request.getQuantity()).build());

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

                        savedOrder.setTotalAmount(totalAmount);
                        savedOrder.setStatus(OrderStatus.CONFIRMED);
                        savedOrder.setUpdatedAt(LocalDateTime.now());
                        Order confirmedOrder = orderRepository.save(savedOrder);

                        return buildOrderResponse("Order confirmed", confirmedOrder, request.getProductId(), request.getQuantity());
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
                } catch (ResourceNotFoundException | ConflictException | ServiceUnavailableException | BadGatewayException ex) {
                        throw ex;
                } catch (Exception ex) {
                        markOrderAsFailed(savedOrder);
                        throw new BadGatewayException("Order processing failed");
                }
    }

        public OrderStatusResponse getOrderById(Long id) {
                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

                return OrderStatusResponse.builder()
                                .status(order.getStatus().name())
                                .build();
        }

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

        private void markOrderAsFailed(Order order) {
                order.setStatus(OrderStatus.FAILED);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
        }

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
