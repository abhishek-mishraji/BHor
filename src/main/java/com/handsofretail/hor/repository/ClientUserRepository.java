package com.handsofretail.hor.repository;

import com.handsofretail.hor.entity.ClientUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientUserRepository extends JpaRepository<ClientUser, Long> {

    Optional<ClientUser> findByEmail(String email);

    boolean existsByEmail(String email);
}