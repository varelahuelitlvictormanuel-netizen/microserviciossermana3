package com.victor.comons.utils;

import java.math.BigDecimal;

public class ValoresNumericosUtils {
    public static <N extends Number> void validarNumeroRequerido(N numero){
        if (numero == null)
            throw  new IllegalArgumentException("El valor numerico es requerido");
    }
    public static void validarLongPositivo(Long numerico, String mensaje){
        validarNumeroRequerido(numerico);

        if (numerico < 0)
            throw  new IllegalArgumentException(mensaje);
    }
    public static void ValidarEnteroPositivo(Integer numero, String mesaje){
        validarNumeroRequerido(numero);
        if (numero< 0)
            throw  new IllegalArgumentException(mesaje);
    }
    public static void ValidarBigdecimalPositivo(BigDecimal numero, String mesaje){
        validarNumeroRequerido(numero);
        if (numero.compareTo(BigDecimal.ZERO) < 0)
            throw  new IllegalArgumentException(mesaje);
    }
    public static void ValidarBigdecimalPositivo(Short numero, Short min, Short max,String mesaje){
        validarNumeroRequerido(numero);
        if (numero <min || numero > max)
            throw  new IllegalArgumentException(mesaje);
    }
    public static void ValidarBigdecimalPositivo(Double numero, Double min, Double max,String mesaje){
        validarNumeroRequerido(numero);
        if (numero <min || numero > max)
            throw  new IllegalArgumentException(mesaje);
    }
    public static void validarRangoShort(Short numero, short min, short max, String mensaje){
        validarNumeroRequerido(numero);
        if (numero<min || numero>max)
            throw new IllegalArgumentException(mensaje);
    }
}
