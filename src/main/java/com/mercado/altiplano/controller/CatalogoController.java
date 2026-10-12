package com.mercado.altiplano.controller;

import com.mercado.altiplano.dto.ComercianteDetalleDto;
import com.mercado.altiplano.dto.ComercianteResumenDto;
import com.mercado.altiplano.dto.PromocionDto;
import com.mercado.altiplano.model.Noticia;
import com.mercado.altiplano.model.Producto;
import com.mercado.altiplano.model.Sector;
import com.mercado.altiplano.service.CatalogoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/sectores")
    public List<Sector> sectores() {
        return catalogoService.sectores();
    }

    @GetMapping("/productos")
    public List<Producto> productos(
            @RequestParam(name = "sector", required = false) Long sectorId
    ) {
        return catalogoService.productos(sectorId);
    }

    /**
     * Búsqueda de comerciantes.
     * Ejemplo: /api/comerciantes?q=tomate&sector=1&orden=precio_asc
     */
    @GetMapping("/comerciantes")
    public List<ComercianteResumenDto> buscar(
            @RequestParam(name = "q", required = false) String texto,
            @RequestParam(name = "sector", required = false) Long sectorId,
            @RequestParam(name = "orden", required = false) String orden
    ) {
        return catalogoService.buscar(texto, sectorId, orden);
    }

    @GetMapping("/comerciantes/{id}")
    public ComercianteDetalleDto detalle(
            @PathVariable("id") long id
    ) {
        return catalogoService.detalle(id);
    }

    @GetMapping("/promociones")
    public List<PromocionDto> promociones() {
        return catalogoService.promociones();
    }

    @GetMapping("/noticias")
    public List<Noticia> noticias() {
        return catalogoService.noticias();
    }
}
