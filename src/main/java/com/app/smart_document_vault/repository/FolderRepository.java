package com.app.smart_document_vault.repository;

import com.app.smart_document_vault.entity.Folder;
import com.app.smart_document_vault.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder,Long> {
    List<Folder> findByUser(User user);

    List<Folder> findAllByUserEmail(String email);

    boolean existsByUserEmailAndName(String email, String name);
    
    Optional<Folder> findByUserEmailAndName(String userEmail,String name);

}
