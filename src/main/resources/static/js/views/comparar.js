import { api } from '../api.js';
import { h, poner, soles, cantidad } from '../dom.js';
import { icono } from '../iconos.js';
import { store } from '../store.js';
import { cargando, falla, sello, vacio } from '../ui.js';

export async function comparar(raiz) {
    poner(raiz, cargando());
    let productos;
    try {
        productos = await api.productos();
        if (store.canasta.length === 0) {
            const referencia = await api.canastaReferencia();
            store.guardarCanasta(referencia.map((i) => ({
                productoId: i.productoId, nombre: i.producto, unidad: i.unidad, icono: i.icono, cantidad: i.cantidad
            })));
        }
    } catch (error) {
        poner(raiz, falla(error, () => comparar(raiz)));
        return;
    }

    const lista = h('div', { id: 'canasta-lista' });
    const resultado = h('div', { id: 'resultado', 'aria-live': 'polite' });
    let orden = 'asc';

    const items = () => store.canasta.map((i) => ({ productoId: i.productoId, cantidad: i.cantidad }));

    function dibujarCanasta() {
        const canasta = store.canasta;
        poner(lista, canasta.length === 0
            ? vacio('Tu canasta está vacía', 'Agrega al menos un producto para comparar.')
            : h('ul', { class: 'canasta' }, canasta.map((i) => h('li', null,
                h('span', { class: 'canasta__nombre' }, h('span', { 'aria-hidden': 'true' }, icono(i.icono) + ' '), i.nombre),
                h('label', { class: 'sr-solo', for: `c-${i.productoId}` }, `Cantidad de ${i.nombre}`),
                h('input', {
                    id: `c-${i.productoId}`, class: 'entrada-cantidad', type: 'number', inputmode: 'decimal',
                    min: '0.5', step: '0.5', value: String(i.cantidad),
                    onchange: (e) => cambiarCantidad(i.productoId, Number(e.target.value), e.target)
                }),
                h('span', { class: 'suave canasta__unidad' }, i.unidad),
                h('button', {
                    class: 'boton boton--texto', type: 'button',
                    'aria-label': `Quitar ${i.nombre}`,
                    onclick: () => quitar(i.productoId)
                }, 'Quitar')))));
    }

    function cambiarCantidad(id, valor, entrada) {
        if (!(valor > 0)) {
            entrada.setCustomValidity('Escribe una cantidad mayor a cero');
            entrada.reportValidity();
            return;
        }
        entrada.setCustomValidity('');
        store.guardarCanasta(store.canasta.map((i) => (i.productoId === id ? { ...i, cantidad: valor } : i)));
        calcular();
    }

    function quitar(id) {
        store.guardarCanasta(store.canasta.filter((i) => i.productoId !== id));
        dibujarCanasta();
        calcular();
    }

    const selector = h('select', { id: 'nuevo-producto' },
        h('option', { value: '' }, 'Elige un producto'),
        productos.map((p) => h('option', { value: p.id }, `${p.nombre} (${p.unidad})`)));

    const agregar = (e) => {
        e.preventDefault();
        const id = Number(selector.value);
        if (!id) return;
        const p = productos.find((x) => x.id === id);
        const canasta = store.canasta;
        if (!canasta.some((i) => i.productoId === id)) {
            canasta.push({ productoId: p.id, nombre: p.nombre, unidad: p.unidad, icono: p.icono, cantidad: 1 });
            store.guardarCanasta(canasta);
        }
        selector.value = '';
        dibujarCanasta();
        calcular();
    };

    async function calcular() {
        if (store.canasta.length === 0) {
            poner(resultado);
            return;
        }
        poner(resultado, cargando('Comparando…'));
        try {
            const r = await api.comparar(items(), orden);
            dibujarResultado(r);
        } catch (error) {
            poner(resultado, falla(error, calcular));
        }
    }

    function dibujarResultado(r) {
        const completos = r.resultados.filter((x) => x.completo);
        const incompletos = r.resultados.filter((x) => !x.completo);
        const mejor = completos.find((x) => x.masEconomico);

        poner(resultado,
            h('div', { class: 'resultado-cabecera' },
                h('h2', null, 'Resultado'),
                h('div', { class: 'campo campo--corto' },
                    h('label', { for: 'orden-cmp' }, 'Ordenar'),
                    h('select', { id: 'orden-cmp', onchange: (e) => { orden = e.target.value; calcular(); } },
                        h('option', { value: 'asc', selected: orden === 'asc' }, 'Menor a mayor precio'),
                        h('option', { value: 'desc', selected: orden === 'desc' }, 'Mayor a menor precio')))),
            completos.length === 0
                ? vacio('Ningún comerciante vende todo lo que elegiste',
                    'Quita algún producto para ver más opciones. Abajo ves qué le falta a cada puesto.')
                : [
                    completos.length >= 2 && mejor && h('div', { class: 'pizarra pizarra--mejor' },
                        h('p', { class: 'pizarra__titulo' }, 'Te conviene'),
                        h('p', { class: 'pizarra__total' }, h('span', null, mejor.nombre), h('span', { class: 'num' }, soles(mejor.total))),
                        h('p', { class: 'pizarra__nota' }, `Pasillo ${mejor.pasillo} · Puesto ${mejor.puesto}`)),
                    h('ol', { class: 'ranking' }, completos.map((x, i) => h('li', { class: `ranking__fila${x.masEconomico && completos.length >= 2 ? ' ranking__fila--mejor' : ''}` },
                        h('div', null,
                            h('h3', null, h('a', { href: `#/comerciante/${x.comercianteId}` }, x.nombre)),
                            h('p', { class: 'suave' }, `Pasillo ${x.pasillo} · Puesto ${x.puesto} · ★ ${x.calificacion.toFixed(1)}`)),
                        h('div', { class: 'ranking__precio' },
                            h('p', { class: 'precio num' }, soles(x.total)),
                            x.masEconomico && completos.length >= 2
                                ? sello('Más económico')
                                : h('p', { class: 'suave num' }, `+${soles(x.diferencia)}`)),
                        h('a', { class: 'boton boton--suave', href: `#/cotizar/${x.comercianteId}` }, 'Cotizar'))))],
            incompletos.length > 0 && h('details', { class: 'incompletos' },
                h('summary', null, `${incompletos.length} ${incompletos.length === 1 ? 'puesto no vende' : 'puestos no venden'} todo lo que elegiste`),
                h('ul', null, incompletos.map((x) => h('li', null,
                    h('strong', null, x.nombre), ': le falta ', x.faltantes.join(', '), '.')))));
    }

    poner(raiz,
        h('h1', null, 'Comparar mi canasta'),
        h('p', { class: 'suave' }, 'Solo se comparan los puestos que venden todos tus productos, así el precio es justo.'),
        h('div', { class: 'comparar' },
            h('section', { class: 'comparar__canasta', 'aria-labelledby': 'h-canasta' },
                h('h2', { id: 'h-canasta' }, 'Mi canasta'),
                lista,
                h('form', { class: 'agregar-producto', onsubmit: agregar },
                    h('label', { class: 'sr-solo', for: 'nuevo-producto' }, 'Agregar producto'),
                    selector,
                    h('button', { class: 'boton boton--suave', type: 'submit' }, 'Agregar'))),
            h('div', { class: 'comparar__resultado' }, resultado)));

    dibujarCanasta();
    calcular();
}
