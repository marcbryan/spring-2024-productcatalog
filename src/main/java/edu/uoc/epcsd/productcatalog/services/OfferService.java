package edu.uoc.epcsd.productcatalog.services;

import edu.uoc.epcsd.productcatalog.controllers.dtos.GetUserResponse;
import edu.uoc.epcsd.productcatalog.entities.Item;
import edu.uoc.epcsd.productcatalog.entities.Offer;
import edu.uoc.epcsd.productcatalog.entities.OfferStatus;
import edu.uoc.epcsd.productcatalog.entities.Product;
import edu.uoc.epcsd.productcatalog.repositories.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@Service
public class OfferService {
    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ItemService itemService;

    @Value("${userService.getUserByEmail.url}")
    private String userServiceUrl;

    public Optional<Offer> findById(Long offerId) {
        return offerRepository.findById(offerId);
    }

    public Offer addOffer(Double dailyPrice, String brand, String model, String serialNumber, String email) {
        // No necesitamos pasar como parámetros ni el estado ni la fecha de creación de la oferta, se asignarán más adelante

        // Utilizamos try/catch porque cuando la petición devuelve 404 salta la excepción HttpClientErrorException.NotFound
        ResponseEntity<GetUserResponse> getUserResponseEntity;
        try {
            getUserResponseEntity = new RestTemplate().getForEntity(userServiceUrl, GetUserResponse.class, email);
        } catch (HttpClientErrorException.NotFound ex) {
            // El usuario no existe
            return null;
        }

        // Comprobamos si existe el usuario (por su email)
        if (getUserResponseEntity.hasBody()) {
            // Comprobamos si existe el producto por marca y modelo
            Optional<Product> product = productService.findByBrandAndModel(brand, model);
            if (product.isPresent()) {
                Offer offer = Offer.builder().dailyPrice(dailyPrice).brand(brand).model(model).serialNumber(serialNumber).email(email).build();
                // Asignamos la fecha y hora actual y el estado 'Pendiente', que es el estado por defecto al crear una oferta
                offer.setDate(new Date());
                offer.setStatus(OfferStatus.PENDING);

                return offerRepository.save(offer);
            }
        }

        // El producto no existe
        return null;
    }

    @Transactional
    public int evaluateOffer(long offerId, OfferStatus status) {
        Optional<Offer> offer = offerRepository.findById(offerId);
        // Comprobamos si la oferta existe
        if (offer.isPresent()) {
            // Comprobamos si el nuevo estado de la oferta es aceptado
            if (status == OfferStatus.ACCEPTED) {
                Optional<Product> product = productService.findByBrandAndModel(offer.get().getBrand(), offer.get().getModel());
                if (product.isPresent()) {
                    long productId = product.get().getId();
                    // Añadimos la unidad de la oferta de segunda mano
                    Item item = itemService.createItem(productId, offer.get().getSerialNumber());

                    // Comprobamos si se ha insertado correctamente
                    if (item == null)
                        return -1;
                }
                else
                    return -1;
            }

            // Actualizamos el estado y la fecha de la oferta
            return offerRepository.evaluateOffer(offerId, status, new Date());
        }
        // Si no existe la oferta, devolvemos 0
        else
            return 0;
    }

    public Optional<Offer> findByEmail(String email) {
        return offerRepository.findByEmail(email);
    }
}
