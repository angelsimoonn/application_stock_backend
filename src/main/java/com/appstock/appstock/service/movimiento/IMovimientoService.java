package com.appstock.appstock.service.movimiento;

import com.appstock.appstock.dto.MovimientoDTO;
import com.appstock.appstock.entity.Movimiento;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IMovimientoService {
    List<Movimiento> getMovimientos();

    Movimiento getMovimientoById(Long id);

    Movimiento addMovimiento(Movimiento movimiento);

    Movimiento updateMovimiento(Long id, MovimientoDTO movimiento);

    void deleteMovimiento(Long id) throws Exception;
}
