package com.app.smart_document_vault.service;

import com.app.smart_document_vault.dto.DocumentDto;
import com.app.smart_document_vault.dto.UserDto;
import com.app.smart_document_vault.entity.User;
import com.app.smart_document_vault.exceptions.UserAlreadyActiveException;
import com.app.smart_document_vault.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setActive(false);
        userRepository.save(user);
    }

    public void reactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(user.isActive()){
            throw new UserAlreadyActiveException("User is already active.");
        }
        user.setActive(true);
        userRepository.save(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    return new UserDto(user.getId(),user.getFullName(),user.getEmail(),user.getCreatedAt(),user.getRole().name());
                }).toList();
    }

    public List<UserDto> getDeactivatedUsers(){
        return userRepository.findByIsActiveFalse()
                .stream()
                .map(user->{
                    return new UserDto(user.getId(),user.getFullName(),user.getEmail(),user.getCreatedAt(),user.getRole().name());
                })
                .toList();
    }
}
