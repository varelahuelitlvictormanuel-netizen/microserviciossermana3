package com.victor.comons.dto.pacientes;

public record DatosPaciente(
        String nombre,
        String numExpediente,
        String edad,
        String peso,
        String estatura,
        String imc,
        String telefono
) {
}
