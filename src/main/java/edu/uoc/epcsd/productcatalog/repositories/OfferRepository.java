package edu.uoc.epcsd.productcatalog.repositories;

import edu.uoc.epcsd.productcatalog.entities.Offer;
import edu.uoc.epcsd.productcatalog.entities.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    @Modifying
    @Query("UPDATE Offer SET status = ?2, date = ?3 WHERE id = ?1")
    int evaluateOffer(long offerId, OfferStatus status, Date date);
    Optional<Offer> findByEmail(String email);
}
