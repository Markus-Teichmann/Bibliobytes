package com.bibliobytes.backend.donations.entities;

import com.bibliobytes.backend.items.entities.Item;
import com.bibliobytes.backend.users.entities.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
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

    @ColumnDefault("1")
    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DonationState status;
}