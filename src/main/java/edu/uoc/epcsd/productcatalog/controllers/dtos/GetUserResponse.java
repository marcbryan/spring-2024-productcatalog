package edu.uoc.epcsd.productcatalog.controllers.dtos;

import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public final class GetUserResponse {
    private final Long id;

    private final String fullName;

    private final String email;

    private final String phoneNumber;
}
