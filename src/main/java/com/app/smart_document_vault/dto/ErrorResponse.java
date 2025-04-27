package com.app.smart_document_vault.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponse(String message, HttpStatus status, int code, LocalDateTime time) {
}
