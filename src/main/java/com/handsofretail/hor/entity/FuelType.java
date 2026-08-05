package com.handsofretail.hor.entity;

import com.handsofretail.hor.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "fuel_types", uniqueConstraints = @UniqueConstraint(name = "uk_fuel_types_name", columnNames = "fuel_name"))
public class FuelType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fuel_type_id")
    private Long fuelTypeId;

    @Column(name = "fuel_name", nullable = false)
    private String fuelName;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Builder.Default
    @OneToMany(mappedBy = "fuelType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreFuelType> storeFuelTypes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "fuelType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GasSalesReportDetail> gasSalesReportDetails = new ArrayList<>();
}