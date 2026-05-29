package com.appstock.appstock.service;

import com.appstock.appstock.dto.ProductoDTO;
import com.appstock.appstock.entity.Categoria;
import com.appstock.appstock.entity.Producto;
import com.appstock.appstock.repository.producto.IProductoRepository;
import com.appstock.appstock.service.producto.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private IProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónica");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");
        producto.setDescripcion("Laptop gaming");
        producto.setPrecio(new BigDecimal("1500.00"));
        producto.setStock(10);
        producto.setCategoria(categoria);
    }

    @Test
    void testGetProductos() {
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findAll()).thenReturn(productos);

        List<Producto> result = productoService.getProductos();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getNombre());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void testGetProductoById() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto result = productoService.getProductoById(1L);

        assertNotNull(result);
        assertEquals("Laptop", result.getNombre());
        assertEquals(10, result.getStock());
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductoByIdNotFound() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Producto result = productoService.getProductoById(99L);

        assertNull(result);
        verify(productoRepository, times(1)).findById(99L);
    }

    @Test
    void testAddProducto() {
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        Producto result = productoService.addProducto(producto);

        assertNotNull(result);
        assertEquals("Laptop", result.getNombre());
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void testUpdateProducto() {
        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setNombre("Laptop Actualizada");
        productoDTO.setDescripcion("Nueva descripción");
        productoDTO.setPrecio(new BigDecimal("1800.00"));
        productoDTO.setStock(15);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        Producto result = productoService.updateProducto(1L, productoDTO);

        assertNotNull(result);
        assertEquals("Laptop Actualizada", result.getNombre());
        assertEquals(new BigDecimal("1800.00"), result.getPrecio());
        assertEquals(15, result.getStock());
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void testDeleteProducto() throws Exception {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        doNothing().when(productoRepository).deleteById(1L);

        assertDoesNotThrow(() -> productoService.deleteProducto(1L));

        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProductoNotFound() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> productoService.deleteProducto(99L));

        verify(productoRepository, times(1)).findById(99L);
        verify(productoRepository, never()).deleteById(99L);
    }

    @Test
    void testGetProductosPorCategoria() {
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.buscarPorCategoria(1L)).thenReturn(productos);

        List<Producto> result = productoService.getProductosPorCategoria(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getNombre());
        verify(productoRepository, times(1)).buscarPorCategoria(1L);
    }
}
