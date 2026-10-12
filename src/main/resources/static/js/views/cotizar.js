import { api } from '../api.js';
import { h, poner, soles, cantidad } from '../dom.js';
import { icono } from '../iconos.js';
import { store } from '../store.js';
import { avisar, cargando, falla, pizarra, ubicacion, vacio } from '../ui.js';

export async function cotizar(raiz, { params }) {
    const canasta = store.canasta;
    if (canasta.length === 0) {
        poner(raiz,
            h('h1', null, 'Cotizar'),
            vacio('Tu canasta está vacía', 'Elige primero qué productos necesitas.',
                h('a', { class: 'boton', href: '#/comparar' }, 'Armar mi canasta')));
        return;
    }

    poner(raiz, cargando());
    let c;
    try {
        c = await api.comerciante(params[0]);
    } catch (error) {
        poner(raiz, falla(error, () => cotizar(raiz, { params })));
        return;
    }

    const precios = new Map(c.productos.map((p) => [p.productoId, p]));
    const lineas = canasta.map((i) => {
        const p = precios.get(i.productoId);
        return { ...i, precio: p ? p.precio : null, subtotal: p ? Number((p.precio * i.cantidad).toFixed(2)) : null };
    });
    const faltantes = lineas.filter((l) => l.precio === null);
    const total = lineas.reduce((suma, l) => suma + (l.subtotal || 0), 0);

    const error = h('p', { class: 'form-error', role: 'alert' });
    const boton = h('button', { class: 'boton boton--ancho', type: 'button', disabled: faltantes.length > 0, onclick: enviar }, 'Enviar cotización');

    async function enviar() {
        poner(error);
        boton.disabled = true;
        try {
            await api.cotizar(c.id, canasta.map((i) => ({ productoId: i.productoId, cantidad: i.cantidad })), store.claveSolicitud());
            store.guardarCanasta([]);
            store.olvidarClave();
            avisar('Cotización enviada. El comerciante la confirmará.');
            location.hash = '#/cotizaciones';
        } catch (falla) {
            poner(error, falla.message);
            boton.disabled = faltantes.length > 0;
        }
    }

    poner(raiz,
        h('p', { class: 'migas' }, h('a', { href: `#/comerciante/${c.id}` }, `← Volver a ${c.nombre}`)),
        h('h1', null, 'Revisa tu cotización'),
        h('p', null, h('strong', null, c.nombre), ' · ', ubicacion(c.pasillo, c.puesto)),

        faltantes.length > 0 && h('div', { class: 'estado estado--error', role: 'alert' },
            h('p', { class: 'estado__titulo' }, `${c.nombre} no vende todo lo de tu canasta`),
            h('p', null, `Le falta: ${faltantes.map((f) => f.nombre).join(', ')}.`),
            h('a', { class: 'boton boton--suave', href: '#/comparar' }, 'Ver quién tiene todo')),

        h('div', { class: 'cotizar' },
            h('div', { class: 'tabla-envoltura' },
                h('table', { class: 'tabla' },
                    h('caption', { class: 'sr-solo' }, 'Productos de la cotización'),
                    h('thead', null, h('tr', null,
                        h('th', { scope: 'col' }, 'Producto'),
                        h('th', { scope: 'col', class: 'der' }, 'Cantidad'),
                        h('th', { scope: 'col', class: 'der' }, 'Precio'),
                        h('th', { scope: 'col', class: 'der' }, 'Subtotal'))),
                    h('tbody', null, lineas.map((l) => h('tr', null,
                        h('th', { scope: 'row' }, h('span', { 'aria-hidden': 'true' }, icono(l.icono) + ' '), l.nombre),
                        h('td', { class: 'der num' }, `${cantidad(l.cantidad)} ${l.unidad}`),
                        h('td', { class: 'der num' }, soles(l.precio)),
                        h('td', { class: 'der num' }, soles(l.subtotal))))))),
            h('aside', { class: 'cotizar__lateral' },
                pizarra({ titulo: 'Tu cotización', total, nota: 'Los precios quedan guardados como estaban hoy.' }),
                h('section', { 'aria-labelledby': 'h-entrega' },
                    h('h2', { id: 'h-entrega', class: 'h-pequeno' }, 'Formas de recibir'),
                    h('ul', { class: 'modalidades' }, c.modalidades.map((m) => h('li', null,
                        h('strong', null, m.etiqueta), h('span', { class: 'suave' }, m.descripcion)))),
                    h('p', { class: 'suave' }, 'Coordinas la entrega con el comerciante al confirmar.')),
                error,
                boton)));
}
