package com.victor.medicos.service;

import com.victor.comons.client.CitaCliente;
import com.victor.comons.dto.medicos.MedicoRequest;
import com.victor.comons.dto.medicos.MedicoResponse;
import com.victor.comons.enums.DisponibilidadMedico;
import com.victor.comons.enums.EspecialidadMedico;
import com.victor.comons.enums.EstadoRegistro;
import com.victor.comons.exceptions.RecursoNoEncontradoException;
import com.victor.medicos.entity.Medico;
import com.victor.medicos.mapper.MedicoMapper;
import com.victor.medicos.repository.MedicoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;
    private final MedicoMapper medicoMapper;
    private final CitaCliente citaCliente;

    @Override
    public MedicoResponse obtenerMedicoPorIdSinEstado(Long id) {
        return medicoMapper.entidadAResponse(
                medicoRepository.findById(id)
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "Médico no encontrado con id: " + id))
        );
    }

    @Override
    public void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
        Medico medico = obtenerMedicoActivoOrExcepcion(idMedico);

        DisponibilidadMedico disponibilidad =
                DisponibilidadMedico.obtenerDisponibilidadPorCodigo(idDisponibilidad);

        if (disponibilidad == DisponibilidadMedico.DISPONIBLE) {
            validarCitaActiva(idMedico);
        }

        medico.atualizarDisponibilidad(disponibilidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicoResponse> listar() {
        return medicoRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO)
                .stream()
                .map(medicoMapper::entidadAResponse)
                .toList();
    }

    @Override
    public MedicoResponse obtenerPorId(Long id) {
        return medicoMapper.entidadAResponse(
                obtenerMedicoActivoOrExcepcion(id)
        );
    }

    @Override
    public MedicoResponse registrar(MedicoRequest request) {
        validarDatosUnicos(request);

        Medico medico = medicoMapper.requestAEntidad(request);

        medico.actualizarEspecilidad(
                EspecialidadMedico.obtenerEspecialidadPorCodigo(
                        request.idEspecialidad())
        );

        medico.atualizarDisponibilidad(DisponibilidadMedico.DISPONIBLE);

        medicoRepository.save(medico);

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public MedicoResponse actualizar(MedicoRequest request, Long id) {
        Medico medico = obtenerMedicoActivoOrExcepcion(id);

        validarCitaActiva(id);
        validarCambioUnicos(request, id);

        medico.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.email(),
                request.telefono(),
                request.cedulaProfesional(),
                EspecialidadMedico.obtenerEspecialidadPorCodigo(
                        request.idEspecialidad())
        );

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public void eliminar(Long id) {
        Medico medico = obtenerMedicoActivoOrExcepcion(id);

        validarCitaActiva(id);

        medico.eliminar();
    }

    private Medico obtenerMedicoActivoOrExcepcion(Long id) {
        return medicoRepository.findByIdAndEstadoRegistro(
                id,
                EstadoRegistro.ACTIVO
        ).orElseThrow(() -> new RecursoNoEncontradoException(
                "No se encontró el médico activo con id: " + id));
    }

    private void validarCitaActiva(Long idMedico) {
        ResponseEntity<Void> respuesta =
                citaCliente.validarAgendaMedico(idMedico);

        if (respuesta.getStatusCode().is4xxClientError()) {
            throw new IllegalStateException(
                    "El médico tiene una cita CONFIRMADA o EN_CURSO"
            );
        }
    }

    private void validarDatosUnicos(MedicoRequest request) {

        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email().trim(),
                EstadoRegistro.ACTIVO)) {
            throw new IllegalArgumentException(
                    "Ya existe un médico activo con el email ingresado");
        }

        if (medicoRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono().trim(),
                EstadoRegistro.ACTIVO)) {
            throw new IllegalArgumentException(
                    "Ya existe un médico activo con el teléfono ingresado");
        }

        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(
                request.cedulaProfesional().trim(),
                EstadoRegistro.ACTIVO)) {
            throw new IllegalArgumentException(
                    "Ya existe un médico activo con la cédula ingresada");
        }
    }

    private void validarCambioUnicos(MedicoRequest request, Long id) {

        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
                request.email().trim(),
                EstadoRegistro.ACTIVO,
                id)) {
            throw new IllegalArgumentException(
                    "Ya existe un médico activo con el email ingresado");
        }

        if (medicoRepository.existsByTelefonoAndEstadoRegistroAndIdNot(
                request.telefono().trim(),
                EstadoRegistro.ACTIVO,
                id)) {
            throw new IllegalArgumentException(
                    "Ya existe un médico activo con el teléfono ingresado");
        }

        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(
                request.cedulaProfesional().trim(),
                EstadoRegistro.ACTIVO,
                id)) {
            throw new IllegalArgumentException(
                    "Ya existe un médico activo con la cédula ingresada");
        }
    }
}