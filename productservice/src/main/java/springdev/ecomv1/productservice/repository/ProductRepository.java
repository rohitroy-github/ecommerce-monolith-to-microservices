package springdev.ecomv1.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springdev.ecomv1.productservice.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
