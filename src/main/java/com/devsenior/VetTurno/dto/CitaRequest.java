package com.devsenior.VetTurno.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class CitaRequest {

    @NotNull(message = "La fecha y hora son obligatorias")
    @Future(message = "La fecha de la cita debe ser futura")
    private LocalDateTime fechaHora;

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;

    @NotNull(message = "La mascota es obligatoria")
    @Positive(message = "El mascotaId debe ser mayor a cero")
    private Long mascotaId;

    @NotNull(message = "El veterinario es obligatorio")
    @Positive(message = "El veterinarioId debe ser mayor a cero")
    private Long veterinarioId;

    // getters y setters

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Long getMascotaId() {
        return mascotaId;
    }

    public void setMascotaId(Long mascotaId) {
        this.mascotaId = mascotaId;
    }

    public Long getVeterinarioId() {
        return veterinarioId;
    }

    public void setVeterinarioId(Long veterinarioId) {
        this.veterinarioId = veterinarioId;
    }
}
