package com.appstock.appstock.controller;

import com.appstock.appstock.dto.ProductoDTO;
import com.appstock.appstock.entity.Categoria;
import com.appstock.appstock.entity.Producto;
import com.appstock.appstock.mapper.Mapper;
import com.appstock.appstock.service.categoria.ICategoriaService;
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
    private ICategoriaService categoriaService;
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

    // CREAR PRODUCTO (Asegurando la categoría)
    @PostMapping("/producto")
    public ResponseEntity<ProductoDTO> createProducto(@RequestBody ProductoDTO productoDTO) {
        try {
            // 1. Mapeo básico (Nombre, precio, stock...)
            Producto producto = mapper.mapType(productoDTO, Producto.class);

            // 2. ASIGNACIÓN MANUAL DE CATEGORÍA (La clave del éxito)
            if (productoDTO.getCategoriaId() != null) {
                Categoria c = categoriaService.getCategoriaById(productoDTO.getCategoriaId());
                producto.setCategoria(c);
            }

            // 3. Guardar
            Producto savedProducto = productoService.addProducto(producto);

            // 4. Devolver respuesta
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.mapType(savedProducto, ProductoDTO.class));
        } catch (Exception e) {
            throw new RuntimeException("Error al crear el producto: " + e.getMessage());
        }
    }

    @PutMapping("/producto/{id}")
    public ResponseEntity<ProductoDTO> updateProducto(@PathVariable("id") Long id, @RequestBody ProductoDTO productoDTO) {
        // --- CHIVATOS DE DEBUG ---
        System.out.println("--> PETICIÓN DE ACTUALIZAR RECIBIDA PARA ID: " + id);
        System.out.println("--> NOMBRE: " + productoDTO.getNombre());
        System.out.println("--> CATEGORIA ID QUE LLEGA: " + productoDTO.getCategoriaId());
        // -------------------------

        try {
            Producto existingProducto = productoService.getProductoById(id);
            if (existingProducto == null) throw new RuntimeException("No existe");

            // Actualizamos datos básicos
            existingProducto.setNombre(productoDTO.getNombre());
            existingProducto.setDescripcion(productoDTO.getDescripcion());
            existingProducto.setPrecio(productoDTO.getPrecio());
            existingProducto.setStock(productoDTO.getStock());
            // IMAGEN Solo la cambiamos si nos envían una nueva (no nula)
            if (productoDTO.getImagen() != null) {
                existingProducto.setImagen(productoDTO.getImagen());
            }

            // IMPORTANTE: ASIGNAR CATEGORÍA
            if (productoDTO.getCategoriaId() != null) {
                // Buscamos la categoría y la asignamos
                Categoria c = categoriaService.getCategoriaById(productoDTO.getCategoriaId());
                existingProducto.setCategoria(c);
                System.out.println("--> ASIGNANDO CATEGORÍA: " + c.getNombre());
            } else {
                System.out.println("--> ¡OJO! EL CATEGORIA ID HA LLEGADO NULL");
            }

            Producto updated = productoService.addProducto(existingProducto);
            return ResponseEntity.ok(mapper.mapType(updated, ProductoDTO.class));
        } catch (Exception e) {
            e.printStackTrace(); // Imprime el error real en consola
            throw new RuntimeException("Error al actualizar");
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

    @GetMapping("/productos/categoria/{id}")
    public ResponseEntity<List<ProductoDTO>> getProductosPorCategoria(@PathVariable("id") Long id) {
        List<Producto> productos = productoService.getProductosPorCategoria(id);
        List<ProductoDTO> dtos = mapper.mapList(productos, ProductoDTO.class);
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/producto/{id}/stock")
    public ResponseEntity<ProductoDTO> actualizarStock(@PathVariable("id") Long id, @RequestParam("cantidad") int cantidad) {
        try {
            Producto producto = productoService.getProductoById(id);
            if (producto == null) throw new RuntimeException("Producto no encontrado");

            // Calculamos el nuevo stock
            int nuevoStock = producto.getStock() + cantidad;

            // Evitamos stock negativo
            if (nuevoStock < 0) nuevoStock = 0;

            producto.setStock(nuevoStock);

            // Guardamos (La categoría y demás datos NO se tocan, así que es seguro)
            Producto saved = productoService.addProducto(producto);

            return ResponseEntity.ok(mapper.mapType(saved, ProductoDTO.class));
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar stock");
        }
    }
}