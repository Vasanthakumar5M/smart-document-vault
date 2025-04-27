package com.app.smart_document_vault.controller;

import com.app.smart_document_vault.dto.DocumentDto;
import com.app.smart_document_vault.dto.FolderDto;
import com.app.smart_document_vault.entity.Folder;
import com.app.smart_document_vault.service.FolderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/folders")
@Tag(name = "Folders",description = "Folder Management APIs")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @Operation(summary = "Folder Creation" , description = "Creating the folder in the server.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "201",description = "Folder created successfully"),
    		@ApiResponse(responseCode = "401",description = "Unauthorized"),
    		@ApiResponse(responseCode = "409",description = "Name Already Exists")
    })
    @PostMapping("/")
    public ResponseEntity<String> createFolder(@Valid @RequestBody FolderDto dto){
        String folderName = dto.getName();
        folderService.createFolder(folderName);
        return ResponseEntity.status(HttpStatus.CREATED).body("Folder created successfully.");
    }

    @Operation(summary = "Rename Folder",description = "Renaming the folder in the server.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Folder renamed successfully"),
    		@ApiResponse(responseCode = "404",description = "Folder not found"),
    		@ApiResponse(responseCode = "403",description = "Forbidden to rename this folder"),
    		@ApiResponse(responseCode = "409",description = "Name Already Exists")
    })
    @PutMapping("/rename/{id}")
    public ResponseEntity<String> renameFolder( @Parameter(description = "ID of the folder to rename") @PathVariable Long id,@Valid @RequestBody FolderDto dto) {
        String newName = dto.getName();
        folderService.renameFolder(id, newName);
        return ResponseEntity.ok("Folder renamed successfully.");
    }

    @Operation(summary = "Delete Folder",description = "Deleting the folder in the server.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Folder deleted successfully"),
    		@ApiResponse(responseCode = "404",description = "Folder not found"),
    		@ApiResponse(responseCode = "403",description = "Forbidden to delete this folder")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFolder( @Parameter(description = "ID of the folder to delete") @PathVariable Long id) {
        folderService.deleteFolder(id);
        return ResponseEntity.ok("Folder deleted successfully.");
    }

    @Operation(summary = "List All Folders",description = "List all the folders which are present in the server.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Folders retrieved successfully")
    })
    @GetMapping("/")
    public ResponseEntity<List<FolderDto>> getAllFoldersForUser() {
        List<FolderDto> folders = folderService.getAllFoldersForUser();
        return ResponseEntity.ok(folders);
    }

    @Operation(summary = "Retrieve Folder by Id",description = "Retrieving the folder contents by using its id.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Folder retrieved successfully"),
    		@ApiResponse(responseCode = "404",description = "Fodler not found"),
    		@ApiResponse(responseCode = "403",description = "Forbidden to access this folder")
    })
    @GetMapping("/{id}")
    public ResponseEntity<List<DocumentDto>> getFolderById( @Parameter(description = "ID of the folder to retrieve") @PathVariable Long id) {
        List<DocumentDto> documents = folderService.getFolderById(id);
        return ResponseEntity.ok(documents);
    }

    @Operation(summary = "Retrieve Folder by Name",description = "Retrieving the folders contents by using its name.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Folder Retrieved successfully"),
    		@ApiResponse(responseCode = "404",description = "Folder not found")
    })
    @GetMapping("/search/{name}")
    public ResponseEntity<List<DocumentDto>> getFolderByName( @Parameter(description = "Name of the folder to retrieve") @PathVariable String name) {
        List<DocumentDto> documents = folderService.getFolderByName(name);
        return ResponseEntity.ok(documents);
    }
}
