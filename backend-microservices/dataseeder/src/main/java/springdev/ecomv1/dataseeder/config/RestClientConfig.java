package springdev.ecomv1.dataseeder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

	@Bean("userRestClient")
	RestClient userRestClient(
			RestClient.Builder builder,
			@Value("${services.user.url:http://localhost:8080}") String baseUrl) {
		return builder.baseUrl(baseUrl).build();
	}

	@Bean("productRestClient")
	RestClient productRestClient(
			RestClient.Builder builder,
			@Value("${services.product.url:http://localhost:8080}") String baseUrl) {
		return builder.baseUrl(baseUrl).build();
	}

	@Bean("orderRestClient")
	RestClient orderRestClient(
			RestClient.Builder builder,
			@Value("${services.order.url:http://localhost:8080}") String baseUrl) {
		return builder.baseUrl(baseUrl).build();
	}

	@Bean("paymentRestClient")
	RestClient paymentRestClient(
			RestClient.Builder builder,
			@Value("${services.payment.url:http://localhost:8080}") String baseUrl) {
		return builder.baseUrl(baseUrl).build();
	}
}