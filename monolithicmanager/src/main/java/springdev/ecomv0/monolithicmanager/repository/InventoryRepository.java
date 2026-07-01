package springdev.ecomv0.monolithicmanager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import springdev.ecomv0.monolithicmanager.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    Optional<Inventory> findByProductId(Long productId);
    

}
