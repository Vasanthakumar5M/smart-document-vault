package com.app.smart_document_vault.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class DownloadToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private LocalDateTime expiresAt;

    @ManyToOne
    private Document document;

    public DownloadToken(){}

    public DownloadToken(String token, LocalDateTime expiresAt, Document document) {
        this.token = token;
        this.expiresAt = expiresAt;
        this.document = document;
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
