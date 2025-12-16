package com.appstock.appstock.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class CategoriaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
}
