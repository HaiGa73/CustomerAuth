package com.shop.controller;

import com.shop.dto.AuthRequest;
import com.shop.dto.AuthResponse;
import com.shop.entity.Customer;
import com.shop.repository.CustomerRepository;
import com.shop.service.NimbusTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class CustomerAuthController {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final NimbusTokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest req) {
        if (customerRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body("Email đã được sử dụng!");
        }

        Customer customer = Customer.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .phone(req.getPhone())
                .balance(BigDecimal.valueOf(1000000))
                .role("ROLE_USER")
                .build();

        return ResponseEntity.ok(customerRepository.save(customer));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        Customer customer = customerRepository.findByEmail(req.getEmail()).orElseThrow();
        String token = tokenService.generateToken(customer);

        return ResponseEntity.ok(new AuthResponse(token, "Bearer", tokenService.getExpirationMs()));
    }
}