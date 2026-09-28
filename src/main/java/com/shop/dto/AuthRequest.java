package com.shop.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
    private String fullName;
    private String phone;
}