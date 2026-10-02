package com.devsenior.VetTurno.dto;

import com.devsenior.VetTurno.model.Cita;
import com.devsenior.VetTurno.model.Mascota;
import com.devsenior.VetTurno.model.Propietario;
import com.devsenior.VetTurno.model.Veterinario;

import java.time.LocalDateTime;

public class CitaDTO {
    private Long id;
    private LocalDateTime fechaHora;
    private String motivo;
    private MascotaResumen mascota;
    private PropietarioResumen propietario;
    private VeterinarioResumen veterinario;

    public CitaDTO(Cita cita) {
        this.id = cita.getId();
        this.fechaHora = cita.getFechaHora();
        this.motivo = cita.getMotivo();
        this.mascota = cita.getMascota() != null
                ? new MascotaResumen(cita.getMascota()) : null;
        this.propietario = cita.getMascota() != null
                && cita.getMascota().getPropietario() != null
                ? new PropietarioResumen(cita.getMascota().getPropietario()) : null;
        this.veterinario = cita.getVeterinario() != null
                ? new VeterinarioResumen(cita.getVeterinario()) : null;
    }

    // getters

    public Long getId() {
        return id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public MascotaResumen getMascota() {
        return mascota;
    }

    public PropietarioResumen getPropietario() {
        return propietario;
    }

    public VeterinarioResumen getVeterinario() {
        return veterinario;
    }

    public static class MascotaResumen {
        private Long id;
        private String nombre;

        public MascotaResumen(Mascota mascota) {
            this.id = mascota.getId();
            this.nombre = mascota.getNombre();
        }

        public Long getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }
    }

    public static class PropietarioResumen {
        private Long id;
        private String nombre;

        public PropietarioResumen(Propietario propietario) {
            this.id = propietario.getId();
            this.nombre = propietario.getNombre();
        }

        public Long getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }
    }

    public static class VeterinarioResumen {
        private Long id;
        private String nombre;

        public VeterinarioResumen(Veterinario veterinario) {
            this.id = veterinario.getId();
            this.nombre = veterinario.getNombre();
        }

        public Long getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }
    }
}
