package com.bibliobytes.backend.entities;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "internals")
public class Internal {
    @Id
    @Size(max = 16)
    @Column(name = "external_id", nullable = false, length = 16)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "external_id")
    private final External external;

    public Internal() {
        this.external = new External();
    }

    public Internal(External external) {
        this.external = external;
    }

    @NotNull
    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @PostConstruct
    private void init() {
        this.id = external.getId();
    }

}