package com.victor.citas.entity;

import com.victor.citas.enums.EstadoCita;
import com.victor.comons.enums.EstadoRegistro;
import com.victor.comons.utils.StringCustomUtils;
import com.victor.comons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "CITAS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CITA")
    private Long id;

    @Column(name = "ID_PACIENTE", nullable = false)
    private Long idPaciente;

    @Column(name = "ID_MEDICO", nullable = false)
    private Long idMedico;

    @Column(name = "FECHA_CITA", nullable = false)
    private LocalDateTime fechaCita;

    @Column(name = "SINTOMAS", nullable = false)
    private String sintomas;

    @Column(name = "ESTADO_CITA", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoCita estadoCita;

    @Column(name = "ESTADO_REGISTRO", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoRegistro estadoRegistro;


    // ==================== VALIDACIONES ====================

    public static void validarId(Long id) {
        ValoresNumericosUtils.validarLongPositivo(
                id,
                "El id es requerido y debe ser positivo"
        );
    }

    private static void validarFecha(LocalDateTime fechaCita) {
        if (fechaCita == null || !fechaCita.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "La fecha de la cita es requerida"
            );
        }
    }

    public static void validarDatos(
            Long idPaciente,
            Long idMedico,
            LocalDateTime fechaCita,
            String sintomas
    ) {
        validarId(idPaciente);
        validarId(idMedico);
        validarFecha(fechaCita);

        StringCustomUtils.validarTamanio(
                sintomas,
                20,
                300,
                "Los síntomas son requeridos"
        );
    }

    private void validarOperacionPermitida(boolean actualizacion) {

        if (estadoRegistro == EstadoRegistro.ELIMINADO) {
            throw new IllegalStateException(
                    "La cita ya está eliminada"
            );
        }

        if (actualizacion && !estadoCita.isActualizable()) {
            throw new IllegalStateException(
                    "La cita con estado " + estadoCita +
                            " no puede actualizarse"
            );
        }

        if (!actualizacion && !estadoCita.isEliminable()) {
            throw new IllegalStateException(
                    "La cita con estado " + estadoCita +
                            " no puede eliminarse"
            );
        }
    }


    // ==================== MÉTODOS DE NEGOCIO ====================

    public static Cita crear(
            Long idPaciente,
            Long idMedico,
            LocalDateTime fechaCita,
            String sintomas
    ) {
        validarDatos(idPaciente, idMedico, fechaCita, sintomas);

        return Cita.builder()
                .idPaciente(idPaciente)
                .idMedico(idMedico)
                .fechaCita(fechaCita)
                .sintomas(sintomas)
                .estadoCita(EstadoCita.PENDIENTE)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }

    public void actualizar(
            Long idPaciente,
            Long idMedico,
            LocalDateTime fechaCita,
            String sintomas
    ) {
        validarOperacionPermitida(true);
        validarDatos(idPaciente, idMedico, fechaCita, sintomas);

        this.idPaciente = idPaciente;
        this.idMedico = idMedico;
        this.fechaCita = fechaCita;
        this.sintomas = sintomas.trim();
    }

    public void eliminar() {
        validarOperacionPermitida(false);
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    public void actualizarEstadoCita(EstadoCita nuevoEstado) {

        if (nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "El nuevo estado de la cita es requerido"
            );
        }

        if (!estadoCita.puedeCambiarA(nuevoEstado)) {
            throw new IllegalStateException(
                    "La cita con estado " + estadoCita +
                            " solo puede cambiar a " + estadoCita.puedeCambiar()
            );
        }

        this.estadoCita = nuevoEstado;
    }
}