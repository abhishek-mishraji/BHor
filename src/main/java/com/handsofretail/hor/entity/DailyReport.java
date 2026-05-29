package com.handsofretail.hor.entity;

import com.handsofretail.hor.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "daily_reports")
public class DailyReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_report_id")
    private Long dailyReportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "grocery_total", precision = 15, scale = 2)
    private BigDecimal groceryTotal;

    @Column(precision = 15, scale = 2)
    private BigDecimal volume;

    @Column(name = "cash_deposit", precision = 15, scale = 2)
    private BigDecimal cashDeposit;

    @Column(name = "check_deposit", precision = 15, scale = 2)
    private BigDecimal checkDeposit;
    @Column(name = "over_short", precision = 15, scale = 2)
    private BigDecimal overShort;
}