package springdev.ecomv1.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springdev.ecomv1.orderservice.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
