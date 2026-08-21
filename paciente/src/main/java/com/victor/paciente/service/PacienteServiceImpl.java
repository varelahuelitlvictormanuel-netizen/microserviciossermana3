package com.victor.paciente.service;

import com.victor.comons.dto.pacientes.PacienteRequest;
import com.victor.comons.dto.pacientes.PacienteResponse;
import com.victor.comons.dto.pacientes.PacienteRequest;
import com.victor.comons.dto.pacientes.PacienteResponse;
import com.victor.comons.enums.EstadoRegistro;
import com.victor.comons.exceptions.RecursoNoEncontradoException;
import com.victor.paciente.entity.Paciente;
import com.victor.comons.enums.EstadoRegistro;
import com.victor.comons.exceptions.RecursoNoEncontradoException;
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
        return pacienteRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO)
                .stream().map(pacienteMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPorId(Long id) {
        log.info("Buscando paciente con id {}...", id);
        return pacienteRepository.findById(id)
                .filter(paciente -> paciente.getEstadoRegistro() ==EstadoRegistro.ACTIVO)
                .map(pacienteMapper::entidadAResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro registro con el id" + id));
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPacientePorIdSinEstado(Long id) {
        log.warn("Buscando paciente con el id {}", id);
        return pacienteMapper.entidadAResponse(obtenerPacienteOException(id));
    }

    @Override
    public PacienteResponse registrar(PacienteRequest request) {
        log.info("Registrando paciente...");
        validarDatosUnicos(request.telefono(), request.email());

        Paciente paciente = pacienteMapper.requestAEntidad(request);
        pacienteRepository.save(paciente);
        log.info("Paciente registrado...");
        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public PacienteResponse actualizar(PacienteRequest request, Long id) {
        log.info("Buscando paciente con id {}", id);
        Paciente paciente = obtenerPacienteOException(id);

        validarCambiosUnicos(request.telefono(), request.email(), id);

        log.info("Actualizando informacion...");
        paciente.actualizar(request.nombre(), request.apellidoPaterno(), request.apellidoMaterno(),
                request.edad(), request.peso(), request.estatura(), request.email(), request.telefono(),
                request.direccion());
        log.info("Paciente actualizado...");
        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public void eliminar(Long id) {
        Paciente paciente = obtenerPacienteOException(id);
        //TODO: Validar citas asociadas
        paciente.borradoLogico();
    }

    public Paciente obtenerPacienteOException(Long id){

        log.info("Buscando paciente con id: {}", id);

        return pacienteRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Paciente no encontrado con id: " + id)
        );
    }
    public void validarDatosUnicos(String telefono, String email){
        if (pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistro(email, EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("El email registrado ya lo tiene registrado un paciente");

        if (pacienteRepository.existsByTelefonoAndEstadoRegistro(telefono, EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("El telefono registrado ya lo tiene registrado un paciente");
    }
    public void validarCambiosUnicos(String telefono, String email, Long id){
        if (pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(email, EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("El email registrado ya lo tiene registrado un paciente");

        if (pacienteRepository.existsByTelefonoAndEstadoRegistroAndIdNot(telefono, EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("El telefono registrado ya lo tiene registrado un paciente");
    }


}