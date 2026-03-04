package com.appstock.appstock.controller;

import com.appstock.appstock.dto.login.*;
import com.appstock.appstock.entity.Usuario;
import com.appstock.appstock.repository.usuario.UsuarioRepository;
import com.appstock.appstock.security.JwtUtil;
import com.appstock.appstock.service.email.EmailService;
import com.appstock.appstock.service.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository; // Acceso directo para updates rápidos
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

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

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest req) {
        // Validar que el email no esté vacío si quieres
        if(req.getEmail() == null || req.getEmail().isEmpty()){
            throw new RuntimeException("El email es obligatorio");
        }

        // Pasamos el email al servicio
        Usuario u = usuarioService.crearUsuario(req.getNombre(), req.getPassword(), req.getEmail(), "ROLE_USER");

        String token = jwtUtil.generateToken(u.getNombre(), u.getRol());
        return new AuthResponse(token, u.getRol());
    }

    // --- NUEVO: CAMBIAR CONTRASEÑA (Desde Ajustes) ---
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        Usuario usuario = usuarioRepository.findByNombre(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar contraseña vieja
        if (!passwordEncoder.matches(request.getOldPassword(), usuario.getPassword())) {
            return ResponseEntity.status(400).body("La contraseña actual es incorrecta");
        }

        // Guardar nueva
        usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    // --- NUEVO: OLVIDÉ MI CONTRASEÑA (Genera Temporal) ---
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No existe ningún usuario con ese email"));

        // 1. Generar pass aleatoria (8 chars)
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);

        // 2. Guardar en BD encriptada
        usuario.setPassword(passwordEncoder.encode(tempPassword));
        usuarioRepository.save(usuario);

        // 3. Enviar por email
        emailService.sendEmail(
                usuario.getEmail(),
                "Recuperación de Contraseña - AppStock",
                "Hola " + usuario.getNombre() + ",\n\n" +
                        "Has solicitado recuperar tu contraseña.\n" +
                        "Tu nueva contraseña temporal es: " + tempPassword + "\n\n" +
                        "Por favor, inicia sesión con ella y cámbiala en Ajustes > Cambiar Contraseña."
        );

        return ResponseEntity.ok("Contraseña temporal enviada a tu correo");
    }
}