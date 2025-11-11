package com.appstock.appstock.service.movimiento;

import com.appstock.appstock.entity.Movimiento;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IMovimientoService {
    List<Movimiento> getMovimientos();

    Movimiento getMovimientoById(Long id);

    Movimiento addMovimiento(Movimiento movimiento);

    Movimiento updateMovimiento(Movimiento movimiento);

    boolean deleteMovimiento(Long id) throws Exception;
}
