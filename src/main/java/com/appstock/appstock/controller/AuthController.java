package com.appstock.appstock.controller;

import com.appstock.appstock.dto.login.*;
import com.appstock.appstock.entity.Usuario;
import com.appstock.appstock.security.JwtUtil;
import com.appstock.appstock.service.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        Usuario usuario = usuarioService.findByNombre(request.getNombre())
                .orElseThrow(() -> new RuntimeException("Usuario no existe"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtUtil.generateToken(usuario.getNombre(), usuario.getRol());
        return new AuthResponse(token, usuario.getRol());
    }

    // Registro: si se desea, permite enviar rol (solo ADMIN debería permitir crear ADMIN)
    @PostMapping("/register")
    public AuthResponse register(@RequestBody LoginRequest req) {
        // por simplicidad: registro público solo con ROLE_USER
        Usuario u = usuarioService.crearUsuario(req.getNombre(), req.getPassword(), "ROLE_USER");
        String token = jwtUtil.generateToken(u.getNombre(), u.getRol());
        return new AuthResponse(token, u.getRol());
    }
}
