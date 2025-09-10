package com.bibliobytes.backend.donations;

import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.items.entities.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends CrudRepository<Donation, Long> {
    List<Donation> findAllByItem(Item item);
    int countAllByItem(Item item);
}
