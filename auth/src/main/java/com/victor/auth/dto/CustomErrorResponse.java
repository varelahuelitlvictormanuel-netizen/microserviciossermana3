package com.victor.auth.dto;
public record CustomErrorResponse(
        int codigo,
        String mensaje
) { }
