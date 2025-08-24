package com.bibliobytes.backend.items;

import com.bibliobytes.backend.donations.DonationRepository;
import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.donations.entities.DonationState;
import com.bibliobytes.backend.items.entities.Item;
import com.bibliobytes.backend.rentals.RentalRepository;
import com.bibliobytes.backend.rentals.entities.Rental;
import com.bibliobytes.backend.rentals.entities.RentalState;
import com.bibliobytes.backend.users.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class ItemService {
    private DonationRepository donationRepository;
    private RentalRepository rentalRepository;

    public List<User> getOwners(Item item) {
        List<Donation> donations = donationRepository.findAllByItem(item);
        List<User> owners = new ArrayList<>();
        for (Donation donation : donations) {
            owners.add(donation.getOwner());
        }
        return owners;
    }

    public int getStock(Item item) {
        int stock = 0;
        for (Donation donation : donationRepository.findAllByItem(item)) {
            if (donation.getStatus() == DonationState.ACCEPTED) {
                stock += donation.getAmount();
            }
        }
        for (Rental rental : rentalRepository.findAllByItem(item)) {
            if (rental.getStatus() == RentalState.APPROVED) {
                stock--;
            }
        }
        return stock;
    }


}
