package com.mercado.altiplano.repository;

import com.mercado.altiplano.model.Comerciante;
import com.mercado.altiplano.model.ItemCanasta;
import com.mercado.altiplano.model.Noticia;
import com.mercado.altiplano.model.PrecioProducto;
import com.mercado.altiplano.model.Producto;
import com.mercado.altiplano.model.Promocion;
import com.mercado.altiplano.model.Sector;

import java.util.List;
import java.util.Optional;

/** Todo lo que el sistema necesita leer del catálogo del mercado, sin saber de dónde viene. */
public interface CatalogoRepositoryPort {

    List<Sector> listarSectores();

    List<Producto> listarProductos();

    Optional<Producto> buscarProducto(long id);

    List<Comerciante> listarComerciantes();

    Optional<Comerciante> buscarComerciante(long id);

    List<PrecioProducto> listarPrecios(long comercianteId);

    Optional<PrecioProducto> buscarPrecio(long comercianteId, long productoId);

    List<Promocion> listarPromociones();

    List<Noticia> listarNoticias();

    /** Canasta de productos que se usa para comparar a los comerciantes. */
    List<ItemCanasta> canastaReferencia();
}
