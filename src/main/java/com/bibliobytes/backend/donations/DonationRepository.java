package com.bibliobytes.backend.donations;

import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.donations.entities.DonationState;
import com.bibliobytes.backend.items.items.entities.Item;
import com.bibliobytes.backend.users.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface DonationRepository extends CrudRepository<Donation, Long> {
    @Query("select d from Donation d where d.item = :item")
    Set<Donation> findAllByItem(@Param("item") Item item);
    @Query("select d.owner from Donation d where d.item = :item and d.status = 'ACCEPTED'")
    Set<User> findAllOwnersByItem(@Param("item") Item item);
    Set<Long> findAllDonationIdsByOwnerId(UUID ownerId);
    Set<Donation> findAllByOwnerId(UUID ownerId);
    Set<Long> findAllItemIdsByStatus(DonationState state);
    int countByStatusAndItem(DonationState state, Item item);
    @Query("select d.id from Donation d")
    Set<Long> findAllIds();
}
