package com.appstock.appstock.controller;

import com.appstock.appstock.dto.MovimientoDTO;
import com.appstock.appstock.entity.Movimiento;
import com.appstock.appstock.mapper.Mapper;
import com.appstock.appstock.service.movimiento.IMovimientoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
@Tag(name = "Movimientos", description = "Gestión de movimientos de stock (entradas y salidas)")
public class MovimientoController {
    @Autowired
    private IMovimientoService movimientoService;
    @Autowired
    private Mapper mapper;

    @Operation(summary = "Obtener todos los movimientos", description = "Retorna una lista de todos los movimientos de stock registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de movimientos obtenida exitosamente")
    })
    @GetMapping("/movimientos")
    public ResponseEntity<List<MovimientoDTO>> getMovimientos(){
        List<Movimiento> movimientos = movimientoService.getMovimientos();

        List<MovimientoDTO> movimientosDTO = mapper.mapList(movimientos, MovimientoDTO.class);

        return ResponseEntity.ok(movimientosDTO);
    }

    @Operation(summary = "Obtener movimiento por ID", description = "Retorna un movimiento específico según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento encontrado"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    @GetMapping("/movimiento/{id}")
    public ResponseEntity<MovimientoDTO> getMovimiento(
            @Parameter(description = "ID del movimiento a buscar") @PathVariable("id") Long id){
        try {
            Movimiento movimiento = movimientoService.getMovimientoById(id);

            MovimientoDTO movimientoDTO = mapper.mapType(movimiento, MovimientoDTO.class);

            return ResponseEntity.ok(movimientoDTO);
        }  catch (Exception e) {
            throw new RuntimeException("Error al obtener la movimiento con id: " + id, e);
        }
    }

    @Operation(summary = "Crear nuevo movimiento", description = "Registra un nuevo movimiento de stock (entrada o salida)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movimiento creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/movimiento")
    public ResponseEntity<MovimientoDTO> createMovimiento(
            @Parameter(description = "Datos del movimiento a registrar") @RequestBody MovimientoDTO movimientoDTO){
        try {
            Movimiento savedMovimiento = movimientoService.addMovimiento(mapper.mapType(movimientoDTO, Movimiento.class));
            MovimientoDTO responseMovimiento = mapper.mapType(savedMovimiento, MovimientoDTO.class);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseMovimiento);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la movimiento");
        }
    }

    @Operation(summary = "Actualizar movimiento", description = "Actualiza los datos de un movimiento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/movimiento/{id}")
    public ResponseEntity<?> updateMovimiento(
            @Parameter(description = "ID del movimiento a actualizar") @PathVariable("id") Long id,
            @Parameter(description = "Nuevos datos del movimiento") @RequestBody MovimientoDTO movimientoDTO){
        Movimiento movimiento = null;
        try {
            movimiento = movimientoService.updateMovimiento(id, movimientoDTO);

            return new ResponseEntity<>(movimiento, HttpStatus.OK);
        } catch (Exception e){
            throw new RuntimeException("Error al actualizar la movimiento");
        }
    }

    @Operation(summary = "Eliminar movimiento", description = "Elimina un movimiento del registro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error al eliminar el movimiento")
    })
    @DeleteMapping("/movimiento/{id}")
    public ResponseEntity<Map<String, Object>> deleteMovimiento(
            @Parameter(description = "ID del movimiento a eliminar") @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            movimientoService.deleteMovimiento(id);
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
