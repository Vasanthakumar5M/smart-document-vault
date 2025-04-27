package com.app.smart_document_vault.controller;

import com.app.smart_document_vault.dto.AuthResponse;
import com.app.smart_document_vault.dto.LoginDto;
import com.app.smart_document_vault.dto.RegisterDto;
import com.app.smart_document_vault.exceptions.InvalidCredentialsException;
import com.app.smart_document_vault.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication",description = "Authentication Management APIs")
public class AuthenticationController {

    private final AuthService authService;

    public AuthenticationController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "User Registration",description = "Register the user in the server.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "Registration successfull")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterDto register){
        return ResponseEntity.ok(authService.register(register));
    }

    @Operation(summary = "User Login",description = "User login process.")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200",description = "logged in successfully"),
    		@ApiResponse(responseCode = "401",description = "Invalid credentials"),
    		@ApiResponse(responseCode = "423",description = "User is deactivated by admin")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDto login) throws InvalidCredentialsException {
        return ResponseEntity.ok(authService.login(login));
    }


}
