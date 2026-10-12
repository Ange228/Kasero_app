package com.mercado.altiplano.controller;

import com.mercado.altiplano.dto.CotizacionRequest;
import com.mercado.altiplano.dto.CotizacionResponse;
import com.mercado.altiplano.service.CotizacionService;
import com.mercado.altiplano.service.SesionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Las cotizaciones son privadas: hay que mandar "Authorization: Bearer <token>" (el token lo da el login). */
@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionController {

    private final CotizacionService cotizacionService;
    private final SesionService sesionService;

    public CotizacionController(CotizacionService cotizacionService, SesionService sesionService) {
        this.cotizacionService = cotizacionService;
        this.sesionService = sesionService;
    }

    @PostMapping
    public ResponseEntity<CotizacionResponse> crear(
            @RequestHeader(name = "Authorization", required = false) String autorizacion,
            @RequestBody CotizacionRequest request
    ) {
        Long usuarioId = sesionService.usuarioDe(autorizacion);

        CotizacionResponse respuesta = CotizacionResponse.desde(
                cotizacionService.crear(usuarioId, request));

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public List<CotizacionResponse> listar(
            @RequestHeader(name = "Authorization", required = false) String autorizacion
    ) {
        Long usuarioId = sesionService.usuarioDe(autorizacion);

        return cotizacionService.listar(usuarioId).stream()
                .map(CotizacionResponse::desde)
                .toList();
    }
}
