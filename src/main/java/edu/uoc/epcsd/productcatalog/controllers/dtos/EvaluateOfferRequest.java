package edu.uoc.epcsd.productcatalog.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter

// He tenido que crear el DTO de esta manera porque @AllArgsConstructor me daba error al deserializar
public final class EvaluateOfferRequest {
    @JsonProperty
    private final String status;

    @JsonCreator
    public EvaluateOfferRequest(String status) {
        this.status = status;
    }
}
