package edu.uoc.epcsd.productcatalog.controllers.dtos;

import edu.uoc.epcsd.productcatalog.entities.Product;
import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
public final class GetProductResponse {

    private final Long id;

    private final String name;

    private final String description;

    private final Double dailyPrice;

    private final String brand;

    private final String model;

    private final String categoryName;

    // Atributo donde guardaremos el número de unidades de un producto
    private final long units;

    public static GetProductResponse fromDomain(Product product) {
        return GetProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .dailyPrice(product.getDailyPrice())
                .brand(product.getBrand())
                .model(product.getModel())
                .categoryName(product.getCategory().getName())
                .build();
    }

    // Método para devolver el detalle de un producto (se añade el número de unidades)
    public static GetProductResponse detailed(Product product) {
        return GetProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .dailyPrice(product.getDailyPrice())
                .brand(product.getBrand())
                .model(product.getModel())
                .categoryName(product.getCategory().getName())
                .units(product.getItemList().size())
                .build();
    }
}
