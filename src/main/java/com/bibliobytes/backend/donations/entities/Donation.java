package com.bibliobytes.backend.donations.entities;

import com.bibliobytes.backend.items.entities.Item;
import com.bibliobytes.backend.users.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "donations")
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "donation_condition")
    @Enumerated(EnumType.STRING)
    private Condition condition;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DonationState status;

    @Column(name = "donation_date")
    private LocalDate date;

    @PrePersist
    public void setDefaults() {
        if (date == null) {
            date = LocalDate.now();
        }
        if (status == null) {
            status = DonationState.APPLIED;
        }
    }
}