package com.victor.paciente.mapper;

import com.victor.comons.dto.medicos.MedicoRequest;
import com.victor.comons.dto.medicos.MedicoResponse;
import com.victor.comons.dto.pacientes.PacienteRequest;
import com.victor.comons.dto.pacientes.PacienteResponse;
import com.victor.comons.mapper.CommonMapper;
import com.victor.paciente.entity.Paciente;

public class PacienteMapper implements CommonMapper<PacienteRequest, PacienteResponse, Paciente> {
    @Override
    public Paciente requestAEntidad(PacienteRequest request) {
        if (request == null)return null;
        return Paciente.builder()
                .nombre(request.nombre().trim())
                .apellidoPaterno(request.apellidoPaterno().trim())
                .apellidoMaterno(request.apellidoMaterno().trim())
                .edad(request.edad())
                .peso(request.peso().doubleValue())
                .estatura(request.estatura().doubleValue())
                .email(request.email().trim())
                .telefono(request.telefono().trim())
                .direccion(request.direccion().trim())
                .build();

    }

    @Override
    public PacienteResponse entidadAResponse(Paciente entidad) {
        if (entidad == null)return null;
        return new PacienteResponse(
                entidad.getId(),
                String.join("",
                        entidad.getNombre(),
                        entidad.getApellidoPaterno(),
                        entidad.getApellidoMaterno()),
                entidad.getEdad(),
                entidad.getPeso(),
                entidad.getEstatura(),
                entidad.getEmail(),
                entidad.getTelefono(),
                entidad.getDireccion()

        );
    }
}
