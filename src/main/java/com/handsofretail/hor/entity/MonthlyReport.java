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
@Table(name = "monthly_reports")
public class MonthlyReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "monthly_report_id")
    private Long monthlyReportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "report_month", nullable = false)
    private Integer reportMonth;

    @Column(name = "report_year", nullable = false)
    private Integer reportYear;

    @Column(name = "department_id")
    private String departmentId;

    @Column(name = "department_name")
    private String departmentName;

    @Column(precision = 15, scale = 2)
    private BigDecimal gross;
    @Column(precision = 15, scale = 2)
    private BigDecimal discount;

    @Column(precision = 15, scale = 2)
    private BigDecimal promotion;

    @Column(precision = 15, scale = 2)
    private BigDecimal refund;

    @Column(name = "void_amount", precision = 15, scale = 2)
    private BigDecimal voidAmount;

    @Column(name = "net_sales", precision = 15, scale = 2)
    private BigDecimal netSales;
}