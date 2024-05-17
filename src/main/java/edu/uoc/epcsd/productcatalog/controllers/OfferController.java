package edu.uoc.epcsd.productcatalog.controllers;

import edu.uoc.epcsd.productcatalog.controllers.dtos.CreateOfferRequest;
import edu.uoc.epcsd.productcatalog.controllers.dtos.EvaluateOfferRequest;
import edu.uoc.epcsd.productcatalog.entities.Offer;
import edu.uoc.epcsd.productcatalog.entities.OfferStatus;
import edu.uoc.epcsd.productcatalog.services.OfferService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.net.URI;

@Log4j2
@RestController
@RequestMapping("/offers")
public class OfferController {

    @Autowired
    private OfferService offerService;

    @PostMapping
    public ResponseEntity<Long> addOffer(@RequestBody CreateOfferRequest createOfferRequest) {
        log.trace("addOffer");

        log.trace("Creating offer {}", createOfferRequest);

        Offer offer = offerService.addOffer(
                createOfferRequest.getDailyPrice(),
                createOfferRequest.getBrand(),
                createOfferRequest.getModel(),
                createOfferRequest.getSerialNumber(),
                createOfferRequest.getEmail());

        if (offer != null) {
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(offer.getId())
                    .toUri();

            return ResponseEntity.created(uri).body(offer.getId());
        }
        else
            return new ResponseEntity<>(0L, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{offerId}")
    public ResponseEntity<OfferStatus> evaluateOffer(@PathVariable @NotNull Long offerId, @RequestBody EvaluateOfferRequest evaluateOfferRequest) {
        log.trace("evaluateOffer");

        String status = evaluateOfferRequest.getStatus();
        // Comprobamos si el estado enviado es correcto, si no devolvemos -1 (indica error)
        if (!status.equals(OfferStatus.ACCEPTED.name()) && !status.equals(OfferStatus.REJECTED.name()))
            return ResponseEntity.badRequest().build();

        // Asignamos el estado
        OfferStatus offerStatus = null;
        if (status.equals(OfferStatus.ACCEPTED.name()))
            offerStatus = OfferStatus.ACCEPTED;
        else
            offerStatus = OfferStatus.REJECTED;

        log.trace("updating offer, newStatus={}", status);

        int affectedRows = offerService.evaluateOffer(offerId, offerStatus);

        if (affectedRows > 0)
            return new ResponseEntity<>(offerStatus, HttpStatus.OK);
        else if (affectedRows == -1)
            return ResponseEntity.badRequest().build();
        else
            return ResponseEntity.notFound().build();
    }

    @GetMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Offer> getOffersByUser(@PathVariable @NotNull String email) {
        log.trace("getOffersByUser");

        return offerService.findByEmail(email).map(offer -> ResponseEntity.ok().body(offer))
                .orElse(ResponseEntity.notFound().build());
    }
}
