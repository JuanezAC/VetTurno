package com.devsenior.VetTurno.repository;

import com.devsenior.VetTurno.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MascotaRepository extends JpaRepository <Mascota, Long> {
}
