package com.victor.paciente.entity;

import com.victor.comons.enums.EstadoRegistro;
import com.victor.comons.utils.StringCustomUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "PACIENTE")
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
    private String num_expediente;

    @Column(name = "TELEFONO", nullable = false, length = 10)
    private String telefono;

    @Column(name = "DIRECCION", nullable = false, length = 150)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estado_registro;

    public void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad, Double peso, Double estatura, Double imc, String email, String num_expediente, String telefono, String direccion, EstadoRegistro estado_registro) {
        StringCustomUtils.validarTamanio(nombre, 1, 50, "El nombre es requerido y debe contener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoPaterno, 1, 50, "El apellidoPaterno es requerido y debe contener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoMaterno, 1, 50, "El apellidoMaterno es requerido y debe contener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(direccion, 1, 150, "La direción es requerido y debe contener entre 1 y 150 caracteres");
        StringCustomUtils.validarTamanio(edad, 1, 100, "La edad es requerida y debe contener entre 1 y 100 caracteres");
        StringCustomUtils.validarTamanio(peso, 0.1, 200, "La peso es requerido y debe contener entre 0.1 y 200 caracteres");
        StringCustomUtils.validarTamanio(estatura, 0.1, 2.0, "La estatura es requerida y debe contener entre 1.0 y 2.0 caracteres");
        StringCustomUtils.validarTamanio(telefono, 0.1, 2.0, "La estatura es requerida y debe contener entre 1.0 y 2.0 caracteres");
    }

}
