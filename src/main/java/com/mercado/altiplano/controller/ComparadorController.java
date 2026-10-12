package com.mercado.altiplano.controller;

import com.mercado.altiplano.dto.ComparacionRequest;
import com.mercado.altiplano.dto.ComparacionResponse;
import com.mercado.altiplano.dto.ItemCanastaDto;
import com.mercado.altiplano.service.ComparadorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ComparadorController {

    private final ComparadorService comparadorService;

    public ComparadorController(ComparadorService comparadorService) {
        this.comparadorService = comparadorService;
    }

    @GetMapping("/canasta-referencia")
    public List<ItemCanastaDto> canastaReferencia() {
        return comparadorService.canastaReferencia();
    }

    /** Si no se envían productos se compara la canasta de referencia. */
    @PostMapping("/comparaciones")
    public ComparacionResponse comparar(
            @RequestBody(required = false) ComparacionRequest request
    ) {
        return comparadorService.comparar(request);
    }
}
