package com.victor.citas.service;

import com.victor.citas.dto.CitaRequest;
import com.victor.citas.dto.CitaResponse;
import com.victor.citas.entity.Cita;
import com.victor.citas.enums.EstadoCita;
import com.victor.citas.mapper.CitaMapper;
import com.victor.citas.repository.CitaRepository;
import com.victor.comons.client.MedicoClient;
import com.victor.comons.dto.medicos.MedicoResponse;
import com.victor.comons.enums.EstadoRegistro;
import com.victor.comons.exceptions.RecursoNoEncontradoException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class CitaServiceImpl implements CitaService{
    private final CitaRepository citaRepository;
    private final CitaMapper citaMapper;
    private final MedicoClient medicoClient;

    @Override
    public void actulizarEstadoCita(Long idCita, Long idEstadoCita) {
        Cita cita = obtenerCitaOrException(idCita);
        cita.actualizarEstadoCita(EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita));

    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listar() {
        log.info("Listando citas activas...");
        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(cita -> citaMapper
                        .entidadAResponse(cita, null, obtenerMedicoSinEstado(cita.getId()))).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorId(Long id) {
        Cita cita = obtenerCitaOrException(id);
        return citaMapper.entidadAResponse(cita, null, obtenerMedicoSinEstado(cita.getIdMedico()));
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {
        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
        Cita cita = citaMapper.requestAEntidad(request);
        citaRepository.save(cita);
        return citaMapper.entidadAResponse(cita, null, medico);
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {
        Cita cita = obtenerCitaOrException(id);
        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());

        log.info("Actualizando cita con id {}", id);

        cita.actualizar(request.idPaciente(), request.idMedico(), request.fechaCita(), request.sintomas());

        return citaMapper.entidadAResponse(cita, null, medico);
    }

    @Override
    public void eliminar(Long id) {
        Cita cita = obtenerCitaOrException(id);
        cita.eliminar();
    }

    private Cita obtenerCitaOrException(Long id){
        log.info("Consultando la cita solicitada...");
        return citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita no encontrada con id: " + id));
    }

    private MedicoResponse obtenerMedicoActivo(Long id){
        return medicoClient.obtenerMedicoActivoPorId(id);
    }

    private MedicoResponse obtenerMedicoSinEstado(Long id){
        return medicoClient.obtenerMedicoPorIdSinEstado(id);
    }
}