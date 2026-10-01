package com.devsenior.VetTurno.repository;

import com.devsenior.VetTurno.model.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropietarioRepository extends JpaRepository <Propietario, Long> {
}
