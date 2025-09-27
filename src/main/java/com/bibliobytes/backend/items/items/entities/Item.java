package com.bibliobytes.backend.items.items.entities;

import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.items.items.dtos.ItemDto;
import com.bibliobytes.backend.items.items.mappers.ItemMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "items")
@Inheritance(strategy = InheritanceType.JOINED)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "stock")
    private int stock;
    public void incrementStock() {
        stock++;
    }
    public void decrementStock() {
        stock--;
    }

    @Column(name = "rental_count")
    private int rentalCount;
    public void incrementCount() {
        rentalCount++;
    }
//    public void decrementCount() {
//        rentalCount--;
//    }

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private ItemState state;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "item_tags",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
    public void addTag(Tag tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            tag.addItem(this);
        }
    }
    public void removeTag(Tag tag) {
        if (tags.contains(tag)) {
            tags.remove(tag);
            tag.removeItem(this);
        }
    }

    @OneToMany(mappedBy = "item")
    private Set<Donation> donations = new HashSet<>();

    public void donate(Donation donation) {
        donations.add(donation);
        donation.setItem(this);
    }

    @PrePersist
    private void setDefaults() {
        if (state == null) {
            state = ItemState.PUBLIC;
        }
    }
}