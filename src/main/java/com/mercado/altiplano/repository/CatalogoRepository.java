package com.mercado.altiplano.repository;

import com.mercado.altiplano.model.Comerciante;
import com.mercado.altiplano.model.ItemCanasta;
import com.mercado.altiplano.model.ModalidadEntrega;
import com.mercado.altiplano.model.Noticia;
import com.mercado.altiplano.model.PrecioProducto;
import com.mercado.altiplano.model.Producto;
import com.mercado.altiplano.model.Promocion;
import com.mercado.altiplano.model.Sector;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mercado.altiplano.model.ModalidadEntrega.ENVIO;
import static com.mercado.altiplano.model.ModalidadEntrega.RECOJO_EN_PUESTO;
import static com.mercado.altiplano.model.ModalidadEntrega.TERCERO_AUTORIZADO;

/**
 * Catálogo del mercado en memoria.
 *
 * IMPORTANTE: los comerciantes, productos y precios de esta clase son DATOS DE EJEMPLO
 * para poder probar y demostrar la plataforma. No corresponden a puestos ni precios reales.
 * En el siguiente sprint se reemplazan por PostgreSQL y por la carga desde Datos Abiertos
 * y el administrador, sin cambiar los servicios (por eso existe CatalogoRepositoryPort).
 */
@Repository
public class CatalogoRepository implements CatalogoRepositoryPort {

    /** Fecha en que se cargaron los precios de ejemplo. */
    private static final LocalDate FECHA_CARGA = LocalDate.of(2026, 10, 12);

    // ---- Sectores ----
    private static final long VERDURAS = 1;
    private static final long FRUTAS = 2;
    private static final long CARNES = 3;
    private static final long PESCADOS = 4;
    private static final long ABARROTES = 5;
    private static final long COMIDA = 6;

    // ---- Productos ----
    private static final long PAPA = 1;
    private static final long CEBOLLA = 2;
    private static final long TOMATE = 3;
    private static final long ZANAHORIA = 4;
    private static final long CHOCLO = 5;
    private static final long FRESA = 6;
    private static final long PLATANO = 7;
    private static final long PALTA = 8;
    private static final long MANZANA = 9;
    private static final long POLLO = 10;
    private static final long RES = 11;
    private static final long TRUCHA = 12;
    private static final long ARROZ = 13;
    private static final long AZUCAR = 14;
    private static final long ACEITE = 15;
    private static final long MENU = 16;
    private static final long BRASA = 17;

    private final List<Sector> sectores = List.of(
            new Sector(VERDURAS, "Verduras", "verduras"),
            new Sector(FRUTAS, "Frutas", "frutas"),
            new Sector(CARNES, "Carnes", "carnes"),
            new Sector(PESCADOS, "Pescados", "pescados"),
            new Sector(ABARROTES, "Abarrotes", "abarrotes"),
            new Sector(COMIDA, "Comida preparada", "comida"),
            new Sector(7, "Otros", "otros")
    );

    private final List<Producto> productos = List.of(
            new Producto(PAPA, "Papa", VERDURAS, "kg", "papa"),
            new Producto(CEBOLLA, "Cebolla", VERDURAS, "kg", "cebolla"),
            new Producto(TOMATE, "Tomate", VERDURAS, "kg", "tomate"),
            new Producto(ZANAHORIA, "Zanahoria", VERDURAS, "kg", "zanahoria"),
            new Producto(CHOCLO, "Choclo", VERDURAS, "unidad", "choclo"),
            new Producto(FRESA, "Fresa", FRUTAS, "kg", "fresa"),
            new Producto(PLATANO, "Plátano de seda", FRUTAS, "kg", "platano"),
            new Producto(PALTA, "Palta", FRUTAS, "kg", "palta"),
            new Producto(MANZANA, "Manzana", FRUTAS, "kg", "manzana"),
            new Producto(POLLO, "Pollo entero", CARNES, "kg", "pollo"),
            new Producto(RES, "Carne de res", CARNES, "kg", "res"),
            new Producto(TRUCHA, "Trucha", PESCADOS, "kg", "pescado"),
            new Producto(ARROZ, "Arroz", ABARROTES, "kg", "arroz"),
            new Producto(AZUCAR, "Azúcar", ABARROTES, "kg", "azucar"),
            new Producto(ACEITE, "Aceite", ABARROTES, "litro", "aceite"),
            new Producto(MENU, "Menú del día", COMIDA, "unidad", "menu"),
            new Producto(BRASA, "Pollo a la brasa (1/4)", COMIDA, "unidad", "brasa")
    );

    private final List<Comerciante> comerciantes = List.of(
            new Comerciante(1, "Casera Anita",
                    "Verduras y frutas frescas del valle, directo del campo.",
                    List.of(VERDURAS, FRUTAS), 4, 120, 4.8, 128, 58,
                    List.of("Yape", "Plin", "Transferencia"),
                    List.of(ENVIO, RECOJO_EN_PUESTO, TERCERO_AUTORIZADO), true),
            new Comerciante(2, "Don José",
                    "Verduras de temporada y papa de la zona.",
                    List.of(VERDURAS), 3, 87, 4.6, 93, 41,
                    List.of("Yape", "Efectivo"),
                    List.of(RECOJO_EN_PUESTO, TERCERO_AUTORIZADO), true),
            new Comerciante(3, "María",
                    "Verduras y frutas de estación al mejor precio.",
                    List.of(VERDURAS, FRUTAS), 5, 45, 4.7, 76, 37,
                    List.of("Yape", "Plin", "Efectivo"),
                    List.of(ENVIO, RECOJO_EN_PUESTO), true),
            new Comerciante(4, "Don Manuel",
                    "Frutas de estación y plátano de seda.",
                    List.of(FRUTAS), 2, 61, 4.5, 54, 22,
                    List.of("Yape", "Efectivo"),
                    List.of(RECOJO_EN_PUESTO), true),
            new Comerciante(5, "Puesto El Buen Sabor",
                    "Pollo a la brasa y menú del día.",
                    List.of(COMIDA, CARNES), 2, 35, 4.7, 210, 64,
                    List.of("Yape", "Plin", "Efectivo"),
                    List.of(ENVIO, RECOJO_EN_PUESTO, TERCERO_AUTORIZADO), true),
            new Comerciante(6, "Pescadería Don Lucho",
                    "Trucha fresca y pescado del día.",
                    List.of(PESCADOS), 7, 9, 4.4, 38, 15,
                    List.of("Efectivo", "Yape"),
                    List.of(RECOJO_EN_PUESTO), true),
            new Comerciante(7, "Abarrotes Doña Rosa",
                    "Arroz, azúcar, aceite y más para tu despensa.",
                    List.of(ABARROTES), 1, 22, 4.6, 67, 19,
                    List.of("Yape", "Plin", "Transferencia", "Efectivo"),
                    List.of(ENVIO, RECOJO_EN_PUESTO, TERCERO_AUTORIZADO), true),
            new Comerciante(8, "Carnes Don Pepe",
                    "Cortes de res y pollo del día.",
                    List.of(CARNES), 6, 14, 4.5, 45, 28,
                    List.of("Efectivo", "Yape"),
                    List.of(RECOJO_EN_PUESTO, TERCERO_AUTORIZADO), true)
    );

    private final List<PrecioProducto> precios = cargarPrecios();

    private final List<Promocion> promociones = List.of(
            new Promocion(1, 1, FRESA, "Fresas en promoción", 30),
            new Promocion(2, 5, BRASA, "Pollo a la brasa", 10),
            new Promocion(3, 4, PLATANO, "Plátano de seda", 10)
    );

    private final List<Noticia> noticias = List.of(
            new Noticia(1, "Gran sorteo por aniversario",
                    "Participa en la actividad por el aniversario del mercado.", "sorteo"),
            new Noticia(2, "Nuevo horario este domingo",
                    "El mercado atenderá de 6 a. m. a 3 p. m.", "horario")
    );

    /** Canasta de referencia: 2 kg de papa y 1 kg de cebolla, tomate y zanahoria. */
    private final List<ItemCanasta> canasta = List.of(
            new ItemCanasta(PAPA, new BigDecimal("2")),
            new ItemCanasta(CEBOLLA, new BigDecimal("1")),
            new ItemCanasta(TOMATE, new BigDecimal("1")),
            new ItemCanasta(ZANAHORIA, new BigDecimal("1"))
    );

    private static List<PrecioProducto> cargarPrecios() {
        List<PrecioProducto> lista = new ArrayList<>();

        // 1 - Casera Anita
        lista.add(precio(1, PAPA, "3.00"));
        lista.add(precio(1, CEBOLLA, "2.50"));
        lista.add(precio(1, TOMATE, "4.00"));
        lista.add(precio(1, ZANAHORIA, "2.00"));
        lista.add(precio(1, CHOCLO, "1.50"));
        lista.add(precio(1, FRESA, "6.00"));
        lista.add(precio(1, PALTA, "9.00"));
        lista.add(precio(1, MANZANA, "5.50"));

        // 2 - Don José
        lista.add(precio(2, PAPA, "3.20"));
        lista.add(precio(2, CEBOLLA, "2.60"));
        lista.add(precio(2, TOMATE, "4.20"));
        lista.add(precio(2, ZANAHORIA, "2.20"));
        lista.add(precio(2, CHOCLO, "1.60"));

        // 3 - María
        lista.add(precio(3, PAPA, "3.50"));
        lista.add(precio(3, CEBOLLA, "2.80"));
        lista.add(precio(3, TOMATE, "4.50"));
        lista.add(precio(3, ZANAHORIA, "2.50"));
        lista.add(precio(3, FRESA, "6.50"));
        lista.add(precio(3, PLATANO, "3.00"));
        lista.add(precio(3, MANZANA, "5.80"));

        // 4 - Don Manuel
        lista.add(precio(4, PLATANO, "2.80"));
        lista.add(precio(4, FRESA, "6.20"));
        lista.add(precio(4, PALTA, "8.50"));
        lista.add(precio(4, MANZANA, "5.20"));

        // 5 - Puesto El Buen Sabor
        lista.add(precio(5, POLLO, "12.50"));
        lista.add(precio(5, MENU, "12.00"));
        lista.add(precio(5, BRASA, "20.00"));

        // 6 - Pescadería Don Lucho
        lista.add(precio(6, TRUCHA, "18.00"));

        // 7 - Abarrotes Doña Rosa
        lista.add(precio(7, ARROZ, "4.80"));
        lista.add(precio(7, AZUCAR, "4.20"));
        lista.add(precio(7, ACEITE, "9.50"));

        // 8 - Carnes Don Pepe
        lista.add(precio(8, POLLO, "12.00"));
        lista.add(precio(8, RES, "28.00"));

        return List.copyOf(lista);
    }

    private static PrecioProducto precio(long comercianteId, long productoId, String valor) {
        return new PrecioProducto(comercianteId, productoId, new BigDecimal(valor), FECHA_CARGA);
    }

    @Override
    public List<Sector> listarSectores() {
        return sectores;
    }

    @Override
    public List<Producto> listarProductos() {
        return productos;
    }

    @Override
    public Optional<Producto> buscarProducto(long id) {
        return productos.stream().filter(p -> p.id() == id).findFirst();
    }

    @Override
    public List<Comerciante> listarComerciantes() {
        return comerciantes;
    }

    @Override
    public Optional<Comerciante> buscarComerciante(long id) {
        return comerciantes.stream().filter(c -> c.id() == id).findFirst();
    }

    @Override
    public List<PrecioProducto> listarPrecios(long comercianteId) {
        return precios.stream()
                .filter(p -> p.comercianteId() == comercianteId)
                .toList();
    }

    @Override
    public Optional<PrecioProducto> buscarPrecio(long comercianteId, long productoId) {
        return precios.stream()
                .filter(p -> p.comercianteId() == comercianteId && p.productoId() == productoId)
                .findFirst();
    }

    @Override
    public List<Promocion> listarPromociones() {
        return promociones;
    }

    @Override
    public List<Noticia> listarNoticias() {
        return noticias;
    }

    @Override
    public List<ItemCanasta> canastaReferencia() {
        return canasta;
    }
}
