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
@Table(name = "yearly_reports")
public class YearlyReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "yearly_report_id")
    private Long yearlyReportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "report_year", nullable = false)
    private Integer reportYear;

    @Column(name = "annual_summary", columnDefinition = "TEXT")
    private String annualSummary;
}