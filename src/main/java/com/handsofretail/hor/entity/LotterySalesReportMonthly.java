package com.handsofretail.hor.entity;

import com.handsofretail.hor.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "lottery_sales_reports_monthly")
public class LotterySalesReportMonthly extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lottery_sales_report_monthly_id")
    private Long lotterySalesReportMonthlyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "report_month", nullable = false)
    private Integer reportMonth;

    @Column(name = "report_year", nullable = false)
    private Integer reportYear;

    @Column(name = "online_sales", precision = 15, scale = 2, nullable = false)
    private BigDecimal onlineSales;

    @Column(name = "scratch_off_sales", precision = 15, scale = 2, nullable = false)
    private BigDecimal scratchOffSales;

    @Column(name = "online_cashes", precision = 15, scale = 2, nullable = false)
    private BigDecimal onlineCashes;

    @Column(name = "scratch_off_cashes", precision = 15, scale = 2, nullable = false)
    private BigDecimal scratchOffCashes;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal commission;
}