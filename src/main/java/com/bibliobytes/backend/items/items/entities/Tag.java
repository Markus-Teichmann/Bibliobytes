package com.bibliobytes.backend.items.items.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 20)
    @NotNull
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Item> items = new HashSet<Item>();
    public void addItem(Item item) {
        if (!items.contains(item)) {
            items.add(item);
            item.addTag(this);
        }
    }
    public void removeItem(Item item) {
        if (items.contains(item)) {
            items.remove(item);
            item.removeTag(this);
        }
    }
}