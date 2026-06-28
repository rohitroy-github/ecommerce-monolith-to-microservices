package springdev.ecomv1.orderservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import springdev.ecomv1.orderservice.dto.CreateOrderRequest;
import springdev.ecomv1.orderservice.dto.OrderResponse;
import springdev.ecomv1.orderservice.dto.OrderStatusResponse;
import springdev.ecomv1.orderservice.dto.SellerOrderResponse;
import springdev.ecomv1.orderservice.dto.UpdateOrderStatusRequest;
import springdev.ecomv1.orderservice.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Temporary stub endpoint until the order workflow is wired to persistence and downstream services.
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrderStatusResponse> getOrderById(@PathVariable Long id) {
        OrderStatusResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    // Seller-scoped endpoint to fetch orders linked to a specific seller id.
    @GetMapping("/sellers/{sellerId}")
    public ResponseEntity<List<SellerOrderResponse>> getOrdersBySellerId(@PathVariable Long sellerId) {
        List<SellerOrderResponse> response = orderService.getOrdersBySellerId(sellerId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse response = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(response);
    }
}
