package springdev.ecomv1.paymentservice.service;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springdev.ecomv1.paymentservice.dto.PaymentResponse;
import springdev.ecomv1.paymentservice.dto.ProcessPaymentRequest;
import springdev.ecomv1.paymentservice.entity.Payment;
import springdev.ecomv1.paymentservice.entity.PaymentStatus;
import springdev.ecomv1.paymentservice.exception.ResourceNotFoundException;
import springdev.ecomv1.paymentservice.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    // Persists a new payment record and returns the confirmed payment details.
    @Transactional
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .status(PaymentStatus.SUCCESS)
                .transactionId("TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8))
                .createdAt(LocalDateTime.now())
                .build();

        Payment saved = paymentRepository.save(payment);

        return PaymentResponse.builder()
                .paymentId(saved.getId())
                .orderId(saved.getOrderId())
                .amount(saved.getAmount())
                .status(saved.getStatus())
                .transactionId(saved.getTransactionId())
                .build();
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .build();
    }
}
