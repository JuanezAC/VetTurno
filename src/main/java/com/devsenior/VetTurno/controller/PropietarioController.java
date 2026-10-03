package com.devsenior.VetTurno.controller;

import com.devsenior.VetTurno.dto.PropietarioDTO;
import com.devsenior.VetTurno.dto.PropietarioRequest;
import com.devsenior.VetTurno.model.Propietario;
import com.devsenior.VetTurno.service.PropietarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/propietarios")
@Tag(name = "Propietarios", description = "Consulta y registro de dueños de mascotas")
public class PropietarioController {
    private final PropietarioService propietarioService;

    public PropietarioController(PropietarioService s) {
        this.propietarioService = s;
    }

    @GetMapping
    @Operation(summary = "Listar todos los propietarios")
    public List<PropietarioDTO> obtenerPropietarios() {
        return propietarioService.listarPropietarios().stream()
                .map(PropietarioDTO::new).toList();
    }

    @PostMapping
    @Operation(summary = "Crear un propietario (requiere token)")
    public ResponseEntity<PropietarioDTO> crearPropietario(@Valid @RequestBody PropietarioRequest req) {
        Propietario p = new Propietario();
        p.setNombre(req.getNombre());
        p.setTelefono(req.getTelefono());
        p.setEmail(req.getEmail());
        Propietario guardado = propietarioService.crearPropietario(p);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PropietarioDTO(guardado));
    }
}
