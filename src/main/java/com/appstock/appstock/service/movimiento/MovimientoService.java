package com.appstock.appstock.service.movimiento;

import com.appstock.appstock.entity.Movimiento;
import com.appstock.appstock.repository.movimiento.IMovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimientoService implements IMovimientoService {
    @Autowired
    private IMovimientoRepository movimientoRepository;

    @Override
    public List<Movimiento> getMovimientos() {
        return (List<Movimiento>) movimientoRepository.findAll();
    }

    @Override
    public Movimiento getMovimientoById(Long id) {
        return movimientoRepository.findById(id).orElse(null);
    }

    @Override
    public Movimiento addMovimiento(Movimiento movimiento) {
        return movimientoRepository.save(movimiento);
    }

    @Override
    public Movimiento updateMovimiento(Movimiento movimiento) {
        return movimientoRepository.save(movimiento);
    }

    @Override
    public boolean deleteMovimiento(Long id) throws Exception {
        movimientoRepository.findById(id).orElseThrow(() -> new Exception("Movimiento no encontrado"));
        movimientoRepository.deleteById(id);
        return true;
    }
}
