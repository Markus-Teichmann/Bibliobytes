package com.bibliobytes.backend.users.entities;

import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.donations.entities.DonationState;
import com.bibliobytes.backend.rentals.entities.Rental;
import com.bibliobytes.backend.rentals.entities.RentalState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @NotNull
    @Column(name = "email")
    private String email;

    @NotNull
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    public void setPassword(String password) {
        if (role == Role.EXTERNAL || role == null) {
            role = Role.APPLICANT;
        }
        this.password = password;
    }

    public void setRole(Role role) {
        if (role == Role.EXTERNAL) {
            password = null;
            this.role = role;
        } else if (password != null) { // Dieser Code ist in dem Fall wichtig, wenn ich einen Nutzer ohne passwort habe, gerade eine Rolle vergeben wird.
            this.role = role;
        }
    }


    @OneToMany(mappedBy = "owner")
    private Set<Donation> donations = new HashSet<>();

    public void donate(Donation donation) {
        donations.add(donation);
        donation.setOwner(this);
        donation.setStatus(DonationState.APPLIED);
    }

    public void withdrawDonation(Donation donation) {
        donation.setStatus(DonationState.WITHDRAWN);
    }

    @OneToMany(mappedBy = "user")
    private Set<Rental> rentals = new HashSet<>();

    public void rent(Rental rental) {
        rentals.add(rental);
        rental.setUser(this);
        rental.setStatus(RentalState.REQUESTED);
        rental.setExternal(null);
    }

    public void rent(Rental rental, User external) {
        rentals.add(rental);
        rental.setUser(this);
        rental.setStatus(RentalState.REQUESTED);
        rental.setExternal(external);
    }

    public void returnRental(Rental rental) {
        rentals.remove(rental);
        rental.setUser(null);
        rental.setExternal(null);
        rental.setStatus(RentalState.RETURNED);
    }
}