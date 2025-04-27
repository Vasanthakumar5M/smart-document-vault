package com.app.smart_document_vault.controller;

import com.app.smart_document_vault.dto.AdminDashboardDto;
import com.app.smart_document_vault.dto.DocumentDto;
import com.app.smart_document_vault.dto.UserDto;
import com.app.smart_document_vault.repository.UserRepository;
import com.app.smart_document_vault.service.AdminService;
import com.app.smart_document_vault.service.DocumentService;
import com.app.smart_document_vault.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin",description = "Admin APIs")
public class AdminController {

    private final AdminService adminService;

    private final UserService userService;

    private final DocumentService documentService;

    public AdminController(AdminService adminService, UserService userService, DocumentService documentService) {
        this.adminService = adminService;
        this.userService = userService;
        this.documentService = documentService;
    }

    @Operation(summary = "Admin Dashboard",description = "Retrieve all the details about the users and documents.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Retrieved successfully")
    })
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDto> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @Operation(summary = "Get all the users",description = "Retrieve all the users.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Retrieved successfully")
    })
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Retrieve User",description = "Retrieve the user by his/her id.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Retrieved successfully")
    })
    @GetMapping("/users/documents/{userId}")
    public ResponseEntity<List<DocumentDto>> getDocumentsByUserId( @Parameter(description = "ID of the user to retrieve") @PathVariable Long userId){
        return ResponseEntity.ok(documentService.getDocumentsByUserId(userId));
    }

    @Operation(summary = "Deative the User",description = "Deactivate the user by his/her id.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Deactivated successfully"),
    		@ApiResponse(responseCode = "404",description = "User not found")
    })
    @PutMapping("/users/deactivate/{userId}")
    public ResponseEntity<String> deactivateUser(@Parameter(description = "ID of the user to deactivate") @PathVariable Long userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.ok("User "+userId+" deactivated successfully.");
    }

    @Operation(summary = "Reactivate the User",description = "Reactivate the user by his/her id.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Reactivated successfully"),
    		@ApiResponse(responseCode = "404",description = "User not found"),
    		@ApiResponse(responseCode = "400",description = "User already active")
    })
    @PutMapping("/users/reactivate/{userId}")
    public ResponseEntity<String> reactivateUser(@Parameter(description = "ID of the user to reactivate") @PathVariable Long userId) {
        userService.reactivateUser(userId);
        return ResponseEntity.ok("User "+userId+" reactivated successfully.");
    }

    @Operation(summary = "Inactive User Details",description = "Retrieve the all inactive user details.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Retrieved successfully")
    })
    @GetMapping("/users/inactive")
    public ResponseEntity<List<UserDto>> getInactiveUsers(){
        return ResponseEntity.ok(userService.getDeactivatedUsers());
    }


}
