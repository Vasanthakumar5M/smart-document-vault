package com.app.smart_document_vault.repository;

import com.app.smart_document_vault.entity.Document;
import com.app.smart_document_vault.entity.Folder;
import com.app.smart_document_vault.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.*;

public interface DocumentRepository extends JpaRepository<Document,Long> {
    List<Document> findByUploadedBy(User user);

    @Query("SELECT d FROM Document d WHERE d.uploadedBy.id= :userId")
    List<Document> findByUserId(Long userId);

    Long countByUploadedAtAfter(LocalDateTime time);

    @Query("SELECT d FROM Document d WHERE d.uploadedBy.email= :email")
    List<Document> findByUserEmail(String email);

    List<Document> findAllByFolder(Folder folder);

    boolean existsByUploadedByEmailAndFileName(String userEmail,String newName);
    
    List<Document> findByUploadedByAndFileNameLike(User user,String name);
    
    List<Document> findByFolder(Folder folder);
}
