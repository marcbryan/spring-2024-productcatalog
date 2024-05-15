package edu.uoc.epcsd.productcatalog.services;

import edu.uoc.epcsd.productcatalog.entities.Category;
import edu.uoc.epcsd.productcatalog.entities.ItemStatus;
import edu.uoc.epcsd.productcatalog.entities.Product;
import edu.uoc.epcsd.productcatalog.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long productId) {
        return productRepository.findById(productId);
    }

    public Product createProduct(Long categoryId, String name, String description, Double dailyPrice, String brand, String model) {

        Product product = Product.builder().name(name).description(description).dailyPrice(dailyPrice).brand(brand).model(model).build();

        if (categoryId != null) {
            Optional<Category> category = categoryService.findById(categoryId);

            if (category.isPresent()) {
                product.setCategory(category.get());
            }
        }

        return productRepository.save(product);
    }

    public List<Product> findByNameLikeIgnoreCase(String name) {
        return productRepository.findByNameLikeIgnoreCase("%" + name + "%");
    }

    public List<Product> findByDescriptionLikeIgnoreCase(String description) {
        return productRepository.findByDescriptionLikeIgnoreCase("%" + description + "%");
    }

    public List<Product> findByBrandLikeIgnoreCase(String brand) {
        return productRepository.findByBrandLikeIgnoreCase("%" + brand + "%");
    }

    public List<Product> findByModelLikeIgnoreCase(String model) {
        return productRepository.findByModelLikeIgnoreCase("%" + model + "%");
    }

    public List<Product> findByCategoryNameLikeIgnoreCase(String category) {
        return productRepository.findByCategoryNameLikeIgnoreCase("%" + category + "%");
    }

    @Transactional
    public int deleteProduct(int productId) {
        return productRepository.deleteProduct(productId, ItemStatus.NOT_OPERATIONAL.name());
    }
}
