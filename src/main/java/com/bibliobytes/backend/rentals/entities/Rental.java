package com.bibliobytes.backend.rentals.entities;

import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.items.entities.Item;
import com.bibliobytes.backend.users.entities.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "external_id")
    private User external;

}