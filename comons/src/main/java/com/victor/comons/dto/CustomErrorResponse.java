package com.victor.comons.dto;

public record CustomErrorResponse(
        int codigo,
        String mensaje
) {
}
