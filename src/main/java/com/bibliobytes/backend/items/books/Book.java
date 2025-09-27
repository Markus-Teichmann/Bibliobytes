package com.bibliobytes.backend.items.books;

import com.bibliobytes.backend.items.items.entities.Item;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "books")
public class Book extends Item {
    @NotNull
    @Column(name = "publisher", nullable = false, length = 200)
    private String publisher;

    @NotNull
    @Column(name = "isbn", nullable = false, length = 13)
    private String isbn;

    @NotNull
    @Column(name = "author", nullable = false)
    private String author;
}