package com.devsenior.VetTurno.dto;

import com.devsenior.VetTurno.model.Mascota;

public class MascotaDTO {
    private Long id;
    private String nombre;
    private String especie;
    private String raza;
    private Long propietarioId;
    private String propietarioNombre;

    public MascotaDTO(Mascota mascota) {
        this.id = mascota.getId();
        this.nombre = mascota.getNombre();
        this.especie = mascota.getEspecie();
        this.raza = mascota.getRaza();
        this.propietarioId = mascota.getPropietario() != null
                ? mascota.getPropietario().getId() : null;
        this.propietarioNombre = mascota.getPropietario() != null
                ? mascota.getPropietario().getNombre() : null;
    }

    // getters

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public String getRaza() {
        return raza;
    }

    public Long getPropietarioId() {
        return propietarioId;
    }

    public String getPropietarioNombre() {
        return propietarioNombre;
    }
}
