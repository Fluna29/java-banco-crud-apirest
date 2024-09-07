package com.banco.banco.Repositories;

import com.banco.banco.Entities.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    List<Cuenta> findByPersonaDni(String dni);
    boolean existsByIban(String iban);
}
