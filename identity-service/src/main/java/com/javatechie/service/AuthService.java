package com.javatechie.service;

import com.javatechie.entity.UserCredential;
import com.javatechie.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserCredentialRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public String saveUser(UserCredential credential) {
        if (credential.getRole() == null) {
            credential.setRole("USER");
        }
        credential.setPassword(passwordEncoder.encode(credential.getPassword()));
        repository.save(credential);
        return "user added to the system";
    }

    public String generateToken(String username,String role2) {
       String role= repository.findByName(username).get().getRole();
        return jwtService.generateToken(username,role);
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }


}
