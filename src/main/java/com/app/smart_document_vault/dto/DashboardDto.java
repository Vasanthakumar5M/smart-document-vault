package com.app.smart_document_vault.dto;

import com.app.smart_document_vault.entity.Folder;
import java.util.List;

public record DashboardDto(List<FolderDto> folders,List<DocumentDto> documents) { }
