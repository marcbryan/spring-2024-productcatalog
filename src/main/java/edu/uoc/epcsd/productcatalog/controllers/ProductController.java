package edu.uoc.epcsd.productcatalog.controllers;


import edu.uoc.epcsd.productcatalog.controllers.dtos.CreateProductRequest;
import edu.uoc.epcsd.productcatalog.controllers.dtos.GetProductResponse;
import edu.uoc.epcsd.productcatalog.entities.Product;
import edu.uoc.epcsd.productcatalog.services.ProductService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<Product> getAllProducts() {
        log.trace("getAllProducts");

        return productService.findAll();
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GetProductResponse> getProductById(@PathVariable @NotNull Long productId) {
        log.trace("getProductById");

        return productService.findById(productId).map(product -> ResponseEntity.ok().body(GetProductResponse.fromDomain(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Long> createProduct(@RequestBody CreateProductRequest createProductRequest) {
        log.trace("createProduct");

        log.trace("Creating product " + createProductRequest);
        Long productId = productService.createProduct(
                createProductRequest.getCategoryId(),
                createProductRequest.getName(),
                createProductRequest.getDescription(),
                createProductRequest.getDailyPrice(),
                createProductRequest.getBrand(),
                createProductRequest.getModel()).getId();
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productId)
                .toUri();

        return ResponseEntity.created(uri).body(productId);
    }

    // TODO: add the code for the missing system operations here:
    // 1. remove product (use DELETE HTTP verb). Must remove the associated items

    // 2. query products by name. He añadido los diferentes criterios como se indica en la PRA1
    @GetMapping("/search")
    public ResponseEntity<List<GetProductResponse>> findProductsByCriteria(@RequestParam(required = false) String name, @RequestParam(required = false) String description, @RequestParam(required = false) String brand, @RequestParam(required = false) String model, @RequestParam(required = false) String category) {
        log.trace("findProductsByCriteria");

        if (name != null) {
            List<Product> products = productService.findByNameLikeIgnoreCase(name);
            return generateProductListResponse(products);
        }
        else if (description != null) {
            List<Product> products = productService.findByDescriptionLikeIgnoreCase(description);
            return generateProductListResponse(products);
        }
        else if (brand != null) {
            List<Product> products = productService.findByBrandLikeIgnoreCase(brand);
            return generateProductListResponse(products);
        }
        else if (model != null) {
            List<Product> products = productService.findByModelLikeIgnoreCase(model);
            return generateProductListResponse(products);
        }
        // 3. query products by category/subcategory
        else if (category != null) {
            List<Product> products = productService.findByCategoryNameLikeIgnoreCase(category);
            return generateProductListResponse(products);
        }
        else
            // No ha enviado ningún parámetro
            return ResponseEntity.badRequest().build();
    }


    // Método para generar la respuesta que se enviará
    private ResponseEntity<List<GetProductResponse>> generateProductListResponse(List<Product> products) {
        if (products.isEmpty())
            return ResponseEntity.notFound().build();
        else {
            List<GetProductResponse> response = new ArrayList<GetProductResponse>();
            for (Product product : products)
                response.add(GetProductResponse.fromDomain(product));

            return new ResponseEntity<List<GetProductResponse>>(response, HttpStatus.OK);
        }
    }
}
