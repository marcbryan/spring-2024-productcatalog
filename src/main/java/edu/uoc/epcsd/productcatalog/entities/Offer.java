package edu.uoc.epcsd.productcatalog.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Date;

@Entity
@ToString
@Getter
@Setter
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dailyPrice", nullable = false)
    private Double dailyPrice;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OfferStatus status;

    @Column(name = "date", nullable = false)
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "serialNumber", nullable = false)
    private String serialNumber;

    @Column(name = "email", nullable = false)
    private String email;
}
