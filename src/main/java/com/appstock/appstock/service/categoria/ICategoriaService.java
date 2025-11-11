package com.appstock.appstock.service.categoria;

import com.appstock.appstock.entity.Categoria;

import java.util.List;

public interface ICategoriaService {
    List<Categoria> getCategorias();

    Categoria getCategoriaById(Long id);

    Categoria addCategoria(Categoria categoria);

    Categoria updateCategoria(Categoria categoria);

    boolean deleteCategoria(Long id) throws Exception;
}
