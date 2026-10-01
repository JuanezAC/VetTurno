package com.devsenior.VetTurno.dto;

import com.devsenior.VetTurno.model.Veterinario;

public class VeterinarioDTO {
    private Long id;
    private String nombre;
    private String especialidad;

    public VeterinarioDTO(Veterinario veterinario) {
        this.id = veterinario.getId();
        this.nombre = veterinario.getNombre();
        this.especialidad = veterinario.getEspecialidad();
    }

    // getters

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }
}
