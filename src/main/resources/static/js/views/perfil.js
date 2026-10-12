import { api } from '../api.js';
import { h, poner, soles, cantidad } from '../dom.js';
import { icono } from '../iconos.js';
import { store } from '../store.js';
import { avisar, cargando, estrellas, falla, ubicacion } from '../ui.js';

export async function perfil(raiz, { params }) {
    poner(raiz, cargando());
    let c;
    try {
        c = await api.comerciante(params[0]);
    } catch (error) {
        poner(raiz, error.estado === 404
            ? h('div', { class: 'estado estado--vacio' },
                h('p', { class: 'estado__titulo' }, 'Ese puesto no existe'),
                h('a', { class: 'boton boton--suave', href: '#/buscar' }, 'Volver a buscar'))
            : falla(error, () => perfil(raiz, { params })));
        return;
    }

    const aviso = h('p', { class: 'suave', id: 'resumen-canasta' });
    const actualizarResumen = () => {
        const n = store.canasta.length;
        poner(aviso, n === 0 ? 'Tu canasta está vacía.' : `Tu canasta tiene ${n} ${n === 1 ? 'producto' : 'productos'}.`);
    };
    actualizarResumen();

    const agregar = (producto, entrada) => {
        const valor = Number(entrada.value);
        if (!(valor > 0)) {
            avisar('Escribe una cantidad mayor a cero.', 'error');
            entrada.focus();
            return;
        }
        const canasta = store.canasta;
        const existente = canasta.find((i) => i.productoId === producto.productoId);
        if (existente) existente.cantidad = valor;
        else canasta.push({
            productoId: producto.productoId, nombre: producto.nombre,
            unidad: producto.unidad, icono: producto.icono, cantidad: valor
        });
        store.guardarCanasta(canasta);
        actualizarResumen();
        avisar(`${producto.nombre} agregado a tu canasta.`);
    };

    poner(raiz,
        h('p', { class: 'migas' }, h('a', { href: '#/buscar' }, '← Volver a buscar')),
        h('header', { class: 'perfil-cabecera' },
            h('div', null,
                h('h1', null, c.nombre),
                h('p', null, estrellas(c.calificacion, c.numResenas), ' · ', ubicacion(c.pasillo, c.puesto)),
                h('p', { class: 'suave' }, c.sectores.join(' · '))),
            h('a', { class: 'boton', href: `#/cotizar/${c.id}` }, 'Cotizar mi canasta')),
        h('p', { class: 'perfil-descripcion' }, c.descripcion),

        h('div', { class: 'perfil-columnas' },
            h('section', { 'aria-labelledby': 'h-pago' },
                h('h2', { id: 'h-pago' }, 'Cómo pagar'),
                h('ul', { class: 'etiquetas' }, c.metodosPago.map((m) => h('li', null, m)))),
            h('section', { 'aria-labelledby': 'h-entrega' },
                h('h2', { id: 'h-entrega' }, 'Cómo recibir tu pedido'),
                h('ul', { class: 'modalidades' }, c.modalidades.map((m) => h('li', null,
                    h('strong', null, m.etiqueta), h('span', { class: 'suave' }, m.descripcion)))))),

        h('section', { 'aria-labelledby': 'h-precios' },
            h('h2', { id: 'h-precios' }, 'Productos y precios'),
            h('p', { class: 'suave' }, 'Cada precio muestra la fecha en que se actualizó.'),
            aviso,
            h('div', { class: 'tabla-envoltura' },
                h('table', { class: 'tabla' },
                    h('caption', { class: 'sr-solo' }, `Productos de ${c.nombre}`),
                    h('thead', null, h('tr', null,
                        h('th', { scope: 'col' }, 'Producto'),
                        h('th', { scope: 'col', class: 'der' }, 'Precio'),
                        h('th', { scope: 'col' }, 'Actualizado'),
                        h('th', { scope: 'col' }, 'Cantidad'))),
                    h('tbody', null, c.productos.map((p) => fila(p, agregar)))))));
}

function fila(p, agregar) {
    const id = `cant-${p.productoId}`;
    const entrada = h('input', { id, type: 'number', inputmode: 'decimal', min: '0.5', step: '0.5', value: '1', class: 'entrada-cantidad' });
    return h('tr', null,
        h('th', { scope: 'row' }, h('span', { 'aria-hidden': 'true' }, icono(p.icono) + ' '), p.nombre),
        h('td', { class: 'der' }, h('span', { class: 'num' }, soles(p.precio)), h('span', { class: 'suave' }, ` / ${p.unidad}`)),
        h('td', { class: 'suave' }, p.actualizado),
        h('td', null, h('div', { class: 'agregar' },
            h('label', { class: 'sr-solo', for: id }, `Cantidad de ${p.nombre} en ${p.unidad}`),
            entrada,
            h('button', { class: 'boton boton--suave', type: 'button', onclick: () => agregar(p, entrada) }, 'Agregar'))));
}
