package com.app.smart_document_vault.dto;

import com.app.smart_document_vault.entity.Document;

import java.time.LocalDateTime;

public class DocumentDto{
    private Long id;
    private String fileName;
    private String fileType;
    private long size;
    private LocalDateTime uploadedAt;

    public DocumentDto(Document doc) {
        this.id = doc.getId();
        this.fileName = doc.getFileName();
        this.fileType = doc.getFileType();
        this.size = doc.getSize();
        this.uploadedAt = doc.getUploadedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
