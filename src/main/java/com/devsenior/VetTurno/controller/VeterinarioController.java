package com.devsenior.VetTurno.controller;

import com.devsenior.VetTurno.dto.VeterinarioDTO;
import com.devsenior.VetTurno.dto.VeterinarioRequest;
import com.devsenior.VetTurno.model.Veterinario;
import com.devsenior.VetTurno.service.VeterinarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veterinarios")
@Tag(name = "Veterinarios", description = "Alta solo ADMIN y consulta de veterinarios")
public class VeterinarioController {
    private final VeterinarioService veterinarioService;

    public VeterinarioController(VeterinarioService s) {
        this.veterinarioService = s;
    }

    @GetMapping
    @Operation(summary = "Listar todos los veterinarios")
    public List<VeterinarioDTO> obtenerVeterinarios() {
        return veterinarioService.listarVeterinarios().stream()
                .map(VeterinarioDTO::new).toList();
    }

    @PostMapping
    @Operation(summary = "Crear un veterinario (solo rol ADMIN)")
    public ResponseEntity<VeterinarioDTO> crearVeterinario(@Valid @RequestBody VeterinarioRequest req) {
        Veterinario v = new Veterinario();
        v.setNombre(req.getNombre());
        v.setEspecialidad(req.getEspecialidad());
        Veterinario guardado = veterinarioService.crearVeterinario(v);
        return ResponseEntity.status(HttpStatus.CREATED).body(new VeterinarioDTO(guardado));
    }
}
