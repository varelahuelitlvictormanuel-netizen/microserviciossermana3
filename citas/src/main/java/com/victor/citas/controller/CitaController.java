package com.victor.citas.controller;

import com.victor.citas.dto.CitaRequest;
import com.victor.citas.dto.CitaResponse;
import com.victor.citas.service.CitaService;
import com.victor.comons.controller.CommonController;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class CitaController extends CommonController<CitaRequest, CitaResponse, CitaService> {
    public CitaController(CitaService service){
        super(service);
    }
    @PatchMapping("/{idCita}/estado/{idEstado}")
    public ResponseEntity<Void> actulizarEstadoCita(
            @PathVariable @Positive(message = "El idCita debe ser positivo") Long idCita,
            @PathVariable @Positive(message = "El idEstado debe ser positivo") Long idEstado){
        service.actulizarEstadoCita(idCita, idEstado);
        return ResponseEntity.noContent().build();
    }

}
