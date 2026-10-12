// Piezas de pantalla que se repiten en varias vistas.
import { h, soles, cantidad } from './dom.js';
import { icono } from './iconos.js';

export function cargando(texto = 'Cargando…') {
    return h('p', { class: 'estado estado--carga', role: 'status' }, h('span', { class: 'giro', 'aria-hidden': 'true' }), texto);
}

export function vacio(titulo, detalle, accion) {
    return h('div', { class: 'estado estado--vacio' },
        h('p', { class: 'estado__titulo' }, titulo),
        detalle && h('p', null, detalle),
        accion);
}

export function falla(error, reintentar) {
    return h('div', { class: 'estado estado--error', role: 'alert' },
        h('p', { class: 'estado__titulo' }, 'No pudimos cargar esto'),
        h('p', null, error.message),
        reintentar && h('button', { class: 'boton boton--suave', type: 'button', onclick: reintentar }, 'Intentar de nuevo'));
}

export function avisar(texto, tipo = 'ok') {
    const caja = document.getElementById('avisos');
    const aviso = h('p', { class: `aviso aviso--${tipo}` }, texto);
    caja.append(aviso);
    setTimeout(() => aviso.remove(), 4500);
}

export function estrellas(nota, resenas) {
    return h('span', { class: 'nota', title: `${nota} de 5` },
        h('span', { 'aria-hidden': 'true' }, '★ '),
        h('span', { class: 'num' }, nota.toFixed(1)),
        h('span', { class: 'sr-solo' }, ' de 5 estrellas'),
        resenas !== undefined && h('span', { class: 'nota__resenas' }, ` (${resenas})`));
}

export function ubicacion(pasillo, puesto) {
    return h('span', { class: 'ubicacion' }, `Pasillo ${pasillo} · Puesto ${puesto}`);
}

export function sello(texto) {
    return h('span', { class: 'sello' }, texto);
}

/**
 * La "pizarra de precios": el total de la canasta escrito como en la tiza de un puesto.
 * filas = [{ nombre, subtotal }]
 */
export function pizarra({ titulo, filas, total, nota }) {
    return h('section', { class: 'pizarra', 'aria-label': titulo },
        h('h3', { class: 'pizarra__titulo' }, titulo),
        filas && h('ul', { class: 'pizarra__filas' },
            filas.map((f) => h('li', null,
                h('span', null, f.nombre),
                h('span', { class: 'pizarra__puntos', 'aria-hidden': 'true' }),
                h('span', { class: 'num' }, soles(f.subtotal))))),
        h('p', { class: 'pizarra__total' },
            h('span', null, 'Total'),
            h('span', { class: 'num' }, soles(total))),
        nota && h('p', { class: 'pizarra__nota' }, nota));
}

export function chipProducto(p, cant) {
    return h('span', { class: 'chip-producto' },
        h('span', { 'aria-hidden': 'true' }, icono(p.icono)),
        ' ', p.producto || p.nombre,
        cant !== undefined && h('span', { class: 'num' }, ` ${cantidad(cant)} ${p.unidad}`));
}
