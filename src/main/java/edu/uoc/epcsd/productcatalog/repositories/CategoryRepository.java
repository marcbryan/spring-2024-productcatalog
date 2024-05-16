package edu.uoc.epcsd.productcatalog.repositories;

import edu.uoc.epcsd.productcatalog.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByNameLikeIgnoreCase(String name);
    List<Category> findByDescriptionLikeIgnoreCase(String description);
    List<Category> findByParentId(long parentId);
}
