package com.victor.citas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.victor.comons.dto.medicos.DatosMedico;
import com.victor.comons.dto.pacientes.DatosPaciente;

import java.time.LocalDateTime;

public record CitaResponse(
        Long id,
        DatosPaciente paciente,
        DatosMedico medico,
        @JsonFormat(shape = JsonFormat.Shape.ANY, pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime fechaCita,
        String sintomas,
        String estadoCita
) {
}
