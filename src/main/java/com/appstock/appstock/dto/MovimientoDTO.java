package com.appstock.appstock.dto;

import com.appstock.appstock.entity.TipoMovimiento;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class MovimientoDTO {
    private String descripcion;
    private TipoMovimiento tipoMovimiento;
    private LocalDateTime fecha;
}
