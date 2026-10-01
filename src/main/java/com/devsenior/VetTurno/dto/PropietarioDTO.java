package com.devsenior.VetTurno.dto;

import com.devsenior.VetTurno.model.Propietario;

public class PropietarioDTO {
    private Long id;
    private String nombre;
    private String telefono;
    private String email;

    public PropietarioDTO(Propietario propietario) {
        this.id = propietario.getId();
        this.nombre = propietario.getNombre();
        this.telefono = propietario.getTelefono();
        this.email = propietario.getEmail();
    }

    // getters

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }
}
