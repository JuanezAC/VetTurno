package com.devsenior.VetTurno.service;

import com.devsenior.VetTurno.exception.NegocioException;
import com.devsenior.VetTurno.model.Mascota;
import com.devsenior.VetTurno.model.Propietario;
import com.devsenior.VetTurno.repository.MascotaRepository;
import com.devsenior.VetTurno.repository.PropietarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MascotaService {
    private final MascotaRepository mascotaRepository;
    private final PropietarioRepository propietarioRepository;

    public MascotaService(MascotaRepository mascotaRepository,
                          PropietarioRepository propietarioRepository) {
        this.mascotaRepository = mascotaRepository;
        this.propietarioRepository = propietarioRepository;
    }

    public List<Mascota> listarMascotas() {
        return mascotaRepository.findAll();
    }

    public Mascota crearMascota(Mascota mascota) {
        resolverPropietario(mascota);
        return mascotaRepository.save(mascota);
    }

    private void resolverPropietario(Mascota mascota) {
        if (mascota.getPropietario() != null
                && mascota.getPropietario().getId() != null) {
            Propietario propietario = propietarioRepository
                    .findById(mascota.getPropietario().getId())
                    .orElseThrow(() -> new NegocioException(
                            "Propietario no encontrado con id " + mascota.getPropietario().getId()));
            mascota.setPropietario(propietario);
        }
    }
}
