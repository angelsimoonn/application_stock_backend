package com.appstock.appstock.controller;

import com.appstock.appstock.dto.login.AuthResponse;
import com.appstock.appstock.dto.login.ChangePasswordRequest;
import com.appstock.appstock.dto.login.LoginRequest;
import com.appstock.appstock.dto.login.RegisterRequest;
import com.appstock.appstock.entity.Usuario;
import com.appstock.appstock.repository.usuario.UsuarioRepository;
import com.appstock.appstock.security.JwtUtil;
import com.appstock.appstock.service.email.EmailService;
import com.appstock.appstock.service.usuario.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private EmailService emailService;

    private Usuario usuario;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("testuser");
        usuario.setPassword("$2a$10$encodedPassword");
        usuario.setRol("ROLE_USER");
        usuario.setEmail("test@example.com");

        loginRequest = new LoginRequest("testuser", "password123");

        registerRequest = new RegisterRequest();
        registerRequest.setNombre("newuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("newuser@example.com");
    }

    @Test
    void testLogin_Success() throws Exception {
        when(usuarioService.findByNombre("testuser")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("password123", usuario.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(usuario.getNombre(), usuario.getRol())).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.rol").value("ROLE_USER"));

        verify(usuarioService, times(1)).findByNombre("testuser");
        verify(passwordEncoder, times(1)).matches("password123", usuario.getPassword());
    }

    @Test
    void testLogin_UserNotFound() throws Exception {
        when(usuarioService.findByNombre("testuser")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is5xxServerError());

        verify(usuarioService, times(1)).findByNombre("testuser");
    }

    @Test
    void testLogin_WrongPassword() throws Exception {
        when(usuarioService.findByNombre("testuser")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("password123", usuario.getPassword())).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is5xxServerError());

        verify(usuarioService, times(1)).findByNombre("testuser");
        verify(passwordEncoder, times(1)).matches("password123", usuario.getPassword());
    }

    @Test
    void testRegister_Success() throws Exception {
        when(usuarioService.crearUsuario(anyString(), anyString(), anyString(), anyString())).thenReturn(usuario);
        when(jwtUtil.generateToken(usuario.getNombre(), usuario.getRol())).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.rol").value("ROLE_USER"));

        verify(usuarioService, times(1)).crearUsuario(
                registerRequest.getNombre(),
                registerRequest.getPassword(),
                registerRequest.getEmail(),
                "ROLE_USER"
        );
    }

    @Test
    void testChangePassword_Success() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setUsername("testuser");
        request.setOldPassword("oldpass");
        request.setNewPassword("newpass");

        when(usuarioRepository.findByNombre("testuser")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("oldpass", usuario.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newpass")).thenReturn("$2a$10$newEncodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Contraseña actualizada correctamente"));

        verify(usuarioRepository, times(1)).findByNombre("testuser");
        verify(passwordEncoder, times(1)).matches("oldpass", usuario.getPassword());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testChangePassword_WrongOldPassword() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setUsername("testuser");
        request.setOldPassword("wrongpass");
        request.setNewPassword("newpass");

        when(usuarioRepository.findByNombre("testuser")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongpass", usuario.getPassword())).thenReturn(false);

        mockMvc.perform(post("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La contraseña actual es incorrecta"));

        verify(usuarioRepository, times(1)).findByNombre("testuser");
        verify(passwordEncoder, times(1)).matches("wrongpass", usuario.getPassword());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testForgotPassword_Success() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("email", "test@example.com");

        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$tempPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Contraseña temporal enviada a tu correo"));

        verify(usuarioRepository, times(1)).findByEmail("test@example.com");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testForgotPassword_EmailNotFound() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("email", "notfound@example.com");

        when(usuarioRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());

        verify(usuarioRepository, times(1)).findByEmail("notfound@example.com");
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}
