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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class MovimientoController {
    @Autowired
    private IMovimientoService movimientoService;
    @Autowired
    private Mapper mapper;

    @GetMapping("/movimientos")
    public ResponseEntity<List<MovimientoDTO>> getMovimientos(){
        List<Movimiento> movimientos = movimientoService.getMovimientos();

        List<MovimientoDTO> movimientosDTO = mapper.mapList(movimientos, MovimientoDTO.class);

        return ResponseEntity.ok(movimientosDTO);
    }

    @GetMapping("/movimiento/{id}")
    public ResponseEntity<MovimientoDTO> getMovimiento(@PathVariable("id") Long id){
        try {
            Movimiento movimiento = movimientoService.getMovimientoById(id);

            MovimientoDTO movimientoDTO = mapper.mapType(movimiento, MovimientoDTO.class);

            return ResponseEntity.ok(movimientoDTO);
        }  catch (Exception e) {
            throw new RuntimeException("Error al obtener la movimiento con id: " + id, e);
        }
    }

    @PostMapping("/movimiento")
    public ResponseEntity<MovimientoDTO> createMovimiento(@RequestBody MovimientoDTO movimientoDTO){
        try {
            Movimiento savedMovimiento = movimientoService.addMovimiento(mapper.mapType(movimientoDTO, Movimiento.class));
            MovimientoDTO responseMovimiento = mapper.mapType(savedMovimiento, MovimientoDTO.class);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseMovimiento);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la movimiento");
        }
    }

    @PutMapping("/movimiento/{id}")
    public ResponseEntity<?> updateMovimiento(@PathVariable("id") Long id, @RequestBody MovimientoDTO movimientoDTO){
        Movimiento movimiento = null;
        try {
            movimiento = movimientoService.updateMovimiento(id, movimientoDTO);

            return new ResponseEntity<>(movimiento, HttpStatus.OK);
        } catch (Exception e){
            throw new RuntimeException("Error al actualizar la movimiento");
        }
    }

    @DeleteMapping("/movimiento/{id}")
    public ResponseEntity<Map<String, Object>> deleteMovimiento(@PathVariable Long id) {
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
