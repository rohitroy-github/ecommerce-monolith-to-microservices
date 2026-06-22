package springdev.ecomv1.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import springdev.ecomv1.orderservice.dto.PaymentClientResponse;
import springdev.ecomv1.orderservice.dto.ProcessPaymentRequest;

@FeignClient(name = "payment-service", url = "${services.payment.url}")
public interface PaymentClient {

    @PostMapping("/api/payments")
    PaymentClientResponse processPayment(@RequestBody ProcessPaymentRequest request);
}
