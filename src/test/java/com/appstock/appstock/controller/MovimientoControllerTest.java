package com.appstock.appstock.controller;

import com.appstock.appstock.dto.MovimientoDTO;
import com.appstock.appstock.entity.Movimiento;
import com.appstock.appstock.entity.TipoMovimiento;
import com.appstock.appstock.mapper.Mapper;
import com.appstock.appstock.service.movimiento.IMovimientoService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MovimientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IMovimientoService movimientoService;

    @MockBean
    private Mapper mapper;

    private Movimiento movimiento;
    private MovimientoDTO movimientoDTO;

    @BeforeEach
    void setUp() {
        movimiento = new Movimiento();
        movimiento.setId(1L);
        movimiento.setDescripcion("Entrada de productos");
        movimiento.setTipoMovimiento(TipoMovimiento.ENTRADA);
        movimiento.setFecha(LocalDateTime.now());

        movimientoDTO = new MovimientoDTO();
        movimientoDTO.setDescripcion("Entrada de productos");
        movimientoDTO.setTipoMovimiento(TipoMovimiento.ENTRADA);
        movimientoDTO.setFecha(LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetMovimientos() throws Exception {
        List<Movimiento> movimientos = Arrays.asList(movimiento);
        List<MovimientoDTO> movimientosDTO = Arrays.asList(movimientoDTO);

        when(movimientoService.getMovimientos()).thenReturn(movimientos);
        when(mapper.mapList(movimientos, MovimientoDTO.class)).thenReturn(movimientosDTO);

        mockMvc.perform(get("/api/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descripcion").value("Entrada de productos"))
                .andExpect(jsonPath("$[0].tipoMovimiento").value("ENTRADA"));

        verify(movimientoService, times(1)).getMovimientos();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetMovimientoById() throws Exception {
        when(movimientoService.getMovimientoById(1L)).thenReturn(movimiento);
        when(mapper.mapType(movimiento, MovimientoDTO.class)).thenReturn(movimientoDTO);

        mockMvc.perform(get("/api/movimiento/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion").value("Entrada de productos"))
                .andExpect(jsonPath("$.tipoMovimiento").value("ENTRADA"));

        verify(movimientoService, times(1)).getMovimientoById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateMovimiento() throws Exception {
        when(mapper.mapType(any(MovimientoDTO.class), eq(Movimiento.class))).thenReturn(movimiento);
        when(movimientoService.addMovimiento(any(Movimiento.class))).thenReturn(movimiento);
        when(mapper.mapType(any(Movimiento.class), eq(MovimientoDTO.class))).thenReturn(movimientoDTO);

        mockMvc.perform(post("/api/movimiento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movimientoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descripcion").value("Entrada de productos"));

        verify(movimientoService, times(1)).addMovimiento(any(Movimiento.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateMovimiento() throws Exception {
        when(movimientoService.updateMovimiento(eq(1L), any(MovimientoDTO.class))).thenReturn(movimiento);

        mockMvc.perform(put("/api/movimiento/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movimientoDTO)))
                .andExpect(status().isOk());

        verify(movimientoService, times(1)).updateMovimiento(eq(1L), any(MovimientoDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteMovimiento() throws Exception {
        doNothing().when(movimientoService).deleteMovimiento(1L);

        mockMvc.perform(delete("/api/movimiento/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").exists());

        verify(movimientoService, times(1)).deleteMovimiento(1L);
    }
}
