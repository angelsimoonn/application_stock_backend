package com.appstock.appstock.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class LoginRequest {
    private String nombre;
    private String password;
    //private String rol;
}
