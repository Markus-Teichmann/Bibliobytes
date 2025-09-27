package com.bibliobytes.backend.rentals;

import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.items.items.entities.Item;
import com.bibliobytes.backend.rentals.entities.Rental;
import com.bibliobytes.backend.rentals.entities.RentalState;
import com.bibliobytes.backend.users.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RentalRepository extends CrudRepository<Rental, Long> {
    Optional<Rental> findByDonation(Donation donation);
    @Query("select count(r) from Rental r, Donation d where d.item = :item and d = r.donation and r.status = :state")
    int countByStatusAndItem(@Param("state") RentalState state, @Param("item") Item item);
    @Query("select r.id from Rental r")
    Set<Long> findAllIds();
    @Query("select r from Rental r where r.user = :user")
    Set<Rental> findAllRentalsByUser(@Param("user") User user);
}
