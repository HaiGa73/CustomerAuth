package com.shop.controller;

import com.shop.entity.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/customer")
public class OrderController {

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal Customer customer) {
        return ResponseEntity.ok(Map.of(
                "id", customer.getId(),
                "fullName", customer.getFullName(),
                "email", customer.getEmail(),
                "phone", customer.getPhone(),
                "walletBalance", customer.getBalance()
        ));
    }
}