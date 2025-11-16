package com.appstock.appstock.controller;

import com.appstock.appstock.dto.CategoriaDTO;
import com.appstock.appstock.entity.Categoria;
import com.appstock.appstock.mapper.Mapper;
import com.appstock.appstock.service.categoria.ICategoriaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class CategoriaController {
    @Autowired
    private ICategoriaService categoriaService;
    @Autowired
    private Mapper mapper;

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaDTO>> getCategorias(){
        List<Categoria> categorias = categoriaService.getCategorias();

        List<CategoriaDTO> categoriasDTO = mapper.mapList(categorias, CategoriaDTO.class);

        return ResponseEntity.ok(categoriasDTO);
    }

    @GetMapping("/categoria/{id}")
    public ResponseEntity<CategoriaDTO> getCategoria(@PathVariable("id") Long id){
        try {
            Categoria categoria = categoriaService.getCategoriaById(id);

            CategoriaDTO categoriaDTO = mapper.mapType(categoria, CategoriaDTO.class);

            return ResponseEntity.ok(categoriaDTO);
        }  catch (Exception e) {
            throw new RuntimeException("Error al obtener la categoria con id: " + id, e);
        }
    }

    @PostMapping("/categoria")
    public ResponseEntity<CategoriaDTO> createCategoria(@RequestBody CategoriaDTO categoriaDTO){
        try {
            Categoria savedCategoria = categoriaService.addCategoria(mapper.mapType(categoriaDTO, Categoria.class));
            CategoriaDTO responseCategoria = mapper.mapType(savedCategoria, CategoriaDTO.class);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseCategoria);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la categoria");
        }
    }

    @PutMapping("/categoria/{id}")
    public ResponseEntity<?> updateCategoria(@PathVariable("id") Long id, @RequestBody CategoriaDTO categoriaDTO){
        Categoria categoria = null;
        try {
            categoria = categoriaService.updateCategoria(id, categoriaDTO);

            return new ResponseEntity<>(categoria, HttpStatus.OK);
        } catch (Exception e){
            throw new RuntimeException("Error al actualizar la categoria");
        }
    }

    @DeleteMapping("/categoria/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategoria(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            categoriaService.deleteCategoria(id);
            response.put("mensaje", "La categoría ha sido eliminada con éxito");
            return ResponseEntity.ok(response); // 200 OK
        } catch (EntityNotFoundException e) {
            response.put("mensaje", "No se encontró la categoría con ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("mensaje", "Error al eliminar la categoría con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
