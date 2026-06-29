package springdev.ecomv1.dataseeder.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import springdev.ecomv1.dataseeder.dto.order.PaymentClientResponse;
import springdev.ecomv1.dataseeder.dto.order.ProcessPaymentRequest;

@Component
public class PaymentClient {

	private final RestClient restClient;

	public PaymentClient(@Qualifier("paymentRestClient") RestClient restClient) {
		this.restClient = restClient;
	}

	public RestClient restClient() {
		return restClient;
	}

	public PaymentClientResponse processPayment(ProcessPaymentRequest request) {
		return restClient.post()
				.uri("/api/payments")
				.body(request)
				.retrieve()
				.body(PaymentClientResponse.class);
	}

	public PaymentClientResponse getPaymentByOrderId(Long orderId) {
		return restClient.get()
				.uri("/api/payments/order/{orderId}", orderId)
				.retrieve()
				.body(PaymentClientResponse.class);
	}

	public PaymentClientResponse getPaymentByOrderId(Long orderId, String jwtToken) {
		return restClient.get()
				.uri("/api/payments/order/{orderId}", orderId)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
				.retrieve()
				.body(PaymentClientResponse.class);
	}

}