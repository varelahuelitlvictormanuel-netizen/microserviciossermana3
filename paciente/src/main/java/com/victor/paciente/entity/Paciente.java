package com.victor.paciente.entity;

import com.victor.comons.enums.EspecialidadMedico;
import com.victor.comons.enums.EstadoRegistro;
import com.victor.comons.utils.StringCustomUtils;
import com.victor.comons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "PACIENTES")
@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PACIENTE")
    private Long id;

    @Column(name = "Nombre", length = 50, nullable = false)
    private String nombre;

    @Column(name = "Apellido_Paterno", length = 50, nullable = false)
    private String apellidoPaterno;

    @Column(name = "Apellido_Materno", length = 50, nullable = false)
    private String apellidoMaterno;

    @Column(name = "EDAD", nullable = false)
    @Min(value = 1, message = "Debe tener la edad de 1 año")
    @Max(value = 100, message = "Debe tener la edad maxima de 100 años")
    private Short edad;

    @Column(name = "PESO", nullable = false)
    private Double peso;

    @Column(name = "ESTATURA", nullable = false)
    private Double estatura;

    @Column(name = "IMC", nullable = false)
    private Double imc;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "NUM_EXPEDIENTE", nullable = false, length = 20)
    private String numExpediente;

    @Column(name = "TELEFONO", nullable = false, length = 10)
    private String telefono;

    @Column(name = "DIRECCION", nullable = false, length = 150)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estadoRegistro;

    public void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad, Double peso, Double estatura, String email, String telefono, String direccion) {
        StringCustomUtils.validarTamanio(nombre, 1, 50,
                "El nombre es requerido y debe contener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoPaterno, 1, 50,
                "El apellidoPaterno es requerido y debe contener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoMaterno, 1, 50,
                "El apellidoMaterno es requerido y debe contener entre 1 y 50 caracteres");
        ValoresNumericosUtils.ValidarRangoShort(edad, (short) 1, (short) 100,
                "La edad es requerida y debe tener entre 1 y 100 años");

        ValoresNumericosUtils.ValidarRangoDouble(peso, 0.1, 200.0,
                "La peso es requerido y debe tener entre 1.0 y 200 kg");

        ValoresNumericosUtils.ValidarRangoDouble(estatura, 1.0, 2.0,
                "La estatura es requerida y debe tener entre 1.0 y 2.0 metros");

        StringCustomUtils.validarTamanio(email, 1, 100,
                "El email es requerido y debe contener entre 1 y 50 caracteres");

        StringCustomUtils.validarTamanio(telefono, 10, 10,
                "El teléfono es requerido y debe contener exactamente 10 dígitos(0-9)");
        if (estadoRegistro == null)
            throw new IllegalArgumentException("La especialidad es requerida");

    }
    public void borradoLogico(){
        validarNoeliminado();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    public void actualizarEstado(EstadoRegistro estadoRegistro) {
        validarNoeliminado();
        if (estadoRegistro == null)
            throw new IllegalArgumentException("El estado es requerido");
        this.estadoRegistro = estadoRegistro;
    }

    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad, Double peso, Double estatura, String email, String telefono, String direccion) {
        validarNoeliminado();
        validarDatos(nombre, apellidoPaterno, apellidoMaterno, edad, peso, estatura,
                email, telefono, direccion);
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.edad = edad;
        this.peso = peso;
        this.estatura = estatura;
        this.imc = peso/(estatura*estatura);
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        generarExpendiente(telefono);
    }
    private void validarNoeliminado() {
        if (this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new IllegalStateException("El paciente ya esta eliminado");
    }

    public void generarimc(Double estatura, Double peso){
        this.imc = peso/(estatura*estatura);
    }

    public void generarExpendiente(String telefono) {
        if (telefono == null || telefono.isBlank())
            throw new IllegalArgumentException("Telefono vacio");

        StringBuilder expediente = new StringBuilder();

        for (char digito : telefono.toCharArray()) {
            expediente.append(digito).append("X");
        }

        this.numExpediente = expediente.toString();
    }
}
