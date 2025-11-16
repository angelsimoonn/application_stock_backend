package com.appstock.appstock.controller;

import com.appstock.appstock.dto.ProductoDTO;
import com.appstock.appstock.entity.Producto;
import com.appstock.appstock.mapper.Mapper;
import com.appstock.appstock.service.producto.IProductoService;
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
public class ProductoController {
    @Autowired
    private IProductoService productoService;
    @Autowired
    private Mapper mapper;

    @GetMapping("/productos")
    public ResponseEntity<List<ProductoDTO>> getProductos(){
        List<Producto> productos = productoService.getProductos();

        List<ProductoDTO> productosDTO = mapper.mapList(productos, ProductoDTO.class);

        return ResponseEntity.ok(productosDTO);
    }

    @GetMapping("/producto/{id}")
    public ResponseEntity<ProductoDTO> getProducto(@PathVariable("id") Long id){
        try {
            Producto producto = productoService.getProductoById(id);

            ProductoDTO productoDTO = mapper.mapType(producto, ProductoDTO.class);

            return ResponseEntity.ok(productoDTO);
        }  catch (Exception e) {
            throw new RuntimeException("Error al obtener la producto con id: " + id, e);
        }
    }

    @PostMapping("/producto")
    public ResponseEntity<ProductoDTO> createProducto(@RequestBody ProductoDTO productoDTO){
        try {
            Producto savedProducto = productoService.addProducto(mapper.mapType(productoDTO, Producto.class));
            ProductoDTO responseProducto = mapper.mapType(savedProducto, ProductoDTO.class);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseProducto);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la producto");
        }
    }

    @PutMapping("/producto/{id}")
    public ResponseEntity<?> updateProducto(@PathVariable("id") Long id, @RequestBody ProductoDTO productoDTO){
        Producto producto = null;
        try {
            producto = productoService.updateProducto(id, productoDTO);

            return new ResponseEntity<>(producto, HttpStatus.OK);
        } catch (Exception e){
            throw new RuntimeException("Error al actualizar la producto");
        }
    }

    @DeleteMapping("/producto/{id}")
    public ResponseEntity<Map<String, Object>> deleteProducto(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            productoService.deleteProducto(id);
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
