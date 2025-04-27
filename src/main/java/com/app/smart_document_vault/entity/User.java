package com.app.smart_document_vault.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String fullName;
    @Column(unique = true,nullable = false)
    @Email
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt=LocalDateTime.now();
    @Column(nullable = false)
    private boolean isActive=true;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Folder> folders=new ArrayList<>();
    @OneToMany(mappedBy = "uploadedBy" , cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Document> documents=new ArrayList<>();

    public User(){}

    public User(String fullName, String email, String password,boolean isActive) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.isActive=isActive;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
