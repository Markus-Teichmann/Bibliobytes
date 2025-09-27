package com.bibliobytes.backend.items.digitals.entities;

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
@Table(name = "subtitles")
public class Subtitle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "language", nullable = false, length = 100)
    private String language;

    @ManyToMany(mappedBy = "subtitles")
    private Set<Digital> digitals = new HashSet<Digital>();
    public void addDigital(Digital digital) {
        if (!digitals.contains(digital)) {
            digitals.add(digital);
            digital.addSubtitle(this);
        }
    }
    public void removeDigital(Digital digital) {
        if (digitals.contains(digital)) {
            digitals.remove(digital);
            digital.removeSubtitle(this);
        }
    }

}