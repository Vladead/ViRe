package com.vire.virebackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
