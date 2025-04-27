package com.app.smart_document_vault.repository;

import com.app.smart_document_vault.entity.DownloadToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DownloadTokenRepository extends JpaRepository<DownloadToken,Long> {
    Optional<DownloadToken> findByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM DownloadToken t WHERE t.expiresAt < :now")
    void deleteAllExpired(@Param("now")LocalDateTime now);
}
