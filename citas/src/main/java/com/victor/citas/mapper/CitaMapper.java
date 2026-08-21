package com.victor.citas.mapper;

import com.victor.citas.dto.CitaRequest;
import com.victor.citas.dto.CitaResponse;
import com.victor.citas.entity.Cita;
import com.victor.comons.dto.medicos.DatosMedico;
import com.victor.comons.dto.medicos.MedicoResponse;
import com.victor.comons.dto.pacientes.DatosPaciente;
import com.victor.comons.dto.pacientes.PacienteResponse;
import com.victor.comons.mapper.CommonMapper;
import org.springframework.stereotype.Component;

@Component
public class CitaMapper implements CommonMapper<CitaRequest, CitaResponse, Cita> {

    @Override
    public Cita requestAEntidad(CitaRequest request) {

        if (request== null ) return null;

        return Cita.crear(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas()
        );
    }


    @Override
    public CitaResponse  entidadAResponse (Cita entidad) {
        if (entidad == null)return null;

        return new CitaResponse(
                entidad.getId(),
                null,
                null,
                entidad.getFechaCita(),
                entidad.getSintomas(),
                entidad.getEstadoCita().getDescripcion()
        );
    }

    public CitaResponse  entidadAResponse (Cita entidad, PacienteResponse paciente, MedicoResponse medico) {

        if (entidad == null)return null;

        return new CitaResponse(
                entidad.getId(),
                pacienteResponseADatosPaciente(paciente),
                medicoResponseAResponseADatosMedico(medico),
                entidad.getFechaCita(),
                entidad.getSintomas(),
                entidad.getEstadoCita().getDescripcion()
        );
    }
    private DatosPaciente pacienteResponseADatosPaciente(PacienteResponse paciente) {
        if (paciente == null) return null;

        return new DatosPaciente(
                paciente.nombre(),
                paciente.numExpediente(),
                paciente.edad() + " años",
                paciente.peso() + " kg.",
                paciente.estatura() + " m.",
                paciente.email(),
                Math.round(paciente.imc() * 100.0) / 100.0 + " " +
                        clasificacionIMC(paciente.imc()),
                paciente.telefono()
        );
    }

    private String clasificacionIMC(double imc){
        if (imc< 18.5) return "bajo peso";
        if (imc< 25) return " peso normsl";
        if (imc< 30) return "sobrepeso";
        if (imc< 35) return "obesidad grado 1";
        if (imc< 40) return "obesidad grado 2";

        return  "obesidad grado 3";
    }

    private DatosMedico medicoResponseAResponseADatosMedico(MedicoResponse medico){
        if (medico== null) return null;
        return new DatosMedico(
                medico.nombre(),
                medico.cedulaProfesional(),
                medico.especialidad()

        );
    }
}
