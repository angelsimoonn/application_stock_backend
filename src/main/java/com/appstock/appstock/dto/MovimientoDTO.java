package com.appstock.appstock.dto;

import com.appstock.appstock.entity.TipoMovimiento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class MovimientoDTO {
    private String descripcion;
    private TipoMovimiento tipoMovimiento;
    private LocalDateTime fecha;
}
