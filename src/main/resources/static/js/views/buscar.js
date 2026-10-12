import { api } from '../api.js';
import { h, poner, soles } from '../dom.js';
import { cargando, estrellas, falla, sello, ubicacion, vacio } from '../ui.js';

const ORDENES = [
    ['precio_asc', 'Menor precio de la canasta'],
    ['precio_desc', 'Mayor precio de la canasta'],
    ['calificacion', 'Mejor calificados'],
    ['cotizaciones', 'Más cotizados']
];

export async function buscar(raiz, { query }) {
    const filtros = {
        q: query.get('q') || '',
        sector: query.get('sector') || '',
        orden: query.get('orden') || 'precio_asc'
    };

    const resultados = h('div', { id: 'resultados', 'aria-live': 'polite' }, cargando());
    let sectores = [];
    try {
        sectores = await api.sectores();
    } catch (error) {
        poner(raiz, falla(error, () => buscar(raiz, { query })));
        return;
    }

    const aplicar = (e) => {
        e && e.preventDefault();
        const datos = new FormData(formulario);
        const p = new URLSearchParams();
        for (const k of ['q', 'sector', 'orden']) {
            const v = String(datos.get(k) || '').trim();
            if (v) p.set(k, v);
        }
        const nuevo = `#/buscar?${p.toString()}`;
        if (location.hash === nuevo) cargar(); else location.hash = nuevo;
    };

    const formulario = h('form', { class: 'filtros', role: 'search', onsubmit: aplicar },
        h('div', { class: 'campo campo--ancho' },
            h('label', { for: 'f-q' }, 'Buscar'),
            h('input', { id: 'f-q', name: 'q', type: 'search', value: filtros.q, placeholder: 'Comerciante, producto o número de puesto', autocomplete: 'off' })),
        h('div', { class: 'campo' },
            h('label', { for: 'f-sector' }, 'Sector'),
            h('select', { id: 'f-sector', name: 'sector', onchange: aplicar },
                h('option', { value: '' }, 'Todos los sectores'),
                sectores.map((s) => h('option', { value: s.id, selected: String(s.id) === filtros.sector }, s.nombre)))),
        h('div', { class: 'campo' },
            h('label', { for: 'f-orden' }, 'Ordenar por'),
            h('select', { id: 'f-orden', name: 'orden', onchange: aplicar },
                ORDENES.map(([valor, texto]) => h('option', { value: valor, selected: valor === filtros.orden }, texto)))),
        h('button', { class: 'boton', type: 'submit' }, 'Buscar'));

    poner(raiz,
        h('h1', null, 'Buscar en el mercado'),
        formulario,
        resultados);

    async function cargar() {
        poner(resultados, cargando());
        try {
            const lista = await api.comerciantes(filtros);
            if (lista.length === 0) {
                poner(resultados, vacio('No encontramos resultados',
                    'Prueba con otra palabra, quita el filtro de sector o busca por producto.',
                    h('a', { class: 'boton boton--suave', href: '#/buscar' }, 'Ver todos los comerciantes')));
                return;
            }
            poner(resultados,
                h('p', { class: 'suave contador' }, `${lista.length} ${lista.length === 1 ? 'comerciante' : 'comerciantes'}`),
                h('ul', { class: 'lista-tarjetas' }, lista.map(tarjeta)),
                h('p', { class: 'suave nota-pie' },
                    'El precio de la canasta (2 kg de papa, 1 kg de cebolla, 1 kg de tomate y 1 kg de zanahoria) solo se muestra a quienes venden todo.'));
        } catch (error) {
            poner(resultados, falla(error, cargar));
        }
    }
    cargar();
}

function tarjeta(c) {
    return h('li', { class: `tarjeta${c.masEconomico ? ' tarjeta--mejor' : ''}` },
        h('div', { class: 'tarjeta__cuerpo' },
            h('h2', null, h('a', { href: `#/comerciante/${c.id}` }, c.nombre)),
            h('p', { class: 'tarjeta__linea' }, estrellas(c.calificacion, c.numResenas), ' · ', ubicacion(c.pasillo, c.puesto)),
            h('p', { class: 'suave' }, c.sectores.join(' · ')),
            h('p', { class: 'suave' }, `${c.numCotizaciones} cotizaciones`)),
        h('div', { class: 'tarjeta__precio' },
            c.precioCanasta === null
                ? h('p', { class: 'suave' }, 'Sin canasta completa')
                : [h('p', { class: 'etiqueta' }, 'Canasta'), h('p', { class: 'precio num' }, soles(c.precioCanasta))],
            c.masEconomico && sello('Más económico'),
            h('a', { class: 'boton boton--suave', href: `#/comerciante/${c.id}` }, 'Ver puesto')));
}
