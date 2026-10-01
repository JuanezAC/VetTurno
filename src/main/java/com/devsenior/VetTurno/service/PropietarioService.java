package com.devsenior.VetTurno.service;

import com.devsenior.VetTurno.model.Propietario;
import com.devsenior.VetTurno.repository.PropietarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropietarioService {
    private final PropietarioRepository propietarioRepository;

    public PropietarioService(PropietarioRepository propietarioRepository) {
        this.propietarioRepository = propietarioRepository;
    }

    public List<Propietario> listarPropietarios() {
        return propietarioRepository.findAll();
    }

    public Propietario crearPropietario(Propietario propietario) {
        return propietarioRepository.save(propietario);
    }
}
