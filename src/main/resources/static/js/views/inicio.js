import { api } from '../api.js';
import { h, poner, soles } from '../dom.js';
import { icono } from '../iconos.js';
import { cargando, falla, ubicacion } from '../ui.js';

export async function inicio(raiz) {
    poner(raiz, cargando());
    let sectores, promociones, noticias, comparacion;
    try {
        [sectores, promociones, noticias, comparacion] = await Promise.all([
            api.sectores(), api.promociones(), api.noticias(), api.comparar()
        ]);
    } catch (error) {
        poner(raiz, falla(error, () => inicio(raiz)));
        return;
    }

    const completos = comparacion.resultados.filter((r) => r.completo);

    poner(raiz,
        h('section', { class: 'portada' },
            h('div', { class: 'portada__texto' },
                h('h1', null, 'Compara antes de caminar el mercado'),
                h('p', null, 'Mira cuánto cuesta tu canasta en cada puesto y cotiza con el comerciante que más te conviene.'),
                h('form', { class: 'buscador', role: 'search', onsubmit: (e) => {
                    e.preventDefault();
                    const q = new FormData(e.target).get('q').trim();
                    location.hash = `#/buscar${q ? `?q=${encodeURIComponent(q)}` : ''}`;
                } },
                    h('label', { class: 'sr-solo', for: 'q-inicio' }, 'Buscar comerciante, producto o puesto'),
                    h('input', { id: 'q-inicio', name: 'q', type: 'search', placeholder: 'Busca papa, tomate o "puesto 120"', autocomplete: 'off' }),
                    h('button', { class: 'boton', type: 'submit' }, 'Buscar'))),
            h('div', { class: 'portada__pizarra' }, canastaDelDia(comparacion, completos))),

        h('section', { 'aria-labelledby': 'h-sectores' },
            h('h2', { id: 'h-sectores' }, 'Sectores del mercado'),
            h('ul', { class: 'sectores' }, sectores.map((s) => h('li', null,
                h('a', { href: `#/buscar?sector=${s.id}` },
                    h('span', { class: 'sectores__icono', 'aria-hidden': 'true' }, icono(s.icono)),
                    h('span', null, s.nombre)))))),

        h('section', { 'aria-labelledby': 'h-promos' },
            h('h2', { id: 'h-promos' }, 'Promociones de hoy'),
            promociones.length === 0
                ? h('p', { class: 'suave' }, 'Hoy no hay promociones publicadas.')
                : h('ul', { class: 'promos' }, promociones.map((p) => h('li', { class: 'promo' },
                    h('span', { class: 'promo__descuento' }, `-${p.descuento}%`),
                    h('div', null,
                        h('h3', null, h('span', { 'aria-hidden': 'true' }, icono(p.icono) + ' '), p.titulo),
                        h('p', { class: 'promo__precio' },
                            h('s', { class: 'num suave' }, soles(p.precioOriginal)), ' ',
                            h('strong', { class: 'num' }, soles(p.precioPromocional)),
                            ` por ${p.unidad}`),
                        h('p', { class: 'suave' },
                            h('a', { href: `#/comerciante/${p.comercianteId}` }, p.comerciante), ' · ',
                            ubicacion(p.pasillo, p.puesto))))))),

        h('section', { 'aria-labelledby': 'h-noticias' },
            h('h2', { id: 'h-noticias' }, 'Avisos del mercado'),
            h('ul', { class: 'noticias' }, noticias.map((n) => h('li', null,
                h('span', { class: 'noticias__icono', 'aria-hidden': 'true' }, icono(n.icono)),
                h('div', null, h('h3', null, n.titulo), h('p', null, n.detalle)))))));
}

function canastaDelDia(comparacion, completos) {
    const lista = comparacion.canasta.map((i) => `${i.cantidad} ${i.unidad} de ${i.producto.toLowerCase()}`).join(', ');
    return h('section', { class: 'pizarra pizarra--portada', 'aria-label': 'Canasta del día' },
        h('h2', { class: 'pizarra__titulo' }, 'Canasta del día'),
        h('p', { class: 'pizarra__nota' }, lista),
        completos.length === 0
            ? h('p', { class: 'pizarra__nota' }, 'Todavía no hay precios para comparar.')
            : h('ol', { class: 'pizarra__filas pizarra__filas--ranking' },
                completos.slice(0, 3).map((r) => h('li', { class: r.masEconomico ? 'es-mejor' : null },
                    h('a', { href: `#/comerciante/${r.comercianteId}` }, r.nombre),
                    h('span', { class: 'pizarra__puntos', 'aria-hidden': 'true' }),
                    h('span', { class: 'num' }, soles(r.total)),
                    r.masEconomico && h('span', { class: 'sello sello--tiza' }, 'Más económico')))),
        h('a', { class: 'boton boton--tiza', href: '#/comparar' }, 'Armar mi canasta'));
}
