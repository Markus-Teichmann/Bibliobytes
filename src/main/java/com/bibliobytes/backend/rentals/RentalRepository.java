package com.bibliobytes.backend.rentals;

import com.bibliobytes.backend.items.entities.Item;
import com.bibliobytes.backend.rentals.entities.Rental;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends CrudRepository<Rental, Long> {
    List<Rental> findAllByItem(Item item);
}
