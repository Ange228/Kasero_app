import { api } from '../api.js';
import { h, poner, soles, cantidad, fechaCorta } from '../dom.js';
import { icono } from '../iconos.js';
import { cargando, falla, vacio } from '../ui.js';

const CLASE_ESTADO = { PENDIENTE: 'pendiente', CONFIRMADA: 'confirmada', RECHAZADA: 'rechazada' };

export async function historial(raiz) {
    poner(raiz, h('h1', null, 'Mis cotizaciones'), cargando());
    let lista;
    try {
        lista = await api.cotizaciones();
    } catch (error) {
        poner(raiz, h('h1', null, 'Mis cotizaciones'), falla(error, () => historial(raiz)));
        return;
    }

    poner(raiz,
        h('h1', null, 'Mis cotizaciones'),
        lista.length === 0
            ? vacio('Aún no pediste ninguna cotización',
                'Compara tu canasta y pide una cotización al puesto que más te convenga.',
                h('a', { class: 'boton', href: '#/comparar' }, 'Comparar mi canasta'))
            : h('ul', { class: 'lista-tarjetas' }, lista.map((c) => h('li', { class: 'tarjeta tarjeta--columna' },
                h('div', { class: 'tarjeta__encabezado' },
                    h('div', null,
                        h('h2', null, h('a', { href: `#/comerciante/${c.comercianteId}` }, c.comerciante)),
                        h('p', { class: 'suave' }, `Cotización n.º ${c.id} · ${fechaCorta(c.fecha)}`)),
                    h('span', { class: `estado-chip estado-chip--${CLASE_ESTADO[c.estado] || 'pendiente'}` }, c.estadoEtiqueta)),
                h('ul', { class: 'lineas' }, c.items.map((i) => h('li', null,
                    h('span', null, h('span', { 'aria-hidden': 'true' }, icono(i.icono) + ' '), `${i.producto} · ${cantidad(i.cantidad)} ${i.unidad}`),
                    h('span', { class: 'num' }, soles(i.subtotal))))),
                h('p', { class: 'lineas__total' }, h('span', null, 'Total'), h('span', { class: 'num' }, soles(c.total)))))));
}
