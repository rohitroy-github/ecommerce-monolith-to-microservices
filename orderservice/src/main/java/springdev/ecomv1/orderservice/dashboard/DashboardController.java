package springdev.ecomv1.orderservice.dashboard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import springdev.ecomv1.orderservice.dto.AdminDashboardResponse;
import springdev.ecomv1.orderservice.dto.SellerMetricsResponse;

@RestController
@RequestMapping("/api/orders/dashboard")
@RequiredArgsConstructor
public class DashboardController {

	private final DashboardService dashboardService;

	@GetMapping("/admin/overview")
	public ResponseEntity<AdminDashboardResponse> getAdminDashboard() {
		return ResponseEntity.ok(dashboardService.getAdminDashboard());
	}

	// Dashboard entrypoint for seller metrics; delegates aggregation logic to service layer.
	@GetMapping("/sellers/{sellerId}/metrics")
	public ResponseEntity<SellerMetricsResponse> getSellerMetrics(@PathVariable Long sellerId) {
		return ResponseEntity.ok(dashboardService.getSellerMetrics(sellerId));
	}
}
