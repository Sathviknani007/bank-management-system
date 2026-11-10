package com.example.bank.service;

import com.example.bank.dto.AuthResponse;
import com.example.bank.dto.LoginRequest;
import com.example.bank.dto.RegisterRequest;
import com.example.bank.exception.BusinessException;
import com.example.bank.model.AppUser;
import com.example.bank.model.Role;
import com.example.bank.repository.AppUserRepository;
import com.example.bank.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final String ADMIN_USERNAME = "admin";

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (appUserRepository.existsByUsername(request.username())) {
            throw new BusinessException("Username is already taken");
        }

        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        if (ADMIN_USERNAME.equalsIgnoreCase(request.username())) {
            user.setRole(Role.ADMIN);
        }
        appUserRepository.save(user);

        return new AuthResponse(jwtService.generateToken(user));
    }

    public AuthResponse login(LoginRequest request) {
        var authentication = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        authenticationManager.authenticate(authentication);
        AppUser user = appUserRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException("Invalid credentials"));
        return new AuthResponse(jwtService.generateToken(user));
    }
}
