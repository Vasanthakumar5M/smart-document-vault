package com.app.smart_document_vault.service;

import com.app.smart_document_vault.dto.AuthResponse;
import com.app.smart_document_vault.dto.LoginDto;
import com.app.smart_document_vault.dto.RegisterDto;
import com.app.smart_document_vault.entity.Role;
import com.app.smart_document_vault.entity.User;
import com.app.smart_document_vault.exceptions.InvalidCredentialsException;
import com.app.smart_document_vault.repository.UserRepository;
import com.app.smart_document_vault.util.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final AuthenticationManager authManager;

    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtService jwtService, AuthenticationManager authManager) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authManager = authManager;
    }

    public AuthResponse register(RegisterDto register){
        User user=new User();
        user.setFullName(register.getFullName());
        user.setEmail(register.getEmail());
        user.setPassword(passwordEncoder.encode(register.getPassword()));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        String token=jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginDto login) throws InvalidCredentialsException {
        Authentication authentication=authManager.authenticate(new UsernamePasswordAuthenticationToken(login.username(),login.password()));

        if(!authentication.isAuthenticated()){
            throw new InvalidCredentialsException("Invalid username or password");
        }
        User user=userRepository.findByEmail(login.username()).get();
        if(!user.isActive()){
            throw new DisabledException("User is deactivated.");
        }
        return new AuthResponse(jwtService.generateToken(user));
    }
}
