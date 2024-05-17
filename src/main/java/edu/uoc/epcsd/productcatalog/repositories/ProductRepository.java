package edu.uoc.epcsd.productcatalog.repositories;

import edu.uoc.epcsd.productcatalog.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameLikeIgnoreCase(String name);
    List<Product> findByDescriptionLikeIgnoreCase(String description);
    List<Product> findByBrandLikeIgnoreCase(String brand);
    List<Product> findByModelLikeIgnoreCase(String model);
    List<Product> findByCategoryNameLikeIgnoreCase(String category);

    @Modifying
    @Query(value="UPDATE item SET status = ?2 WHERE product_id = ?1",
           nativeQuery = true)
    int deleteProduct(int productId, String status);

    Optional<Product> findByBrandAndModel(String brand, String model);
}
