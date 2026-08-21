package com.victor.paciente.service;

import com.victor.comons.dto.pacientes.PacienteRequest;
import com.victor.comons.dto.pacientes.PacienteResponse;
import com.victor.comons.enums.EstadoRegistro;
import com.victor.comons.exceptions.RecursoNoEncontradoException;
import com.victor.paciente.entity.Paciente;
import com.victor.paciente.mapper.PacienteMapper;
import com.victor.paciente.repository.PacienteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponse> listar() {

        log.info("Listando pacientes activos...");

        return pacienteRepository
                .findByEstadoRegistro(EstadoRegistro.ACTIVO)
                .stream()
                .map(pacienteMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPorId(Long id) {

        log.info("Buscando paciente activo con id {}", id);

        return pacienteMapper.entidadAResponse(
                obtenerPacienteOException(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPacientePorIdSinEstado(Long id) {

        log.info("Buscando paciente sin validar estado con id {}", id);

        return pacienteMapper.entidadAResponse(
                pacienteRepository.findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Paciente no encontrado con id: " + id
                                ))
        );
    }

    @Override
    public PacienteResponse registrar(PacienteRequest request) {
        log.info("Registrando paciente...");
        validarDatosUnicos(
                request.telefono(),
                request.email()
        );
        Paciente paciente = pacienteMapper.requestAEntidad(request);
        pacienteRepository.save(paciente);
        log.info("Paciente registrado correctamente");
        return pacienteMapper.entidadAResponse(paciente);
    }
    @Override
    public PacienteResponse actualizar(
            PacienteRequest request,
            Long id) {
        log.info("Actualizando paciente con id {}", id);
        Paciente paciente = obtenerPacienteOException(id);
        validarCambiosUnicos(
                request.telefono(),
                request.email(),
                id
        );
        paciente.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.peso(),
                request.estatura(),
                request.email(),
                request.telefono(),
                request.direccion()
        );
        log.info("Paciente actualizado correctamente");
        return pacienteMapper.entidadAResponse(paciente);
    }
    @Override
    public void eliminar(Long id) {
        log.info("Eliminando lógicamente paciente con id {}", id);
        Paciente paciente = obtenerPacienteOException(id);
        paciente.borradoLogico();
        log.info("Paciente eliminado lógicamente");
    }
    private Paciente obtenerPacienteOException(Long id) {
        return pacienteRepository
                .findById(id)
                .filter(paciente ->
                        paciente.getEstadoRegistro() == EstadoRegistro.ACTIVO
                )
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Paciente no encontrado o eliminado con id: " + id
                        )
                );
    }
    private void validarDatosUnicos(
            String telefono,
            String email) {
        if (pacienteRepository
                .existsByEmailIgnoreCaseAndEstadoRegistro(
                        email,
                        EstadoRegistro.ACTIVO)) {

            throw new IllegalArgumentException(
                    "El email ya lo tiene registrado un paciente"
            );
        }
        if (pacienteRepository
                .existsByTelefonoAndEstadoRegistro(
                        telefono,
                        EstadoRegistro.ACTIVO)) {
            throw new IllegalArgumentException(
                    "El teléfono ya lo tiene registrado un paciente"
            );
        }
    }

    private void validarCambiosUnicos(
            String telefono,
            String email,
            Long id) {
        if (pacienteRepository
                .existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
                        email,
                        EstadoRegistro.ACTIVO,
                        id)) {
            throw new IllegalArgumentException(
                    "El email ya lo tiene registrado otro paciente"
            );
        }

        if (pacienteRepository
                .existsByTelefonoAndEstadoRegistroAndIdNot(
                        telefono,
                        EstadoRegistro.ACTIVO,
                        id)) {

            throw new IllegalArgumentException(
                    "El teléfono ya lo tiene registrado otro paciente"
            );
        }
    }
}