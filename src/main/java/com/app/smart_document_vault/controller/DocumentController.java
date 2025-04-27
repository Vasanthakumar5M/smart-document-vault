package com.app.smart_document_vault.controller;

import com.app.smart_document_vault.dto.DashboardDto;
import com.app.smart_document_vault.dto.DocumentDto;
import com.app.smart_document_vault.entity.Document;
import com.app.smart_document_vault.service.DocumentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.time.LocalDate;
import java.util.List;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Documents",description = "Document Management APIs")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Operation(summary = "User Dashboard", description = "Retrieve the total folders and documents which are present in the user dashboard.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Success")
    })
    @GetMapping("/")
    public ResponseEntity<DashboardDto> userDashboard(){
        DashboardDto dto=documentService.userDashboard();
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Upload Document", description = "Upload the documents to the server.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Document uploaded Successfully"),
    		@ApiResponse(responseCode = "401",description = "Unauthorized")
    })
    @PostMapping(value="/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             Authentication authentication,
                                             @RequestParam(required = false)Long folderId) throws IOException {

            String email=authentication.getName(); //<- authenticated user email.
            Document document=documentService.uploadDocument(file,email,folderId);

            return ResponseEntity.ok("File uploaded: "+document.getFileName());
    }

    @Operation(summary = "Download Document",description = "Download the documents from the server.")
    @ApiResponses(value = {
    	    @ApiResponse(responseCode = "200", description = "Successfully downloaded"),
    	    @ApiResponse(responseCode = "404", description = "Document not found"),
    	    @ApiResponse(responseCode = "403", description = "Forbidden to access this document")
    	})
    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadDocument(@Parameter(description = "ID of the document to retrieve") @PathVariable Long id,Authentication authentication) throws IOException {
            String email= authentication.getName();
            Resource file= documentService.downloadFile(id,email);

            String contentType= Files.probeContentType(file.getFile().toPath()); //determine the content type to browser open it properly.

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + file.getFilename() + "\"")
                    //attachment <- treat this response as a downloadable file , filename=" " <- suggest a name when the file is saved
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream")) //application/octet-stream <- generic binary data.
                    .body(file);
    }

    @Operation(summary = "Get All Documents",description = "Retrieve all the documents which are belongs to the user.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Retrieved successfully"),
    		@ApiResponse(responseCode = "401",description = "Unauthorized")
    })
    @GetMapping("/myfiles")
    public ResponseEntity<List<DocumentDto>> getMyDocuments(
            Authentication auth,@Parameter(description = "Name of the document")
            @RequestParam(required = false) String name,
            @Parameter(description = "Folder id")
            @RequestParam(required = false) Long folderId,
            @Parameter(description = "Document Type")
            @RequestParam(required = false) String fileType,
            @Parameter(description = "From date")
            @RequestParam(required = false)LocalDate fromDate,
            @Parameter(description = "To date")
            @RequestParam(required = false)LocalDate toDate
    ) {
        String email = auth.getName();
        List<DocumentDto> result = documentService.getDocumentsWithFilters(email,name, folderId,fileType, fromDate, toDate);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Delete Document",description = "Delete the documents which are present in the server.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Deleted Successfully"),
    		@ApiResponse(responseCode = "404",description = "Document not found"),
    		@ApiResponse(responseCode = "403",description = "Forbidden to delete this document")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDocument(@Parameter(description = "ID of the document to delete") @PathVariable long id, Authentication authentication) throws IOException {
            documentService.deleteDocument(id, authentication.getName());
            return ResponseEntity.ok("Document deleted successfully");
    }

    @Operation(summary = "Share Document",description = "Generate the link for sharing the document.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Link generated successfully"),
    		@ApiResponse(responseCode = "404",description = "Document not found"),
    		@ApiResponse(responseCode = "403",description = "Forbidden to share this document")
    })
    @PostMapping("/share/{id}")
    public ResponseEntity<String> createShareLink(@Parameter(description = "ID of the document to share") @PathVariable Long id,Authentication authentication) throws FileNotFoundException {
            String link=documentService.generateDownloadToken(id, authentication.getName());
            return ResponseEntity.ok(link);
    }

    @Operation(summary = "Download using link", description = "Download the documents using the shared link.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Successfully Downloaded"),
    		@ApiResponse(responseCode = "401",description = "Invalid or expired token")
    })
    @GetMapping("/share/{token}")
    public ResponseEntity<Resource> downloadViaToken(@Parameter(description = "Token of the shared document") @PathVariable String token) throws IOException {
        Resource resource= documentService.downloadViaToken(token);
        String contentType= Files.probeContentType(resource.getFile().toPath());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream")) //application/octet-stream <- generic binary data.

                .body(resource);
    }
    
    @Operation(summary = "Move Document",description = "Move the documents to the another place.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Moved Successfully"),
    		@ApiResponse(responseCode = "404",description = "Document or Folder not found"),
    		@ApiResponse(responseCode = "403",description = "Forbidden to move this document")
    })
    @PutMapping("/move")
    public ResponseEntity<String> moveDocument(@Parameter(description = "ID of the document to move") @RequestParam Long documentId,
    									   @Parameter(description = "ID of the folder to move")
                                           @RequestParam(required = false) Long folderId,
                                           Authentication authentication) throws FileNotFoundException {
        documentService.moveDocument(documentId,folderId, authentication.getName());
        return ResponseEntity.ok("Document moved to folder successfully.");
    }

    @Operation(summary = "Copy Document",description = "Copy the documents to the another place.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Copied Successfully"),
    		@ApiResponse(responseCode = "404",description = "Document or Folder not found"),
    		@ApiResponse(responseCode = "403",description = "Forbidden to copy this document")
    })
    @PostMapping("/copy")
    public ResponseEntity<String> copyDocument(@Parameter(description = "ID of the document to copy") @RequestParam Long documentId,
    		 							   @Parameter(description = "ID of the folder to copy")
                                           @RequestParam(required = false) Long folderId,
                                           Authentication authentication) throws IOException {
        documentService.copyDocument(documentId, folderId, authentication.getName());
        return ResponseEntity.ok("Document copied to folder successfully.");
    }

    @Operation(summary = "Rename Document",description = "Rename the documents which are present in the server.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Renamed Successfully"),
    		@ApiResponse(responseCode = "404",description = "Document not found"),
    		@ApiResponse(responseCode = "403",description = "Forbidden to rename this document"),
    		@ApiResponse(responseCode = "409",description = "Name already exists")
    })
    @PutMapping("/rename")
    public ResponseEntity<String> renameDocument( @Parameter(description = "ID of the document to rename") @RequestParam Long documentId,
    		 									 @Parameter(description = "New name of the document")
                                                 @RequestParam String name,
                                                 Authentication authentication) throws FileAlreadyExistsException, FileNotFoundException {
        documentService.renameDocument(documentId,name,authentication.getName());
        return ResponseEntity.ok("Document renamed successfully.");
    }
    
    @Operation(summary = "Search Document",description = "Searching the documents by its start,middle or last letters.",responses = @ApiResponse(responseCode = "401",description = "Unauthorized"))
    @GetMapping("/search")
    public ResponseEntity<List<DocumentDto>> findByFileName(@RequestParam String name,Authentication authentication){
    	return ResponseEntity.ok(documentService.findByFileName(name, authentication.getName()));
    }

}
