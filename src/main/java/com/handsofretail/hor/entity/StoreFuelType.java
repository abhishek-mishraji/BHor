package com.handsofretail.hor.entity;

import com.handsofretail.hor.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "store_fuel_types", uniqueConstraints = @UniqueConstraint(name = "uk_store_fuel_types_store_fuel_type", columnNames = {
        "store_id", "fuel_type_id" }))
public class StoreFuelType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_fuel_type_id")
    private Long storeFuelTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_type_id", nullable = false)
    private FuelType fuelType;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
}