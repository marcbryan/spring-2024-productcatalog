package edu.uoc.epcsd.productcatalog.repositories;

import edu.uoc.epcsd.productcatalog.entities.Item;
import edu.uoc.epcsd.productcatalog.entities.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, String> {

    Optional<Item> findBySerialNumber(String serialNumber);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Item SET status = ?2 WHERE serialNumber = ?1")
    int setOperational(String serialNumber, ItemStatus status);
}
