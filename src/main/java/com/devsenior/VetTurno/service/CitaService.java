package com.devsenior.VetTurno.service;

import com.devsenior.VetTurno.exception.NegocioException;
import com.devsenior.VetTurno.model.Cita;
import com.devsenior.VetTurno.model.Mascota;
import com.devsenior.VetTurno.model.Veterinario;
import com.devsenior.VetTurno.repository.CitaRepository;
import com.devsenior.VetTurno.repository.MascotaRepository;
import com.devsenior.VetTurno.repository.VeterinarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CitaService {
    private final CitaRepository citaRepository;
    private final MascotaRepository mascotaRepository;
    private final VeterinarioRepository veterinarioRepository;

    public CitaService(CitaRepository citaRepository,
                       MascotaRepository mascotaRepository,
                       VeterinarioRepository veterinarioRepository) {
        this.citaRepository = citaRepository;
        this.mascotaRepository = mascotaRepository;
        this.veterinarioRepository = veterinarioRepository;
    }

    public List<Cita> listarCitas() {
        return citaRepository.findAll();
    }

    public List<Cita> listarPorVeterinario(Long veterinarioId) {
        return citaRepository.findByVeterinarioId(veterinarioId);
    }

    public Cita agendarCita(Cita cita) {
        resolverMascota(cita);
        resolverVeterinario(cita);
        validarFechaFutura(cita.getFechaHora());
        validarHorarioDisponible(cita);
        return citaRepository.save(cita);
    }

    private void resolverMascota(Cita cita) {
        if (cita.getMascota() != null && cita.getMascota().getId() != null) {
            Mascota mascota = mascotaRepository
                    .findById(cita.getMascota().getId())
                    .orElseThrow(() -> new NegocioException(
                            "Mascota no encontrada con id " + cita.getMascota().getId()));
            cita.setMascota(mascota);
        }
    }

    private void resolverVeterinario(Cita cita) {
        if (cita.getVeterinario() != null && cita.getVeterinario().getId() != null) {
            Veterinario veterinario = veterinarioRepository
                    .findById(cita.getVeterinario().getId())
                    .orElseThrow(() -> new NegocioException(
                            "Veterinario no encontrado con id " + cita.getVeterinario().getId()));
            cita.setVeterinario(veterinario);
        }
    }

    private void validarFechaFutura(LocalDateTime fechaHora) {
        if (fechaHora == null || !fechaHora.isAfter(LocalDateTime.now())) {
            throw new NegocioException("La fecha de la cita debe ser futura");
        }
    }

    private void validarHorarioDisponible(Cita cita) {
        citaRepository
                .findByVeterinarioIdAndFechaHora(cita.getVeterinario().getId(), cita.getFechaHora())
                .ifPresent(existente -> {
                    throw new NegocioException(
                            "El veterinario ya tiene una cita agendada en ese horario");
                });
    }
}
