package com.victor.paciente.controller;

import com.victor.comons.controller.CommonController;
import com.victor.comons.dto.pacientes.PacienteRequest;
import com.victor.comons.dto.pacientes.PacienteResponse;
import com.victor.paciente.service.PacienteService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class PacienteController extends CommonController<PacienteRequest, PacienteResponse, PacienteService> {
    public PacienteController(PacienteService service) {
        super(service);
    }

    @GetMapping("/id-paciente/{id}")
    public ResponseEntity<PacienteResponse> obtenerPacientePorIdSinEstado(@PathVariable @Positive(message = "El id debe ser positivo") Long id){
        return ResponseEntity.ok(service.obtenerPacientePorIdSinEstado(id));
    }
}