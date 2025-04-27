package com.app.smart_document_vault.repository;

import com.app.smart_document_vault.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String username);
    List<User> findByIsActiveFalse();
    Long countByIsActiveTrue();
    Long countByIsActiveFalse();
}
