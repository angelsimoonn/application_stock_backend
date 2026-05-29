package com.appstock.appstock.controller;

import com.appstock.appstock.dto.ProductoDTO;
import com.appstock.appstock.entity.Categoria;
import com.appstock.appstock.entity.Producto;
import com.appstock.appstock.mapper.Mapper;
import com.appstock.appstock.service.categoria.ICategoriaService;
import com.appstock.appstock.service.producto.IProductoService;
import com.appstock.appstock.service.producto.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Tag(name = "Productos", description = "Gestión de productos del inventario")
public class ProductoController {
    private final Logger logger = LoggerFactory.getLogger(ProductoService.class);
    @Autowired
    private IProductoService productoService;
    @Autowired
    private ICategoriaService categoriaService;
    @Autowired
    private Mapper mapper;

    @Operation(summary = "Obtener todos los productos", description = "Retorna una lista de todos los productos disponibles en el inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    })
    @GetMapping("/productos")
    public ResponseEntity<List<ProductoDTO>> getProductos(){
        List<Producto> productos = productoService.getProductos();

        List<ProductoDTO> productosDTO = mapper.mapList(productos, ProductoDTO.class);

        return ResponseEntity.ok(productosDTO);
    }

    @Operation(summary = "Obtener producto por ID", description = "Retorna un producto específico según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/producto/{id}")
    public ResponseEntity<ProductoDTO> getProducto(
            @Parameter(description = "ID del producto a buscar") @PathVariable("id") Long id){
        try {
            Producto producto = productoService.getProductoById(id);

            ProductoDTO productoDTO = mapper.mapType(producto, ProductoDTO.class);

            return ResponseEntity.ok(productoDTO);
        }  catch (Exception e) {
            throw new RuntimeException("Error al obtener la producto con id: " + id, e);
        }
    }

    @Operation(summary = "Crear nuevo producto", description = "Crea un nuevo producto en el inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/producto")
    public ResponseEntity<ProductoDTO> createProducto(
            @Parameter(description = "Datos del producto a crear") @Valid @RequestBody ProductoDTO productoDTO) {
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
            logger.error(e.getMessage(), e.getStackTrace(), e);
            throw new RuntimeException("Error al crear el producto: " + e.getMessage());
        }
    }

    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/producto/{id}")
    public ResponseEntity<ProductoDTO> updateProducto(
            @Parameter(description = "ID del producto a actualizar") @PathVariable("id") Long id,
            @Parameter(description = "Nuevos datos del producto") @Valid @RequestBody ProductoDTO productoDTO) {
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

    @Operation(summary = "Eliminar producto", description = "Elimina un producto del inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/producto/{id}")
    public ResponseEntity<Map<String, Object>> deleteProducto(
            @Parameter(description = "ID del producto a eliminar") @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            productoService.deleteProducto(id);
            response.put("mensaje", "La categoría ha sido eliminada con éxito");
            return ResponseEntity.ok(response); // 200 OK
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage(), e.getStackTrace(), e);
            response.put("mensaje", "No se encontró la categoría con ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e.getStackTrace(), e);
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