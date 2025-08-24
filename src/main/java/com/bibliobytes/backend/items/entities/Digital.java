package com.bibliobytes.backend.items.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "digitals")
public class Digital extends Item {
    @Column(name = "runtime", length = 20)
    private String runtime;

    @Column(name = "label", length = 100)
    private String label;

    @Column(name = "production", length = 100)
    private String production;

    @ManyToMany
    @JoinTable(
            name = "digitals_actors",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private Set<Actor> actors = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "digitals_languages",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    private Set<Language> languages = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "digitals_subtitles",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "subtitles_id")
    )
    private Set<Subtitle> subtitles = new HashSet<>();
}