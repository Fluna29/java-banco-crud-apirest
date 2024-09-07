package com.banco.banco.Controllers;

import org.springframework.web.bind.annotation.RestController;
import com.banco.banco.Repositories.PersonaRepository;
import com.banco.banco.Entities.Persona;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/usuario")
public class PersonaController {

    @Autowired
    private PersonaRepository personaRepository;
    
    @GetMapping
    public List<Persona> getAllPersonas() {
        return personaRepository.findAll();
    }

    @GetMapping("/{dni}")
    public Persona getUsuarioPorDni(@PathVariable String dni) {
        return personaRepository.findByDni(dni)
            .orElseThrow(() -> new RuntimeException("Persona no encontrada con el DNI: " + dni));
    }

    @PostMapping("/crearUsuario")
    public String crearUsuario(@RequestBody Persona persona) {
        personaRepository.save(persona);
        return "Usuario creado";
    }

    @PutMapping("/{dni}/editarUsuario")
    public Persona editarUsuario(@PathVariable String dni, @RequestBody Persona detallesPersona) {
        Persona persona = personaRepository.findByDni(dni)
            .orElseThrow(() -> new RuntimeException("Persona no encontrada con el DNI: " + dni));
        
        persona.setNombre(detallesPersona.getNombre());
        persona.setApellido(detallesPersona.getApellido());
        persona.setDireccion(detallesPersona.getDireccion());
        persona.setTelefono(detallesPersona.getTelefono());

        return personaRepository.save(persona);
    }

    @PutMapping("/{dni}/cambiarDni")
    public Persona cambiarDni(@PathVariable String dni, @RequestParam String nuevoDni) {
        Persona persona = personaRepository.findByDni(dni)
            .orElseThrow(() -> new RuntimeException("Persona no encontrada con el DNI: " + dni));
        
        // Verificar si el nuevo DNI ya existe
        if (personaRepository.existsByDni(nuevoDni)) {
            throw new RuntimeException("El nuevo DNI ya está en uso.");
        }
        
        // Cambiar el DNI de la persona
        persona.setDni(nuevoDni);
        return personaRepository.save(persona);
    }

    @DeleteMapping("/{dni}/eliminarUsuario")
    public String eliminarUsuario(@PathVariable String dni) {
        Persona persona = personaRepository.findByDni(dni)
            .orElseThrow(() -> new RuntimeException("Persona no encontrada con el DNI: " + dni));
        personaRepository.delete(persona);
        return "Usuario con DNI: " + persona.getDni() + " eliminado";
    }
}
