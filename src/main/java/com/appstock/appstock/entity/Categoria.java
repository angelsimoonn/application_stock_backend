package com.appstock.appstock.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column
    private String descripcion;

    // Relación jerárquica opcional (para subcategorías)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Categoria categoriaPadre;

    // Una categoría puede tener muchos productos
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producto> productos;
}
