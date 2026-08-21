package com.victor.medicos.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class MedicoServiceImpl implements MedicoService{
    private final MedicoRepository medicoRepository;
    private final MedicoMapper medicoMapper;
    @Override
    public MedicoResponse obtenerMedicoPorIdSinEstado(Long id) {
        log.info("Buscando medico sin estado con id: {}", id);
        return medicoMapper.entidadAResponse(medicoRepository.findById(id).orElseThrow(()-> new RecursoNoEncontradoException(
                "Medico sin estado no encontrado con id:" + id
        )));
    }

    @Override
    public void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
        Medico medico = obtenerMedicoActivoOrExepcion(idMedico);
        log.info("Actualizando disponibilidad del medico con id: {}", idMedico);
        DisponibilidadMedico nuevaDisponibilidad = DisponibilidadMedico
                .obtenerDisponibilidadPorCodigo(idDisponibilidad);
        DisponibilidadMedico disponibilidadAnterior = medico.getDisponibilidad();
        medico.atualizarDisponibilidad(nuevaDisponibilidad);
        log.info("Disponibilidad del médico con id{}cambio de {} a {}",
                idMedico, disponibilidadAnterior, nuevaDisponibilidad);

    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicoResponse> listar() {
        log.info("Buscando medicos activos...");
        return medicoRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(medicoMapper::entidadAResponse).toList();
    }

    @Override
    public MedicoResponse obtenerPorId(Long id) {
        log.info("Buscando al medico {} activo..", id);
        return medicoMapper.entidadAResponse(obtenerMedicoActivoOrExepcion(id));
    }

    @Override
    public MedicoResponse registrar(MedicoRequest request) {
        log.info("Registrando medico...");
        validarDatosUnicos(request);
        Medico medico = medicoMapper.requestAEntidad(request);
        medico.actualizarEspecilidad(
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad())
        );
        medicoRepository.save(medico);
        log.info("Medico {}{}{} registrado", medico.getNombre(),medico.getApellidoPaterno(),
                medico.getApellidoMaterno());
        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public MedicoResponse actualizar(MedicoRequest request, Long id) {
        Medico medico = obtenerMedicoActivoOrExepcion(id);
        validarCambioUnicos(request, id);
        medico.actualizar(request.nombre(), request.apellidoPaterno(), request.apellidoMaterno(), request.edad(),
                request.email(),request.telefono(),request.cedulaProfesional(),
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad()));
        log.info("Registro actualizado del Medico {}{}{}",request.nombre(), request.apellidoPaterno(),
                request.apellidoMaterno());
        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public void eliminar(Long id) {
        Medico medico = obtenerMedicoActivoOrExepcion(id);
        log.info("Ekiminar medico {}", id);
        medico.eliminar();
        log.info("Medico ha sido ELIMINADO");
    }
    private Medico obtenerMedicoActivoOrExepcion(Long id){
        log.info("Buscando al id {}", id);
        return medicoRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(()-> new RecursoNoEncontradoException("No se encontro el medico activo con id"+ id));
    }
    private void validarDatosUnicos(MedicoRequest request){
        log.info("Validar info unica...");
        log.info("email...");
        if(medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(request.email().trim(), EstadoRegistro.ACTIVO))
            throw  new IllegalArgumentException("Ya existe un medico activo con el email ingresado");
        log.info("telefono...");
        if(medicoRepository.existsByTelefonoAndEstadoRegistro(request.telefono().trim(), EstadoRegistro.ACTIVO))
            throw  new IllegalArgumentException("Ya existe un medico activo con el telefono ingresado");
        log.info("cedula...");
        if(medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(request.cedulaProfesional().trim(), EstadoRegistro.ACTIVO))
            throw  new IllegalArgumentException("Ya existe un medico activo con la cedula ingresada");

    }
    private void validarCambioUnicos(MedicoRequest request, Long id){
        log.info("validar info unica entre doctores ya existentes...");
        log.info("email a actulizar...");
        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(request.email().trim(),
                EstadoRegistro.ACTIVO,id))
            throw new IllegalArgumentException("Ya existe un medico activo con el email ingresado");
        log.info("telefono o actualizar...");
        if (medicoRepository.existsByTelefonoAndEstadoRegistroAndIdNot(request.telefono().trim(),
                EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe un medico activo con el telefono ingresado");
        log.info("cedulaa actualizar...");
        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(
                request.cedulaProfesional().trim(), EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe un medico activo con la cedula ingresada");

    }
}