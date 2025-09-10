package com.bibliobytes.backend.rentals;

import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.items.entities.Item;
import com.bibliobytes.backend.rentals.entities.Rental;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends CrudRepository<Rental, Long> {
    Optional<Rental> findByDonation(Donation donation);
}
