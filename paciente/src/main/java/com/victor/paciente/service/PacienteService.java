package com.victor.paciente.service;

import com.victor.comons.dto.pacientes.PacienteRequest;
import com.victor.comons.dto.pacientes.PacienteResponse;
import com.victor.comons.service.CrudService;

public interface PacienteService extends CrudService<PacienteRequest, PacienteResponse> {
PacienteResponse obtenerPacientePorIdSinEstado(Long id);

}
