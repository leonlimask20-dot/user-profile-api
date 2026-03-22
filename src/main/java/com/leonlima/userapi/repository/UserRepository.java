package com.leonlima.userapi.repository;

import com.leonlima.userapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // Usado para checar duplicidade antes do INSERT, evitando round-trip desnecessário
    boolean existsByEmail(String email);
}
