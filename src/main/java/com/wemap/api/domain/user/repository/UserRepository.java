package com.wemap.api.domain.user.repository;

import com.wemap.api.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByLoginId(String loginId);

    Boolean existsByEmail(String email);

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByIdx(Long idx);
}
