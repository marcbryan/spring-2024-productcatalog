package edu.uoc.epcsd.productcatalog.services;

import edu.uoc.epcsd.productcatalog.entities.Item;
import edu.uoc.epcsd.productcatalog.entities.ItemStatus;
import edu.uoc.epcsd.productcatalog.entities.Product;
import edu.uoc.epcsd.productcatalog.kafka.KafkaConstants;
import edu.uoc.epcsd.productcatalog.kafka.ProductMessage;
import edu.uoc.epcsd.productcatalog.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private KafkaTemplate<String, ProductMessage> productKafkaTemplate;

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findBySerialNumber(String serialNumber) {
        return itemRepository.findBySerialNumber(serialNumber);
    }

    @Transactional
    public Item setOperational(String serialNumber, @RequestBody Boolean operational) {
        Optional<Item> item = itemRepository.findBySerialNumber(serialNumber);

        // Comprobamos si la unidad existe
        if (item.isEmpty())
            return null;
        else {
            int affectedRows = -1;

            if (operational)
                affectedRows = itemRepository.setOperational(serialNumber, ItemStatus.OPERATIONAL);
            else
                affectedRows = itemRepository.setOperational(serialNumber, ItemStatus.NOT_OPERATIONAL);

            if (affectedRows > 0) {
                item = itemRepository.findBySerialNumber(serialNumber);
                if (item.isPresent()) {
                    if (operational) {
                        long productId = item.get().getProduct().getId();
                        // Enviamos el mensaje UNIT_AVAILABLE a la cola de mensajes de Kafka
                        productKafkaTemplate.send(KafkaConstants.PRODUCT_TOPIC + KafkaConstants.SEPARATOR + KafkaConstants.UNIT_AVAILABLE, ProductMessage.builder().productId(productId).build());
                    }

                    return item.get();
                }
            }

            return null;
        }
    }

    public Item createItem(Long productId, String serialNumber) {

        // by default a new unit is OPERATIONAL
        Item item = Item.builder().serialNumber(serialNumber).status(ItemStatus.OPERATIONAL).build();

        Optional<Product> product = productService.findById(productId);

        if (product.isPresent()) {
            item.setProduct(product.get());
        } else {
            throw new IllegalArgumentException("Could not find the product with Id: " + productId);
        }

        Item savedItem = itemRepository.save(item);

        productKafkaTemplate.send(KafkaConstants.PRODUCT_TOPIC + KafkaConstants.SEPARATOR + KafkaConstants.UNIT_AVAILABLE, ProductMessage.builder().productId(productId).build());

        return savedItem;
    }
}
