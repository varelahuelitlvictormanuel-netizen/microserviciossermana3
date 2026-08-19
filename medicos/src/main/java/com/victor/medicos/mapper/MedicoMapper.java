package com.victor.medicos.mapper;

import com.victor.comons.dto.medicos.MedicoRequest;
import com.victor.comons.dto.medicos.MedicoResponse;
import com.victor.comons.enums.DisponibilidadMedico;
import com.victor.comons.enums.EstadoRegistro;
import com.victor.comons.mapper.CommonMapper;
import com.victor.medicos.entity.Medico;
import org.springframework.stereotype.Component;

@Component
public class MedicoMapper implements CommonMapper<MedicoRequest, MedicoResponse, Medico> {
    @Override
    public Medico requestAEntidad(MedicoRequest request) {
        if (request == null)return null;
        return Medico.builder()
                .nombre(request.nombre().trim())
                .apellidoPaterno (request.apellidoPaterno().trim())
                .apellidoMaterno (request.apellidoMaterno().trim())
                .email(request.email().toLowerCase().trim())
                .telefono(request.telefono().trim())
                .cedulaProfesional(request.cedulaProfesional().trim())
                .disponibilidad(DisponibilidadMedico.DISPONIBLE)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }

    @Override
    public MedicoResponse entidadAResponse(Medico entidad) {
       if (entidad == null)return null;

       return new MedicoResponse(
               entidad.getId(),
               String.join("",
                       entidad.getNombre(),
                       entidad.getApellidoPaterno(),
                       entidad.getApellidoMaterno()),
               entidad.getEdad(),
               entidad.getEmail(),
               entidad.getTelefono(),
               entidad.getCedulaProfesional(),
               entidad.getEspecialidad().getDescripcion(),
               entidad.getDisponibilidad().getDescripcion(),
               entidad.getDisponibilidad().getCodigo()
       );
    }
}
