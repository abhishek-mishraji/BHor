package com.handsofretail.hor.entity;

import com.handsofretail.hor.entity.base.BaseEntity;
import com.handsofretail.hor.enums.Status;
import com.handsofretail.hor.enums.UserRole;
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
@Table(name = "client_users")
public class ClientUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.CLIENT;

    @Builder.Default
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Store> stores = new ArrayList<>();
}