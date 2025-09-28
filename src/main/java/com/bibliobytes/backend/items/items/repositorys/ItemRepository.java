package com.bibliobytes.backend.items.items.repositorys;

import com.bibliobytes.backend.donations.entities.DonationState;
import com.bibliobytes.backend.items.books.Book;
import com.bibliobytes.backend.items.digitals.entities.Digital;
import com.bibliobytes.backend.items.items.entities.Item;
import com.bibliobytes.backend.rentals.entities.RentalState;
import com.bibliobytes.backend.users.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {
    Optional<Item> findById(long id);
    @Query("select b from Book b where b.id = :id")
    Optional<Book> findBookById(@Param("id") long id);
    @Query("select d from Digital d where d.id = :id")
    Optional<Digital> findDigitalById(@Param("id") long id);
    @Query("select i.id from Item i")
    Set<Long> findAllIds();
    @Query("select i from Item i")
    Set<Item> findAllItems();
    @Query("select i from Item i, Donation d where d.status = :state and d.item = i")
    Set<Item> findAllByDonationStatus(@Param("state") DonationState state);
    @Query("select i from Item i, Donation d where d.id = :id and d.item = i")
    Item findByDonationId(@Param("id") long id);
    @Query("select i from Item i, Donation d, Rental r where r.status = :state and r.donation = d and d.item = i")
    Set<Item> findAllByRentalState(@Param("state") RentalState state);
}
