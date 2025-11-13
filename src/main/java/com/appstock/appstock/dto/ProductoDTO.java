package com.appstock.appstock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class ProductoDTO {
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
}
