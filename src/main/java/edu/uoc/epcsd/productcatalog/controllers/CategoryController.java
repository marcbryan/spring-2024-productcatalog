package edu.uoc.epcsd.productcatalog.controllers;

import edu.uoc.epcsd.productcatalog.controllers.dtos.CreateCategoryRequest;
import edu.uoc.epcsd.productcatalog.controllers.dtos.GetCategoryResponse;
import edu.uoc.epcsd.productcatalog.entities.Category;
import edu.uoc.epcsd.productcatalog.services.CategoryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<Category> getAllCategories() {
        log.trace("getAllCategories");

        return categoryService.findAll();
    }

    @PostMapping
    public ResponseEntity<Long> createCategory(@RequestBody CreateCategoryRequest createCategoryRequest) {
        log.trace("createCategory");

        log.trace("Creating category " + createCategoryRequest);
        Long categoryId = categoryService.createCategory(
                createCategoryRequest.getParentId(),
                createCategoryRequest.getName(),
                createCategoryRequest.getDescription()).getId();
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoryId)
                .toUri();

        return ResponseEntity.created(uri).body(categoryId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<GetCategoryResponse>> findCategoriesByCriteria(@RequestParam(required = false) String name, @RequestParam(required = false) String description, @RequestParam(required = false) Long parentCategory) {
        log.trace("findCategoriesByCriteria");

        // 1. query categories by name
        if (name != null) {
            List<Category> categories = categoryService.findByNameLikeIgnoreCase("%" + name + "%");
            return generateCategoryListResponse(categories);
        }
        // 2. query categories by description
        else if (description != null) {
            List<Category> categories = categoryService.findByDescriptionLikeIgnoreCase("%" + description + "%");
            return generateCategoryListResponse(categories);
        }
        // 3. query categories by parent category (must return all categories under the category specified by the id parameter)
        else if (parentCategory != null) {
            List<Category> categories = categoryService.findByParentId(parentCategory);
            return generateCategoryListResponse(categories);
        }
        else
            // No ha enviado ningún parámetro
            return ResponseEntity.badRequest().build();
    }


    // Método para generar la respuesta que se enviará
    private ResponseEntity<List<GetCategoryResponse>> generateCategoryListResponse(List<Category> categories) {
        if (categories.isEmpty())
            return ResponseEntity.notFound().build();
        else {
            List<GetCategoryResponse> response = new ArrayList<>();
            for (Category category : categories)
                response.add(GetCategoryResponse.fromDomain(category));

            return new ResponseEntity<List<GetCategoryResponse>>(response, HttpStatus.OK);
        }
    }
}
