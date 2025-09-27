package com.bibliobytes.backend.rentals.entities;

import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.users.entities.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "rentals")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "donation_id", nullable = false)
    private Donation donation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ColumnDefault("(curdate())")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @ColumnDefault("((curdate() + interval 4 week))")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "status", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private RentalState status;

    public void setStatus(RentalState state) {
        if (
                this.status == RentalState.REQUESTED &&
                state == RentalState.APPROVED
        ) {
            donation.getItem().decrementStock();
            donation.getItem().incrementCount();
        }
        if (
            this.status == RentalState.APPROVED &&
            (state == RentalState.DENIED || state == RentalState.RETURNED)
        ) {
            donation.getItem().incrementStock();
        }
        this.status = state;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "external_id")
    private User external;

    @PrePersist
    protected void setDefaults() {
        if (status == null) {
            status = RentalState.REQUESTED;
        }
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = LocalDate.now().plusWeeks(4);
        }
    }

}