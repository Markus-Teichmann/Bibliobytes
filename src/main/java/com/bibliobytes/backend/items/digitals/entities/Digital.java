package com.bibliobytes.backend.items.digitals.entities;

import com.bibliobytes.backend.items.items.entities.Item;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
            joinColumns = @JoinColumn(name = "digitals_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private Set<Actor> actors = new HashSet<>();
    public void addActor(Actor actor) {
        if (!actors.contains(actor)) {
            actors.add(actor);
            actor.addDigital(this);
        }
    }
    public void removeActor(Actor actor) {
        if (actors.contains(actor)) {
            actors.remove(actor);
            actor.removeDigital(this);
        }
    }

    @ManyToMany
    @JoinTable(
            name = "digitals_languages",
            joinColumns = @JoinColumn(name = "digitals_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    private Set<Language> languages = new HashSet<>();
    public void addLanguage(Language language) {
        if (!languages.contains(language)) {
            languages.add(language);
            language.addDigital(this);
        }
    }
    public void removeLanguage(Language language) {
        if (languages.contains(language)) {
            languages.remove(language);
            language.removeDigital(this);
        }
    }

    @ManyToMany
    @JoinTable(
            name = "digitals_subtitles",
            joinColumns = @JoinColumn(name = "digitals_id"),
            inverseJoinColumns = @JoinColumn(name = "subtitles_id")
    )
    private Set<Subtitle> subtitles = new HashSet<>();
    public void addSubtitle(Subtitle subtitle) {
        if (!subtitles.contains(subtitle)) {
            subtitles.add(subtitle);
            subtitle.addDigital(this);
        }
    }
    public void removeSubtitle(Subtitle subtitle) {
        if (subtitles.contains(subtitle)) {
            subtitles.remove(subtitle);
            subtitle.removeDigital(this);
        }
    }

}