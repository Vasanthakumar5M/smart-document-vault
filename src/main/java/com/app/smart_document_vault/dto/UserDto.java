package com.app.smart_document_vault.dto;

import java.time.LocalDateTime;

public record UserDto(Long id,String name,String email,LocalDateTime createdAt,String role) {}
