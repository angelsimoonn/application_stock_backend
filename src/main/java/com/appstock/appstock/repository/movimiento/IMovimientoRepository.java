package com.appstock.appstock.repository.movimiento;

import com.appstock.appstock.entity.Movimiento;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMovimientoRepository extends CrudRepository<Movimiento, Long> {
}
