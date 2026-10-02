package com.devsenior.VetTurno.controller;

import com.devsenior.VetTurno.dto.CitaDTO;
import com.devsenior.VetTurno.dto.CitaRequest;
import com.devsenior.VetTurno.model.Cita;
import com.devsenior.VetTurno.model.Mascota;
import com.devsenior.VetTurno.model.Veterinario;
import com.devsenior.VetTurno.service.CitaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {
    private final CitaService citaService;

    public CitaController(CitaService s) {
        this.citaService = s;
    }

    @GetMapping
    public List<CitaDTO> obtenerCitas() {
        return citaService.listarCitas().stream()
                .map(CitaDTO::new).toList();
    }

    @GetMapping("/veterinario/{id}")
    public List<CitaDTO> obtenerCitasPorVeterinario(@PathVariable Long id) {
        return citaService.listarPorVeterinario(id).stream()
                .map(CitaDTO::new).toList();
    }

    @PostMapping
    public ResponseEntity<CitaDTO> agendarCita(@RequestBody CitaRequest req) {
        Cita c = new Cita();
        c.setFechaHora(req.getFechaHora());
        c.setMotivo(req.getMotivo());
        Mascota mascota = new Mascota();
        mascota.setId(req.getMascotaId());
        c.setMascota(mascota);
        Veterinario veterinario = new Veterinario();
        veterinario.setId(req.getVeterinarioId());
        c.setVeterinario(veterinario);
        Cita guardada = citaService.agendarCita(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CitaDTO(guardada));
    }
}
