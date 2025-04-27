package com.app.smart_document_vault.security;


import com.app.smart_document_vault.entity.User;
import com.app.smart_document_vault.repository.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepository.findByEmail(username).orElseThrow();
        if(!user.isActive()){
            throw new DisabledException("User is Deactivated.");
        }
        return new UserDetailsManager(user);
    }
}
