package com.victor.paciente.mapper;

import com.victor.comons.dto.pacientes.PacienteRequest;
import com.victor.comons.dto.pacientes.PacienteResponse;
import com.victor.comons.enums.EstadoRegistro;
import com.victor.comons.mapper.CommonMapper;
import com.victor.paciente.entity.Paciente;
import org.springframework.stereotype.Component;

@Component
public class PacienteMapper implements CommonMapper<PacienteRequest, PacienteResponse, Paciente> {

    @Override
    public Paciente requestAEntidad(PacienteRequest request) {
        if (request == null) return null;

        Paciente paciente = Paciente.builder()
                .nombre(request.nombre().trim())
                .apellidoPaterno(request.apellidoPaterno().trim())
                .apellidoMaterno(request.apellidoMaterno().trim())
                .edad(request.edad())
                .peso(request.peso().doubleValue())
                .estatura(request.estatura().doubleValue())
                .email(request.email().trim())
                .telefono(request.telefono().trim())
                .direccion(request.direccion().trim())
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();

        paciente.generarExpendiente(request.telefono());
        paciente.generarimc(request.estatura(), request.peso());

        return paciente;
    }

    @Override
    public PacienteResponse entidadAResponse(Paciente entidad) {
        if (entidad == null) return null;

        String nombre = entidad.getEstadoRegistro() == EstadoRegistro.ELIMINADO
                ? "Usuario eliminado prueba test"
                : String.join("",
                entidad.getNombre(),
                entidad.getApellidoPaterno(),
                entidad.getApellidoMaterno());

        return new PacienteResponse(
                entidad.getId(),
                nombre,
                entidad.getEdad(),
                entidad.getPeso(),
                entidad.getEstatura(),
                entidad.getImc(),
                entidad.getEmail(),
                entidad.getTelefono(),
                entidad.getDireccion(),
                entidad.getNumExpediente()
        );
    }
}
