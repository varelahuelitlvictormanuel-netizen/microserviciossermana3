package com.victor.comons.client;

import com.victor.comons.dto.medicos.MedicoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "medicos")
public interface MedicoClient {
        @GetMapping("/{id}")
        MedicoResponse obtenerMedicoActivoPorId(@PathVariable Long id);
        @GetMapping("/id-medico/{id}")
        MedicoResponse obtenerMedicoPorIdSinEstado(@PathVariable Long id);
    }

