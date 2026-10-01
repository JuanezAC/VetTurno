package com.devsenior.VetTurno.repository;

import com.devsenior.VetTurno.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeterinarioRepository extends JpaRepository <Veterinario, Long> {
}
