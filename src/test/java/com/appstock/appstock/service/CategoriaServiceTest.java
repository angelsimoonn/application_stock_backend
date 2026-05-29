package com.appstock.appstock.service;

import com.appstock.appstock.dto.CategoriaDTO;
import com.appstock.appstock.entity.Categoria;
import com.appstock.appstock.repository.categoria.ICategoriaRepository;
import com.appstock.appstock.service.categoria.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private ICategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónica");
        categoria.setDescripcion("Productos electrónicos");
    }

    @Test
    void testGetCategorias() {
        List<Categoria> categorias = Arrays.asList(categoria);
        when(categoriaRepository.findAll()).thenReturn(categorias);

        List<Categoria> result = categoriaService.getCategorias();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electrónica", result.get(0).getNombre());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void testGetCategoriaById() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        Categoria result = categoriaService.getCategoriaById(1L);

        assertNotNull(result);
        assertEquals("Electrónica", result.getNombre());
        verify(categoriaRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCategoriaByIdNotFound() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        Categoria result = categoriaService.getCategoriaById(99L);

        assertNull(result);
        verify(categoriaRepository, times(1)).findById(99L);
    }

    @Test
    void testAddCategoria() {
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        Categoria result = categoriaService.addCategoria(categoria);

        assertNotNull(result);
        assertEquals("Electrónica", result.getNombre());
        verify(categoriaRepository, times(1)).save(categoria);
    }

    @Test
    void testUpdateCategoria() {
        CategoriaDTO categoriaDTO = new CategoriaDTO();
        categoriaDTO.setNombre("Electrónica Actualizada");
        categoriaDTO.setDescripcion("Nueva descripción");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        Categoria result = categoriaService.updateCategoria(1L, categoriaDTO);

        assertNotNull(result);
        assertEquals("Electrónica Actualizada", result.getNombre());
        assertEquals("Nueva descripción", result.getDescripcion());
        verify(categoriaRepository, times(1)).findById(1L);
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    void testDeleteCategoria() throws Exception {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoriaRepository).deleteById(1L);

        assertDoesNotThrow(() -> categoriaService.deleteCategoria(1L));

        verify(categoriaRepository, times(1)).existsById(1L);
        verify(categoriaRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCategoriaNotFound() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);

        assertThrows(Exception.class, () -> categoriaService.deleteCategoria(99L));

        verify(categoriaRepository, times(1)).existsById(99L);
        verify(categoriaRepository, never()).deleteById(99L);
    }
}
