package com.appstock.appstock.controller;

import com.appstock.appstock.dto.ProductoDTO;
import com.appstock.appstock.entity.Categoria;
import com.appstock.appstock.entity.Producto;
import com.appstock.appstock.mapper.Mapper;
import com.appstock.appstock.service.categoria.ICategoriaService;
import com.appstock.appstock.service.producto.IProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IProductoService productoService;

    @MockBean
    private ICategoriaService categoriaService;

    @MockBean
    private Mapper mapper;

    private Producto producto;
    private ProductoDTO productoDTO;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónica");
        categoria.setDescripcion("Productos electrónicos");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");
        producto.setDescripcion("Laptop gaming");
        producto.setPrecio(new BigDecimal("1500.00"));
        producto.setStock(10);
        producto.setCategoria(categoria);

        productoDTO = new ProductoDTO();
        productoDTO.setId(1L);
        productoDTO.setNombre("Laptop");
        productoDTO.setDescripcion("Laptop gaming");
        productoDTO.setPrecio(new BigDecimal("1500.00"));
        productoDTO.setStock(10);
        productoDTO.setCategoriaId(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetProductos() throws Exception {
        List<Producto> productos = Arrays.asList(producto);
        List<ProductoDTO> productosDTO = Arrays.asList(productoDTO);

        when(productoService.getProductos()).thenReturn(productos);
        when(mapper.mapList(productos, ProductoDTO.class)).thenReturn(productosDTO);

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Laptop"))
                .andExpect(jsonPath("$[0].precio").value(1500.00));

        verify(productoService, times(1)).getProductos();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetProductoById() throws Exception {
        when(productoService.getProductoById(1L)).thenReturn(producto);
        when(mapper.mapType(producto, ProductoDTO.class)).thenReturn(productoDTO);

        mockMvc.perform(get("/api/producto/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laptop"))
                .andExpect(jsonPath("$.stock").value(10));

        verify(productoService, times(1)).getProductoById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProducto() throws Exception {
        when(mapper.mapType(any(ProductoDTO.class), eq(Producto.class))).thenReturn(producto);
        when(categoriaService.getCategoriaById(1L)).thenReturn(categoria);
        when(productoService.addProducto(any(Producto.class))).thenReturn(producto);
        when(mapper.mapType(any(Producto.class), eq(ProductoDTO.class))).thenReturn(productoDTO);

        mockMvc.perform(post("/api/producto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Laptop"));

        verify(productoService, times(1)).addProducto(any(Producto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateProducto() throws Exception {
        when(productoService.getProductoById(1L)).thenReturn(producto);
        when(categoriaService.getCategoriaById(1L)).thenReturn(categoria);
        when(productoService.addProducto(any(Producto.class))).thenReturn(producto);
        when(mapper.mapType(any(Producto.class), eq(ProductoDTO.class))).thenReturn(productoDTO);

        mockMvc.perform(put("/api/producto/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laptop"));

        verify(productoService, times(1)).getProductoById(1L);
        verify(productoService, times(1)).addProducto(any(Producto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteProducto() throws Exception {
        doNothing().when(productoService).deleteProducto(1L);

        mockMvc.perform(delete("/api/producto/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").exists());

        verify(productoService, times(1)).deleteProducto(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetProductosPorCategoria() throws Exception {
        List<Producto> productos = Arrays.asList(producto);
        List<ProductoDTO> productosDTO = Arrays.asList(productoDTO);

        when(productoService.getProductosPorCategoria(1L)).thenReturn(productos);
        when(mapper.mapList(productos, ProductoDTO.class)).thenReturn(productosDTO);

        mockMvc.perform(get("/api/productos/categoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Laptop"));

        verify(productoService, times(1)).getProductosPorCategoria(1L);
    }
}
