package com.victor.medicos.service;

import com.victor.comons.dto.medicos.MedicoRequest;
import com.victor.comons.dto.medicos.MedicoResponse;
import com.victor.comons.service.CrudService;

public interface MedicoService extends CrudService<MedicoRequest, MedicoResponse> {
    MedicoResponse obtenerMedicoPorIdSinEstado(Long id);
    void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad);
}
