package com.banco.banco.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.banco.banco.Entities.Persona;

public interface PersonaRepository extends JpaRepository<Persona, String> {

    // Método para buscar una persona por su dni
    Optional<Persona> findByDni(String dni);

    // Método para verificar si ya existe una persona con un dni
    boolean existsByDni(String dni);
}
