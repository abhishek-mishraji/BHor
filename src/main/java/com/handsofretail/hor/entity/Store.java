package com.handsofretail.hor.entity;

import com.handsofretail.hor.entity.base.BaseEntity;
import com.handsofretail.hor.enums.Status;
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
@Table(name = "stores")
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientUser client;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "store_code", nullable = false, unique = true)
    private String storeCode;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "contact_number")
    private String contactNumber;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyReport> dailyReports = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MonthlyReport> monthlyReports = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<YearlyReport> yearlyReports = new ArrayList<>();
}