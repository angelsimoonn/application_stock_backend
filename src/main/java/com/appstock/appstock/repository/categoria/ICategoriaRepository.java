package com.appstock.appstock.repository.categoria;

import com.appstock.appstock.entity.Categoria;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoriaRepository extends CrudRepository<Categoria, Long> {
}
