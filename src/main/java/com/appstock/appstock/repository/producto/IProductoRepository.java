package com.appstock.appstock.repository.producto;

import com.appstock.appstock.entity.Producto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductoRepository extends CrudRepository<Producto, Long> {
    @Query("SELECT p FROM Producto p WHERE p.categoria.id = :id")
    List<Producto> buscarPorCategoria(@Param("id") Long id);
}
