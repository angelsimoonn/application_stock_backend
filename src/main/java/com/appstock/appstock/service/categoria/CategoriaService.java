package com.appstock.appstock.service.categoria;

import com.appstock.appstock.entity.Categoria;
import com.appstock.appstock.repository.categoria.ICategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService implements ICategoriaService{

    @Autowired
    private ICategoriaRepository categoriaRepository;

    @Override
    public List<Categoria> getCategorias() {
        return (List<Categoria>) categoriaRepository.findAll();
    }

    @Override
    public Categoria getCategoriaById(Long id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    @Override
    public Categoria addCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    //Guardamos el objeto desde aquí??
    @Override
    public Categoria updateCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Override
    public boolean deleteCategoria(Long id) throws Exception {
        categoriaRepository.deleteById(id);
        return true;
    }
}
