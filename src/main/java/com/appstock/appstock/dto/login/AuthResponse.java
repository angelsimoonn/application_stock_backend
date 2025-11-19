package com.appstock.appstock.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class AuthResponse {
    private String token;
    private String rol;
}
