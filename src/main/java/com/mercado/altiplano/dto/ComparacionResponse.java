package com.mercado.altiplano.dto;

import java.util.List;

public record ComparacionResponse(
        List<ItemCanastaDto> canasta,
        List<ResultadoComparacionDto> resultados
) {
}
