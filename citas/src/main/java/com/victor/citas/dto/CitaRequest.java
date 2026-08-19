package com.victor.citas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record CitaRequest(
        @NotNull(message = "El id del paciente es requerido")
        @Positive(message = "El id del paciente debe de ser positivo")
        long idPaciente,
        @NotNull(message = "El id del medico es requerido")
        @Positive(message = "El id del medico debe de ser positivo")
        long idMedico,
        @NotNull(message = "La fecha de la cita es requerida")
        @FutureOrPresent(message = "La fecha de la cita debe ser futura")
        @JsonFormat(shape = JsonFormat.Shape.ANY, pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime fechaCita,
        @NotBlank(message = "Los sintomas son requeridos")
        @Size(min = 20, max = 500, message = "La descripcion de los sintomas debe tener entre 20 y 500 caracteres")
        String sintomas

) {
}
