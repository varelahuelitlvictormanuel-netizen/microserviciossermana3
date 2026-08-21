package com.victor.comons.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "citas")
public interface CitaCliente {
// La logica de la cita
@GetMapping("/agenda-medico/{idMedico}")
ResponseEntity<Void> validarAgendaMedico(@PathVariable Long idMedico);

@GetMapping("/agenda-paciente/{idPaciente}")
ResponseEntity<Void> validarAgendaPaciente(@PathVariable Long idPaciente);
}