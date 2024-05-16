package edu.uoc.epcsd.productcatalog.controllers.dtos;

import edu.uoc.epcsd.productcatalog.entities.Category;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
public final class GetCategoryResponse {
    private final Long id;
    private final String name;
    private final String description;
    private final String parent;
    private final List<String> children;

    public static GetCategoryResponse fromDomain(Category category) {
        String parent = null;
        // Comprobamos si tiene categoría padre
        if (category.getParent() != null)
            parent = category.getParent().getName();

        List<String> childrens = null;
        // Comprobamos si tiene hijos
        if (!category.getChildren().isEmpty()) {
            childrens = new ArrayList<>();
            for (Category children : category.getChildren())
                childrens.add(children.getName());
        }

        return GetCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parent(parent)
                .children(childrens)
                .build();
    }
}
