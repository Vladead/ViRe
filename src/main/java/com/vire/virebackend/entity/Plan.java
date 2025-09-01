package com.vire.virebackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "plans")
public class Plan extends BaseEntity {

    @Column(name = "name", unique = true, nullable = false, length = 64)
    private String name;

    @DecimalMin(value = "0.00")
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Positive
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;
}
