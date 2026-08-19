package com.victor.medicos.entity;

import com.victor.comons.enums.DisponibilidadMedico;
import com.victor.comons.enums.EspecialidadMedico;
import com.victor.comons.enums.EstadoRegistro;
import com.victor.comons.utils.StringCustomUtils;
import com.victor.comons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MEDICOS")
@AllArgsConstructor
@NoArgsConstructor
@Builder@Getter
public class Medico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MEDICO")
    private Long id;

    @Column(name = "Nombre", length = 50, nullable = false)
    private String nombre;

    @Column(name = "Apellido_Paterno", length = 50, nullable = false)
    private String apellidoPaterno;

    @Column(name = "Apellido_Materno", length = 50, nullable = false)
    private String apellidoMaterno;

    @Column(name = "EDAD", nullable = false)
    private Short edad;

    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;

    @Column(name = "TELEFONO", length = 10, nullable = false)
    private String telefono;

    @Column(name = "CEDULA_PROFESIONAL", length = 12, nullable = false)
    private String cedulaProfesional;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESPECIALIDAD", nullable = false)
    private EspecialidadMedico especialidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "DISPONIBILIDAD", nullable = false)
    private DisponibilidadMedico disponibilidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estadoRegistro;

    public void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad, String email, String telefono, String cedulaProfesional, EspecialidadMedico especialidad) {
        StringCustomUtils.validarTamanio(nombre, 1, 50,
                "El nombre es requerido y debe contener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoPaterno, 1, 50,
                "El apellidoPaterno es requerido y debe contener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoMaterno, 1, 50,
                "El apellidoMaterno es requerido y debe contener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(email, 1, 100,
                "El email es requerido y debe contener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(telefono, 10, 10,
                "El teléfono es requerido y debe contener exactamente 10 dígitos(0-9)");

        StringCustomUtils.validarTamanio(cedulaProfesional, 12, 12,
                "La cédula profesional es requerida y debe contener exactamente 12 caracteres");

        ValoresNumericosUtils.validarRangoShort(edad, (short) 18, (short) 100,
                "La edad es requerida y debe tener ser entre 18 y 100 años");

        if (especialidad == null)
            throw new IllegalArgumentException("La especialidad es requerida");
    }

    private void validarNoeliminado() {
        if (this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new IllegalStateException("El medico ya esta eliminado");
    }

    public void actualizarEspecilidad(EspecialidadMedico especialidad) {
        validarNoeliminado();
        if (especialidad == null)
            throw new IllegalArgumentException("La especialidad es requerida");
        this.especialidad = especialidad;
    }

    public void atualizarDisponibilidad(DisponibilidadMedico disponibilidad) {
        validarNoeliminado();
        if (disponibilidad == null)
            throw new IllegalArgumentException("La disponibilidad es requerida");
        this.disponibilidad = disponibilidad;
    }

    public void eliminar() {
        validarNoeliminado();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }
    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad, String email, String telefono, String cedulaProfesional, EspecialidadMedico especialidad) {
        validarNoeliminado();
        validarDatos(nombre, apellidoPaterno, apellidoMaterno, edad, email, telefono, cedulaProfesional, especialidad);
        actualizarEspecilidad(especialidad);

        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.edad = edad;
        this.email = email;
        this.telefono = telefono;
        this.cedulaProfesional = cedulaProfesional;
        this.especialidad = especialidad;
    }
}

