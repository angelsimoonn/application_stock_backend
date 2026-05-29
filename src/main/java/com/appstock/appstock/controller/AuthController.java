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
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Autenticación", description = "Gestión de autenticación y seguridad de usuarios")
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository; // Acceso directo para updates rápidos
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso, token generado"),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public AuthResponse login(
            @Parameter(description = "Credenciales de inicio de sesión") @Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioService.findByNombre(request.getNombre())
                .orElseThrow(() -> new RuntimeException("Usuario no existe"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtUtil.generateToken(usuario.getNombre(), usuario.getRol());
        return new AuthResponse(token, usuario.getRol());
    }

    @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario en el sistema y retorna un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos o usuario ya existe")
    })
    @PostMapping("/register")
    public AuthResponse register(
            @Parameter(description = "Datos de registro del nuevo usuario") @Valid @RequestBody RegisterRequest req) {
        // Validar que el email no esté vacío si quieres
        if(req.getEmail() == null || req.getEmail().isEmpty()){
            throw new RuntimeException("El email es obligatorio");
        }

        // Pasamos el email al servicio
        Usuario u = usuarioService.crearUsuario(req.getNombre(), req.getPassword(), req.getEmail(), "ROLE_USER");

        String token = jwtUtil.generateToken(u.getNombre(), u.getRol());
        return new AuthResponse(token, u.getRol());
    }

    @Operation(summary = "Cambiar contraseña", description = "Permite al usuario cambiar su contraseña actual por una nueva")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Parameter(description = "Datos para cambio de contraseña") @Valid @RequestBody ChangePasswordRequest request) {
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

    @Operation(summary = "Recuperar contraseña", description = "Genera una contraseña temporal y la envía al email del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña temporal enviada al correo"),
            @ApiResponse(responseCode = "404", description = "No existe usuario con ese email")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @Parameter(description = "Email del usuario que olvidó su contraseña") @RequestBody Map<String, String> request) {
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