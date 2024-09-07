package com.banco.banco.Controllers;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.banco.banco.Entities.Cuenta;
import com.banco.banco.Entities.Persona;
import com.banco.banco.Repositories.PersonaRepository;
import com.banco.banco.Repositories.CuentaRepository;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/cuenta")
public class CuentaController {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @GetMapping
    public List<Cuenta> getAllCuentas() {
        return cuentaRepository.findAll();
    }

    @GetMapping("/listadocuentas/{dni}")
    public List<Cuenta> obtenerCuentasPorDni(@PathVariable String dni) {
        return cuentaRepository.findByPersonaDni(dni);
    }

    @GetMapping("/{idCuenta}")
    public Cuenta getCuentaPorNumeroCuenta(@PathVariable Long idCuenta) {
        return cuentaRepository.findById(idCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con el numero de cuenta: " + idCuenta));
    }

    @PostMapping("/crearCuenta")
    public Cuenta crearCuenta(@RequestBody Map<String, Object> cuentaData) {
        String iban = (String) cuentaData.get("iban");
        double saldo = Double.parseDouble(cuentaData.get("saldo").toString());
        String dni = (String) cuentaData.get("dni");

        // Buscar la persona por DNI
        Persona persona = personaRepository.findByDni(dni)
            .orElseThrow(() -> new RuntimeException("Persona no encontrada con el DNI: " + dni));

        // Verificar si ya existe una cuenta con el mismo IBAN
        if (cuentaRepository.existsByIban(iban)) {
            throw new RuntimeException("Ya existe una cuenta con el IBAN: " + iban);
        }

        // Crear la nueva cuenta y asociarla a la persona
        Cuenta cuenta = new Cuenta();
        cuenta.setIban(iban);
        cuenta.setSaldo(saldo);
        cuenta.setPersona(persona);

        // Guardar la cuenta en la base de datos
        return cuentaRepository.save(cuenta);
    }

    @PutMapping("editarCuenta/{idCuenta}")
    public String editarCuenta(@PathVariable Long idCuenta, @RequestBody Cuenta cuenta) {
        Cuenta cuentaActualizada = cuentaRepository.findById(idCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con el numero de cuenta: " + idCuenta));
        cuentaActualizada.setSaldo(cuenta.getSaldo());
        cuentaActualizada.setPersona(cuenta.getPersona());
        cuentaRepository.save(cuentaActualizada);
        return "Cuenta actualizada";
    }

    @DeleteMapping("/eliminarCuenta")
    public String eliminarCuenta(@RequestParam Long idCuenta) {
        Cuenta cuenta = cuentaRepository.findById(idCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con el numero de cuenta: " + idCuenta));
        cuentaRepository.delete(cuenta);
        return "Cuenta eliminada";
    }

    @PutMapping("/actualizarSaldo")
    public Cuenta actualizarSaldo(@RequestParam Long idCuenta, @RequestParam double saldo) {
        Cuenta cuenta = cuentaRepository.findById(idCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con el numero de cuenta: " + idCuenta));
        cuenta.setSaldo(saldo);
        return cuentaRepository.save(cuenta);
    }
    
    @GetMapping("/saldo")
    public double getSaldoPorNumeroCuenta(@RequestParam Long idCuenta) {
        Cuenta cuenta = cuentaRepository.findById(idCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con el numero de cuenta: " + idCuenta));
        return cuenta.getSaldo();
    }

    @GetMapping("/depositar")
    public String depositarSaldo(@RequestParam Long idCuenta, @RequestParam double monto) {
        Cuenta cuenta = cuentaRepository.findById(idCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con el numero de cuenta: " + idCuenta));
        cuenta.setSaldo(cuenta.getSaldo() + monto);
        cuentaRepository.save(cuenta);
        return "Deposito realizado";
    }

    @GetMapping("/retirar")
    public String retirarSaldo(@RequestParam Long idCuenta, @RequestParam double monto) {
        Cuenta cuenta = cuentaRepository.findById(idCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con el numero de cuenta: " + idCuenta));
        if (cuenta.getSaldo() < monto) {
            return "Saldo insuficiente";
        }
        cuenta.setSaldo(cuenta.getSaldo() - monto);
        cuentaRepository.save(cuenta);
        return "Retiro realizado";
    }

    @GetMapping("/transferir")
    public String transferirSaldo(@RequestParam Long idCuentaOrigen, @RequestParam Long idCuentaDestino,
            @RequestParam double monto) {
        Cuenta cuentaOrigen = cuentaRepository.findById(idCuentaOrigen)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con el numero de cuenta: " + idCuentaOrigen));
        Cuenta cuentaDestino = cuentaRepository.findById(idCuentaDestino)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con el numero de cuenta: " + idCuentaDestino));
        if (cuentaOrigen.getSaldo() < monto) {
            return "Saldo insuficiente";
        }
        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo() - monto);
        cuentaDestino.setSaldo(cuentaDestino.getSaldo() + monto);
        cuentaRepository.save(cuentaOrigen);
        cuentaRepository.save(cuentaDestino);
        return "Transferencia realizada";
    }
}
