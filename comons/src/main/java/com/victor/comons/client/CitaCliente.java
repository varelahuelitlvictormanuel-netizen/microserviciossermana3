package com.victor.comons.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "citas")
public interface CitaCliente {

    @GetMapping("/medico/{idMedico}/confirmada-en-curso")
    boolean tieneCitaConfirmadaOEnCursoMedico(
            @PathVariable Long idMedico
    );
}