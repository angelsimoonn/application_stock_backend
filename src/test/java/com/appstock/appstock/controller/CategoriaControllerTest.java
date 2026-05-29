package com.appstock.appstock.controller;

import com.appstock.appstock.dto.CategoriaDTO;
import com.appstock.appstock.entity.Categoria;
import com.appstock.appstock.mapper.Mapper;
import com.appstock.appstock.service.categoria.ICategoriaService;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICategoriaService categoriaService;

    @MockBean
    private Mapper mapper;

    private Categoria categoria;
    private CategoriaDTO categoriaDTO;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónica");
        categoria.setDescripcion("Productos electrónicos");

        categoriaDTO = new CategoriaDTO();
        categoriaDTO.setId(1L);
        categoriaDTO.setNombre("Electrónica");
        categoriaDTO.setDescripcion("Productos electrónicos");
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetCategorias() throws Exception {
        List<Categoria> categorias = Arrays.asList(categoria);
        List<CategoriaDTO> categoriasDTO = Arrays.asList(categoriaDTO);

        when(categoriaService.getCategorias()).thenReturn(categorias);
        when(mapper.mapList(categorias, CategoriaDTO.class)).thenReturn(categoriasDTO);

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Electrónica"));

        verify(categoriaService, times(1)).getCategorias();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetCategoriaById() throws Exception {
        when(categoriaService.getCategoriaById(1L)).thenReturn(categoria);
        when(mapper.mapType(categoria, CategoriaDTO.class)).thenReturn(categoriaDTO);

        mockMvc.perform(get("/api/categoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Electrónica"));

        verify(categoriaService, times(1)).getCategoriaById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateCategoria() throws Exception {
        when(mapper.mapType(any(CategoriaDTO.class), eq(Categoria.class))).thenReturn(categoria);
        when(categoriaService.addCategoria(any(Categoria.class))).thenReturn(categoria);
        when(mapper.mapType(any(Categoria.class), eq(CategoriaDTO.class))).thenReturn(categoriaDTO);

        mockMvc.perform(post("/api/categoria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Electrónica"));

        verify(categoriaService, times(1)).addCategoria(any(Categoria.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateCategoria() throws Exception {
        when(categoriaService.updateCategoria(eq(1L), any(CategoriaDTO.class))).thenReturn(categoria);

        mockMvc.perform(put("/api/categoria/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaDTO)))
                .andExpect(status().isOk());

        verify(categoriaService, times(1)).updateCategoria(eq(1L), any(CategoriaDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteCategoria() throws Exception {
        doNothing().when(categoriaService).deleteCategoria(1L);

        mockMvc.perform(delete("/api/categoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").exists());

        verify(categoriaService, times(1)).deleteCategoria(1L);
    }
}
