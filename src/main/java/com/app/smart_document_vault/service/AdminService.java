package com.app.smart_document_vault.service;

import com.app.smart_document_vault.dto.AdminDashboardDto;
import com.app.smart_document_vault.entity.Document;
import com.app.smart_document_vault.repository.DocumentRepository;
import com.app.smart_document_vault.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminService {

    private final UserRepository userRepository;

    private final DocumentRepository documentRepository;

    private final UserService userService;

    public AdminService(UserRepository userRepository, DocumentRepository documentRepository, UserService userService) {
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.userService = userService;
    }

    public AdminDashboardDto getDashboardStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActiveTrue();
        long deactivatedUsers = userRepository.countByIsActiveFalse();

        long totalDocs = documentRepository.count();
        long docsThisWeek = documentRepository.countByUploadedAtAfter(LocalDateTime.now().minusDays(7));

        long totalStorageUsed = documentRepository.findAll().stream()
                .mapToLong(Document::getSize)
                .sum();

        AdminDashboardDto dto = new AdminDashboardDto();
        dto.setTotalUsers(totalUsers);
        dto.setActiveUsers(activeUsers);
        dto.setDeactivatedUsers(deactivatedUsers);
        dto.setTotalDocuments(totalDocs);
        dto.setDocumentsThisWeek(docsThisWeek);
        dto.setTotalStorageUsedInMB((totalStorageUsed/(1024*1024)));

        return dto;
    }




}
