package com.app.smart_document_vault.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String filePath;
    private String fileType;

    private long size;
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name="folder_id")
    private Folder folder;

    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    public Document(){}

    public Document(Document document) {
        this.fileName = document.fileName;
        this.filePath = document.filePath;
        this.fileType = document.fileType;
        this.size = document.size;
        this.uploadedAt = LocalDateTime.now();
        this.folder = document.folder;
        this.uploadedBy = document.uploadedBy;
    }

    public Document(String fileName, String filePath, String fileType, long size, LocalDateTime uploadedAt, User uploadedBy) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.size = size;
        this.uploadedAt = uploadedAt;
        this.uploadedBy = uploadedBy;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

}
