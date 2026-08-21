package com.victor.citas.service;

import com.victor.citas.dto.CitaRequest;
import com.victor.citas.dto.CitaResponse;
import com.victor.comons.service.CrudService;

public interface CitaService  extends CrudService<CitaRequest, CitaResponse> {

    void actualizarEstadoCita(Long idCita, Long idEstadoCita);

    boolean tieneCitaConfirmadaOEnCursoMedico(Long idMedico);

}