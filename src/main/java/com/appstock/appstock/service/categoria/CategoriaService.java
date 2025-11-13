package com.appstock.appstock.service.categoria;

import com.appstock.appstock.dto.CategoriaDTO;
import com.appstock.appstock.entity.Categoria;
import com.appstock.appstock.repository.categoria.ICategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
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

    //Guardamos el objeto desde aquí
    @Override
    public Categoria updateCategoria(Long id, CategoriaDTO categoria) {
        Categoria existingCategoria = categoriaRepository.findById(id).orElseThrow(() -> new RuntimeException("Categoria con ID: " + id + " no encontrada"));
        existingCategoria.setNombre(categoria.getNombre());
        existingCategoria.setDescripcion(categoria.getDescripcion());
        return categoriaRepository.save(existingCategoria);
    }

    @Override
    public void deleteCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new EntityNotFoundException("Categoría no encontrada con ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }
}
