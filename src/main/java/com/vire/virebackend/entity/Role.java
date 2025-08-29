package com.vire.virebackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", length = 50)
    private String name;

    /**
     * Jpa hook to make sure all roles are in upper case
     */
    @PrePersist
    @PreUpdate
    private void normalize() {
        if (name != null) name = name.trim().toUpperCase();
    }
}
