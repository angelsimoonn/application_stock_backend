package com.appstock.appstock.service.usuario;

import com.appstock.appstock.entity.Usuario;
import com.appstock.appstock.repository.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario crearUsuario(String nombre, String password, String email, String rol) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setPassword(passwordEncoder.encode(password));
        u.setEmail(email); // <--- GUARDAMOS EMAIL
        u.setRol(rol == null ? "ROLE_USER" : rol);
        return usuarioRepository.save(u);
    }

    public Optional<Usuario> findByNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre);
    }
}
