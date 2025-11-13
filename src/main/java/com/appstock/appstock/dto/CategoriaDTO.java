package com.appstock.appstock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Getter
@Setter
public class CategoriaDTO {
    private String nombre;
    private String descripcion;
}
