package com.handsofretail.hor.entity;

import com.handsofretail.hor.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "gas_sales_report_details", uniqueConstraints = @UniqueConstraint(name = "uk_gas_sales_report_details_report_fuel_type", columnNames = {
        "gas_sales_report_monthly_id", "fuel_type_id" }))
public class GasSalesReportDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gas_sales_report_detail_id")
    private Long gasSalesReportDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gas_sales_report_monthly_id", nullable = false)
    private GasSalesReportMonthly gasSalesReportMonthly;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_type_id", nullable = false)
    private FuelType fuelType;

    @Column(name = "volume_sold", precision = 19, scale = 4, nullable = false)
    private BigDecimal volumeSold;

    @Column(name = "profit_per_gallon", precision = 19, scale = 4, nullable = false)
    private BigDecimal profitPerGallon;
}