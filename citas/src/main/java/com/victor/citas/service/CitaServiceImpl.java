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

    private final CitaRepository citaRepository;
    private final CitaMapper citaMapper;
    private final MedicoClient medicoClient;
    private final PacienteClient pacienteClient;


    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listar() {

        log.info("Listando todas las citas activas");

        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO)
                .stream()
                .map(cita -> citaMapper.entidadAResponse(
                        cita,
                        obtenerPacienteSinEstado(cita.getIdPaciente()),
                        obtenerMedicoSinEstado(cita.getIdMedico())
                ))
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorId(Long id) {

        Cita cita = citaRepository.findByIdAndEstadoRegistro(
                id,
                EstadoRegistro.ACTIVO
        ).orElseThrow(() ->
                new RecursoNoEncontradoException(
                        "Cita no encontrada con ID: " + id
                )
        );

        return citaMapper.entidadAResponse(
                cita,
                obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico())
        );
    }


    @Override
    public CitaResponse registrar(CitaRequest request) {

        log.info("Registrando nueva cita");

        // Paciente activo
        PacienteResponse paciente =
                pacienteClient.obtenerPorId(request.idPaciente());

        boolean pacienteTieneCita =
                citaRepository.existsByIdPacienteAndEstadoCitaIn(
                        request.idPaciente(),
                        List.of(
                                EstadoCita.PENDIENTE,
                                EstadoCita.CONFIRMADA,
                                EstadoCita.EN_CURSO
                        )
                );

        if (pacienteTieneCita) {
            throw new IllegalStateException(
                    "El paciente ya tiene una cita activa"
            );
        }

        // Médico activo
        MedicoResponse medico =
                obtenerMedicoActivo(request.idMedico());

        validarMedicoDisponible(medico);

        boolean medicoTieneCita =
                citaRepository.existsByIdMedicoAndEstadoCitaIn(
                        request.idMedico(),
                        List.of(
                                EstadoCita.PENDIENTE,
                                EstadoCita.CONFIRMADA,
                                EstadoCita.EN_CURSO
                        )
                );

        if (medicoTieneCita) {
            throw new IllegalStateException(
                    "El médico ya tiene una cita activa"
            );
        }

        Cita cita = citaMapper.requestAEntidad(request);

        citaRepository.save(cita);

        actualizarDisponibilidadMedico(
                medico.id(),
                DisponibilidadMedico.NO_DISPONIBLE.getCodigo()
        );

        log.info("Cita registrada exitosamente");

        return citaMapper.entidadAResponse(
                cita,
                paciente,
                medico
        );
    }


    @Override
    @Transactional(readOnly = true)
    public boolean tieneCitaConfirmadaOEnCursoMedico(Long idMedico) {

        return citaRepository.existsByIdMedicoAndEstadoCitaIn(
                idMedico,
                List.of(
                        EstadoCita.CONFIRMADA,
                        EstadoCita.EN_CURSO
                )
        );
    }


    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {

        log.info("Actualizando cita con ID: {}", id);

        Cita cita = citaRepository.findByIdAndEstadoRegistro(
                id,
                EstadoRegistro.ACTIVO
        ).orElseThrow(() ->
                new RecursoNoEncontradoException(
                        "Cita no encontrada con ID: " + id
                )
        );

        validarCitaActualizable(cita);

        PacienteResponse paciente =
                pacienteClient.obtenerPorId(request.idPaciente());

        boolean pacienteTieneOtraCita =
                citaRepository.existsByIdPacienteAndEstadoCitaInAndIdNot(
                        request.idPaciente(),
                        List.of(
                                EstadoCita.PENDIENTE,
                                EstadoCita.CONFIRMADA,
                                EstadoCita.EN_CURSO
                        ),
                        id
                );

        if (pacienteTieneOtraCita) {
            throw new IllegalStateException(
                    "El paciente ya tiene otra cita activa"
            );
        }

        if (!cita.getIdMedico().equals(request.idMedico())) {

            MedicoResponse medicoNuevo =
                    obtenerMedicoActivo(request.idMedico());

            validarMedicoDisponible(medicoNuevo);

            boolean medicoTieneOtraCita =
                    citaRepository.existsByIdMedicoAndEstadoCitaInAndIdNot(
                            request.idMedico(),
                            List.of(
                                    EstadoCita.PENDIENTE,
                                    EstadoCita.CONFIRMADA,
                                    EstadoCita.EN_CURSO
                            ),
                            id
                    );

            if (medicoTieneOtraCita) {
                throw new IllegalStateException(
                        "El nuevo médico ya tiene otra cita activa"
                );
            }

            actualizarDisponibilidadMedico(
                    cita.getIdMedico(),
                    DisponibilidadMedico.DISPONIBLE.getCodigo()
            );

            actualizarDisponibilidadMedico(
                    request.idMedico(),
                    DisponibilidadMedico.NO_DISPONIBLE.getCodigo()
            );
        }

        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas()
        );

        MedicoResponse medico =
                obtenerMedicoActivo(request.idMedico());

        log.info("Cita actualizada correctamente");

        return citaMapper.entidadAResponse(
                cita,
                paciente,
                medico
        );
    }


    @Override
    public void actualizarEstadoCita(
            Long idCita,
            Long idEstadoCita
    ) {

        Cita cita = citaRepository.findByIdAndEstadoRegistro(
                idCita,
                EstadoRegistro.ACTIVO
        ).orElseThrow(() ->
                new RecursoNoEncontradoException(
                        "Cita no encontrada: " + idCita
                )
        );

        EstadoCita nuevoEstado =
                EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita);

        cita.actualizarEstadoCita(nuevoEstado);

        if (nuevoEstado == EstadoCita.FINALIZADA ||
                nuevoEstado == EstadoCita.CANCELADA) {

            actualizarDisponibilidadMedico(
                    cita.getIdMedico(),
                    DisponibilidadMedico.DISPONIBLE.getCodigo()
            );
        }

        if (nuevoEstado == EstadoCita.EN_CURSO) {

            actualizarDisponibilidadMedico(
                    cita.getIdMedico(),
                    DisponibilidadMedico.EN_CONSULTA.getCodigo()
            );
        }

        log.info(
                "Estado de cita {} actualizado a {}",
                idCita,
                nuevoEstado
        );
    }


    @Override
    public void eliminar(Long id) {

        Cita cita = citaRepository.findByIdAndEstadoRegistro(
                id,
                EstadoRegistro.ACTIVO
        ).orElseThrow(() ->
                new RecursoNoEncontradoException(
                        "Cita no encontrada con ID: " + id
                )
        );

        validarCitaEliminable(cita);

        if (cita.getEstadoCita() == EstadoCita.PENDIENTE) {

            actualizarDisponibilidadMedico(
                    cita.getIdMedico(),
                    DisponibilidadMedico.DISPONIBLE.getCodigo()
            );
        }

        cita.eliminar();

        log.info(
                "Cita {} eliminada lógicamente",
                id
        );
    }


    private MedicoResponse obtenerMedicoActivo(Long id) {

        return medicoClient.obtenerMedicoActivoPorId(id);
    }


    private MedicoResponse obtenerMedicoSinEstado(Long id) {

        return medicoClient.obtenerMedicoPorIdSinEstado(id);
    }


    private void actualizarDisponibilidadMedico(
            Long id,
            Long disponibilidad
    ) {
        medicoClient.actualizarDisponibilidadMedico(
                id,
                disponibilidad
        );
    }


    private void validarMedicoDisponible(
            MedicoResponse medico
    ) {

        if (!medico.idDisponibilidad().equals(
                DisponibilidadMedico.DISPONIBLE.getCodigo()
        )) {

            throw new IllegalStateException(
                    "El médico no está disponible"
            );
        }
    }


    private void validarCitaActualizable(Cita cita) {

        if (cita.getEstadoCita() != EstadoCita.PENDIENTE &&
                cita.getEstadoCita() != EstadoCita.CONFIRMADA) {

            throw new IllegalStateException(
                    "La cita solo puede actualizarse si está " +
                            "PENDIENTE o CONFIRMADA"
            );
        }
    }


    private void validarCitaEliminable(Cita cita) {

        if (cita.getEstadoCita() != EstadoCita.PENDIENTE
                && cita.getEstadoCita() != EstadoCita.CANCELADA
                && cita.getEstadoCita() != EstadoCita.FINALIZADA) {

            throw new IllegalStateException(
                    "La cita solo puede eliminarse si está " +
                            "PENDIENTE, CANCELADA o FINALIZADA"
            );
        }
    }


    private PacienteResponse obtenerPacienteSinEstado(Long id) {

        return pacienteClient.buscarPacienteSinEstado(id);
    }
}