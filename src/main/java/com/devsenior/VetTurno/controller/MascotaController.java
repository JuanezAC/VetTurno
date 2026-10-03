package com.devsenior.VetTurno.controller;

import com.devsenior.VetTurno.dto.MascotaDTO;
import com.devsenior.VetTurno.dto.MascotaRequest;
import com.devsenior.VetTurno.model.Mascota;
import com.devsenior.VetTurno.model.Propietario;
import com.devsenior.VetTurno.service.MascotaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {
    private final MascotaService mascotaService;

    public MascotaController(MascotaService s) {
        this.mascotaService = s;
    }

    @GetMapping
    public List<MascotaDTO> obtenerMascotas() {
        return mascotaService.listarMascotas().stream()
                .map(MascotaDTO::new).toList();
    }

    @PostMapping
    public ResponseEntity<MascotaDTO> crearMascota(@Valid @RequestBody MascotaRequest req) {
        Mascota m = new Mascota();
        m.setNombre(req.getNombre());
        m.setEspecie(req.getEspecie());
        m.setRaza(req.getRaza());
        Propietario propietario = new Propietario();
        propietario.setId(req.getPropietarioId());
        m.setPropietario(propietario);
        Mascota guardada = mascotaService.crearMascota(m);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MascotaDTO(guardada));
    }
}
