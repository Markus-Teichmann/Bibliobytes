package com.bibliobytes.backend.donations;

import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.donations.entities.DonationState;
import com.bibliobytes.backend.items.items.entities.Item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface DonationRepository extends CrudRepository<Donation, Long> {
    @Query("select d from Donation d")
    Set<Donation> findAllByItem(Item item);
    Set<Long> findAllDonationIdsByOwnerId(UUID ownerId);
    Set<Donation> findAllByOwnerId(UUID ownerId);
    Set<Long> findAllItemIdsByStatus(DonationState state);
    int countByStatusAndItem(DonationState state, Item item);
    @Query("select d.id from Donation d")
    Set<Long> findAllIds();
}
