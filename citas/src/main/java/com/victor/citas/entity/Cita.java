package com.victor.citas.entity;


import com.victor.citas.enums.EstadoCita;
import com.victor.comons.enums.EstadoRegistro;
import com.victor.comons.utils.StringCustomUtils;
import com.victor.comons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Setter
@Entity
@Table(name = "CITAS")
@Getter
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

    public static  void validarId(Long id , String campo){
        ValoresNumericosUtils.validarLongPositivo(id ,
                "el id es requerido y drbr der positivo ");
    }
    private static  void validarFecha(LocalDateTime fechaCita){
        if (fechaCita== null || !fechaCita.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("la fecha cita es requerida");
    }

    public static void validarDatos(Long idPaciente, Long idMedico, LocalDateTime fechaCita, String sintomas){


        validarId(idPaciente, "paciente");
        validarId(idMedico, "medico");
        validarFecha(fechaCita);

        StringCustomUtils.validarTamanio(sintomas,20 , 300,
                " los sintomas son requeridos ");
    }

    private void validarNoEliminada(){
        if (this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw  new IllegalStateException("la cita ya esta eliminada");
    }

    private void  validarEliminacionPermitida(){

        validarNoEliminada();
        if (!estadoCita.isEliminable())
            throw  new IllegalStateException("la cita con estado"+ estadoCita
                    + " no puede eliminarse");

    }

    private void  validarActualizacionPermitida(){

        validarNoEliminada();
        if (!estadoCita.isActualizable())
            throw  new IllegalStateException("la cita con estado"+ estadoCita
                    + " no puede eliminarse");

    }

    public void eliminar(){

        validarEliminacionPermitida();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    public void actualizar (Long idPaciente, Long idMedico,
                            LocalDateTime fechaCita, String sintomas
    ){

        validarActualizacionPermitida();
        validarDatos(idPaciente, idMedico, fechaCita, sintomas);


        this.idPaciente = idPaciente;
        this.idMedico= idMedico;
        this.fechaCita = fechaCita;
        this.sintomas = sintomas.trim();
    }

    public void actualizarEstadoCita(EstadoCita nuevoEstado) {

        if (nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "el nuevo estado de la cita es requerido"
            );
        }

        if (!this.estadoCita.puedeCambiarA(nuevoEstado)) {
            throw new IllegalStateException(
                    "la cita con estado "
                            + this.estadoCita
                            + " solo puede cambiar a "
                            + this.estadoCita.puedeCambiar()
            );
        }

        this.estadoCita = nuevoEstado;
    }


    public  static  Cita crear( Long idPaciente,
                                Long idMedico, LocalDateTime fechaCita, String sintomas){

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
}