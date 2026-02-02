package com.appstock.appstock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;

    // --- NUEVO CAMPO ---
    private String imagen;
    // -------------------

    @JsonProperty("categoriaId")
    private Long categoriaId;
}