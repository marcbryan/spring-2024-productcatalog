package edu.uoc.epcsd.productcatalog.controllers.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class CreateOfferRequest {
    private final double dailyPrice;
    private final String brand;
    private final String model;
    private final String serialNumber;
    private final String email;
}
