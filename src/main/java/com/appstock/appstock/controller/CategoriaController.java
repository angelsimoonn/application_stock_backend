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
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
@Tag(name = "Categorías", description = "Gestión de categorías de productos")
public class CategoriaController {
    @Autowired
    private ICategoriaService categoriaService;
    @Autowired
    private Mapper mapper;

    @Operation(summary = "Obtener todas las categorías", description = "Retorna una lista de todas las categorías disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente")
    })
    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaDTO>> getCategorias(){
        List<Categoria> categorias = categoriaService.getCategorias();

        List<CategoriaDTO> categoriasDTO = mapper.mapList(categorias, CategoriaDTO.class);

        return ResponseEntity.ok(categoriasDTO);
    }

    @Operation(summary = "Obtener categoría por ID", description = "Retorna una categoría específica según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @GetMapping("/categoria/{id}")
    public ResponseEntity<CategoriaDTO> getCategoria(
            @Parameter(description = "ID de la categoría a buscar") @PathVariable("id") Long id){
        try {
            Categoria categoria = categoriaService.getCategoriaById(id);

            CategoriaDTO categoriaDTO = mapper.mapType(categoria, CategoriaDTO.class);

            return ResponseEntity.ok(categoriaDTO);
        }  catch (Exception e) {
            throw new RuntimeException("Error al obtener la categoria con id: " + id, e);
        }
    }

    @Operation(summary = "Crear nueva categoría", description = "Crea una nueva categoría de productos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/categoria")
    public ResponseEntity<CategoriaDTO> createCategoria(
            @Parameter(description = "Datos de la categoría a crear") @Valid @RequestBody CategoriaDTO categoriaDTO){
        try {
            Categoria savedCategoria = categoriaService.addCategoria(mapper.mapType(categoriaDTO, Categoria.class));
            CategoriaDTO responseCategoria = mapper.mapType(savedCategoria, CategoriaDTO.class);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseCategoria);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la categoria");
        }
    }

    @Operation(summary = "Actualizar categoría", description = "Actualiza los datos de una categoría existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/categoria/{id}")
    public ResponseEntity<?> updateCategoria(
            @Parameter(description = "ID de la categoría a actualizar") @PathVariable("id") Long id,
            @Parameter(description = "Nuevos datos de la categoría") @Valid @RequestBody CategoriaDTO categoriaDTO){
        Categoria categoria = null;
        try {
            categoria = categoriaService.updateCategoria(id, categoriaDTO);

            return new ResponseEntity<>(categoria, HttpStatus.OK);
        } catch (Exception e){
            throw new RuntimeException("Error al actualizar la categoria");
        }
    }

    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error al eliminar la categoría")
    })
    @DeleteMapping("/categoria/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategoria(
            @Parameter(description = "ID de la categoría a eliminar") @PathVariable Long id) {
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
