package com.app.smart_document_vault.dto;

import com.app.smart_document_vault.entity.Folder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FolderDto {

    private long id;
    @NotBlank(message = "Folder name cannot be empty")
    private String name;

    public FolderDto(){}

    public FolderDto(Folder folder){
        this.id=folder.getId();
        this.name=folder.getName();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

