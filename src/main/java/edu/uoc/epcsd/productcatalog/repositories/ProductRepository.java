package edu.uoc.epcsd.productcatalog.repositories;

import edu.uoc.epcsd.productcatalog.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameLikeIgnoreCase(String name);
    List<Product> findByDescriptionLikeIgnoreCase(String description);
    List<Product> findByBrandLikeIgnoreCase(String brand);
    List<Product> findByModelLikeIgnoreCase(String model);
    List<Product> findByCategoryNameLikeIgnoreCase(String category);
}
