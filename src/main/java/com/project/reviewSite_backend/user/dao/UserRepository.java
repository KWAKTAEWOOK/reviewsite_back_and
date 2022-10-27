package com.project.reviewSite_backend.user.dao;

import com.project.reviewSite_backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByUserid(String userid);

    boolean existsByUserid(String userid);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    User save(User user);

}

