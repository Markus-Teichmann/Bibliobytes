package com.bibliobytes.backend.items.entities;

import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.donations.entities.DonationState;
import com.bibliobytes.backend.rentals.entities.Rental;
import com.bibliobytes.backend.users.entities.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "items")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "titel", nullable = false, length = 200)
    private String titel;

    @Column(name = "place", nullable = false, length = 100)
    private String place;

    @Column(name = "topic", nullable = false, length = 100)
    private String topic;

    @Column(name = "note", length = 500)
    private String note;

    @ManyToMany
    @JoinTable(
            name = "item_tags",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new LinkedHashSet<>();

    @OneToMany(mappedBy = "item")
    private List<Donation> donations = new ArrayList<>();

    public void donate(Donation donation) {
        donations.add(donation);
        donation.setItem(this);
    }

    @OneToMany(mappedBy = "item")
    private List<Rental> rentals = new ArrayList<>();

    public void addToRental(Rental rental) {
        rentals.add(rental);
        rental.setItem(this);
    }

    public void removeFromRental(Rental rental) {
        rentals.remove(rental);
        rental.setItem(null);
    }
}