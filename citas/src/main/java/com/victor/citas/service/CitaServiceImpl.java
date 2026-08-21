package com.victor.citas.service;

import com.victor.citas.dto.CitaRequest;
import com.victor.citas.dto.CitaResponse;
import com.victor.citas.entity.Cita;
import com.victor.citas.enums.EstadoCita;
import com.victor.citas.mapper.CitaMapper;
import com.victor.citas.repository.CitaRepository;
import com.victor.comons.client.MedicoClient;
import com.victor.comons.client.PacienteClient;
import com.victor.comons.dto.medicos.MedicoResponse;
import com.victor.comons.dto.pacientes.PacienteResponse;
import com.victor.comons.enums.DisponibilidadMedico;
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
public class CitaServiceImpl implements CitaService {

    private static final List<EstadoCita> CITAS_ACTIVAS = List.of(
            EstadoCita.PENDIENTE,
            EstadoCita.CONFIRMADA,
            EstadoCita.EN_CURSO
    );

    private final CitaRepository citaRepository;
    private final CitaMapper citaMapper;
    private final MedicoClient medicoClient;
    private final PacienteClient pacienteClient;

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listar() {
        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO)
                .stream()
                .map(cita -> citaMapper.entidadAResponse(
                        cita,
                        pacienteClient.buscarPacienteSinEstado(cita.getIdPaciente()),
                        medicoClient.obtenerMedicoPorIdSinEstado(cita.getIdMedico())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorId(Long id) {
        Cita cita = obtenerCita(id);

        return citaMapper.entidadAResponse(
                cita,
                pacienteClient.buscarPacienteSinEstado(cita.getIdPaciente()),
                medicoClient.obtenerMedicoPorIdSinEstado(cita.getIdMedico()));
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {
        PacienteResponse paciente = pacienteClient.obtenerPorId(request.idPaciente());
        validarCitaPaciente(request.idPaciente());

        MedicoResponse medico = medicoClient.obtenerMedicoActivoPorId(request.idMedico());
        validarDisponibilidad(medico);
        validarCitaMedico(request.idMedico());

        Cita cita = citaMapper.requestAEntidad(request);
        citaRepository.save(cita);
        cambiarDisponibilidad(medico.id(), DisponibilidadMedico.NO_DISPONIBLE);

        return citaMapper.entidadAResponse(cita, paciente, medico);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean tieneCitaConfirmadaOEnCursoMedico(Long idMedico) {
        return citaRepository.existsByIdMedicoAndEstadoCitaIn(
                idMedico,
                List.of(EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO));
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {
        Cita cita = obtenerCita(id);
        PacienteResponse paciente = pacienteClient.obtenerPorId(request.idPaciente());

        validarOtraCitaPaciente(request.idPaciente(), id);

        MedicoResponse medico = medicoClient.obtenerMedicoActivoPorId(request.idMedico());

        if (!cita.getIdMedico().equals(request.idMedico())) {
            validarDisponibilidad(medico);
            validarOtraCitaMedico(request.idMedico(), id);

            cambiarDisponibilidad(
                    cita.getIdMedico(),
                    DisponibilidadMedico.DISPONIBLE);

            cambiarDisponibilidad(
                    request.idMedico(),
                    DisponibilidadMedico.NO_DISPONIBLE);
        }

        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas());

        return citaMapper.entidadAResponse(cita, paciente, medico);
    }

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {
        Cita cita = obtenerCita(idCita);
        EstadoCita estado = EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita);

        cita.actualizarEstadoCita(estado);

        switch (estado) {
            case EN_CURSO -> cambiarDisponibilidad(
                    cita.getIdMedico(),
                    DisponibilidadMedico.EN_CONSULTA);

            case FINALIZADA, CANCELADA -> cambiarDisponibilidad(
                    cita.getIdMedico(),
                    DisponibilidadMedico.DISPONIBLE);
        }

        log.info("Estado de cita {} actualizado a {}", idCita, estado);
    }

    @Override
    public void eliminar(Long id) {
        Cita cita = obtenerCita(id);

        if (cita.getEstadoCita() == EstadoCita.PENDIENTE) {
            cambiarDisponibilidad(
                    cita.getIdMedico(),
                    DisponibilidadMedico.DISPONIBLE);
        }

        cita.eliminar();
        log.info("Cita {} eliminada lógicamente", id);
    }

    private Cita obtenerCita(Long id) {
        return citaRepository.findByIdAndEstadoRegistro(
                id,
                EstadoRegistro.ACTIVO
        ).orElseThrow(() ->
                new RecursoNoEncontradoException(
                        "Cita no encontrada con ID: " + id));
    }

    private void validarCitaPaciente(Long idPaciente) {
        if (citaRepository.existsByIdPacienteAndEstadoCitaIn(
                idPaciente, CITAS_ACTIVAS)) {
            throw new IllegalStateException(
                    "El paciente ya tiene una cita activa");
        }
    }

    private void validarCitaMedico(Long idMedico) {
        if (citaRepository.existsByIdMedicoAndEstadoCitaIn(
                idMedico, CITAS_ACTIVAS)) {
            throw new IllegalStateException(
                    "El médico ya tiene una cita activa");
        }
    }

    private void validarOtraCitaPaciente(Long idPaciente, Long id) {
        if (citaRepository.existsByIdPacienteAndEstadoCitaInAndIdNot(
                idPaciente, CITAS_ACTIVAS, id)) {
            throw new IllegalStateException(
                    "El paciente ya tiene otra cita activa");
        }
    }

    private void validarOtraCitaMedico(Long idMedico, Long id) {
        if (citaRepository.existsByIdMedicoAndEstadoCitaInAndIdNot(
                idMedico, CITAS_ACTIVAS, id)) {
            throw new IllegalStateException(
                    "El nuevo médico ya tiene otra cita activa");
        }
    }

    private void validarDisponibilidad(MedicoResponse medico) {
        if (!medico.idDisponibilidad().equals(
                DisponibilidadMedico.DISPONIBLE.getCodigo())) {
            throw new IllegalStateException(
                    "El médico no está disponible");
        }
    }

    private void cambiarDisponibilidad(
            Long idMedico,
            DisponibilidadMedico disponibilidad) {

        medicoClient.actualizarDisponibilidadMedico(
                idMedico,
                disponibilidad.getCodigo());
    }
}