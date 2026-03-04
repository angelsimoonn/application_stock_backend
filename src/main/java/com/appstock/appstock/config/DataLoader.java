package com.appstock.appstock.config;

import com.appstock.appstock.entity.Categoria;
import com.appstock.appstock.entity.Producto;
import com.appstock.appstock.entity.Usuario;
import com.appstock.appstock.repository.categoria.ICategoriaRepository;
import com.appstock.appstock.repository.producto.IProductoRepository;
import com.appstock.appstock.repository.usuario.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepo,
                                      ICategoriaRepository categoriaRepo,
                                      IProductoRepository productoRepo,
                                      PasswordEncoder encoder) {
        return args -> {
            // USUARIO ADMIN POR DEFECTO
            if (usuarioRepo.findByNombre("admin").isEmpty()) {
                Usuario u = new Usuario();
                u.setNombre("admin");
                u.setPassword(encoder.encode("1234"));
                u.setRol("ADMIN");
                u.setEmail("angelsaimon02@gmail.com"); // <--- PON TU CORREO REAL PARA PROBAR
                usuarioRepo.save(u);
            }

            // CATEGORIA Y PRODUCTO
            if (categoriaRepo.count() == 0) {
                Categoria cat = new Categoria();
                cat.setNombre("General");
                cat.setDescripcion("Categoria por defecto");
                categoriaRepo.save(cat);

                Producto p = new Producto();
                p.setNombre("Producto Prueba");
                p.setDescripcion("Esto es una prueba");
                p.setPrecio(new BigDecimal("10.50"));
                p.setStock(50);
                p.setCategoria(cat);
                productoRepo.save(p);

                System.out.println("--- DATOS DE PRUEBA CARGADOS ---");
            }
        };
    }
}