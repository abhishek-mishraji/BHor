package com.handsofretail.hor.entity;

import com.handsofretail.hor.entity.base.BaseEntity;
import com.handsofretail.hor.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "admin_users")
public class AdminUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.ADMIN;
}