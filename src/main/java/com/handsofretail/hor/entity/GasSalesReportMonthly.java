package com.handsofretail.hor.entity;

import com.handsofretail.hor.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "gas_sales_reports_monthly", uniqueConstraints = @UniqueConstraint(name = "uk_gas_sales_reports_monthly_store_period", columnNames = {
        "store_id", "report_month", "report_year" }))
public class GasSalesReportMonthly extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gas_sales_report_monthly_id")
    private Long gasSalesReportMonthlyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "report_month", nullable = false)
    private Integer reportMonth;

    @Column(name = "report_year", nullable = false)
    private Integer reportYear;

    @Column(name = "credit_fees", precision = 19, scale = 4, nullable = false)
    private BigDecimal creditFees;

    @Column(name = "total_volume_sold", precision = 19, scale = 4, nullable = false)
    private BigDecimal totalVolumeSold;

    @Column(name = "net_profit_per_gallon", precision = 19, scale = 4, nullable = false)
    private BigDecimal netProfitPerGallon;

    @Column(name = "net_profit", precision = 19, scale = 4, nullable = false)
    private BigDecimal netProfit;

    @Builder.Default
    @OneToMany(mappedBy = "gasSalesReportMonthly", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GasSalesReportDetail> details = new ArrayList<>();
}