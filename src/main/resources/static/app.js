import { api } from './js/api.js';
import { h, poner } from './js/dom.js';
import { store } from './js/store.js';
import { avisar } from './js/ui.js';
import { acceso } from './js/views/acceso.js';
import { buscar } from './js/views/buscar.js';
import { comparar } from './js/views/comparar.js';
import { cotizar } from './js/views/cotizar.js';
import { historial } from './js/views/historial.js';
import { inicio } from './js/views/inicio.js';
import { perfil } from './js/views/perfil.js';

// Rutas con #: cada una dice qué vista dibuja y si pide sesión.
const RUTAS = [
    { patron: /^\/?$/, vista: inicio, menu: 'inicio', titulo: 'Inicio' },
    { patron: /^\/buscar$/, vista: buscar, menu: 'buscar', titulo: 'Buscar' },
    { patron: /^\/comerciante\/(\d+)$/, vista: perfil, menu: 'buscar', titulo: 'Puesto' },
    { patron: /^\/comparar$/, vista: comparar, menu: 'comparar', titulo: 'Comparar' },
    { patron: /^\/cotizar\/(\d+)$/, vista: cotizar, menu: 'comparar', titulo: 'Cotizar', privada: true },
    { patron: /^\/cotizaciones$/, vista: historial, menu: 'cotizaciones', titulo: 'Mis cotizaciones', privada: true },
    { patron: /^\/acceso$/, vista: acceso, menu: null, titulo: 'Ingresar' }
];

const ENLACES = [
    ['inicio', '#/', 'Inicio'],
    ['buscar', '#/buscar', 'Buscar'],
    ['comparar', '#/comparar', 'Comparar'],
    ['cotizaciones', '#/cotizaciones', 'Mis cotizaciones']
];

const raiz = document.getElementById('contenido');
let pedido = 0;

function dibujarMenus(activo) {
    const enlaces = () => ENLACES.map(([clave, href, texto]) =>
        h('a', { href, 'aria-current': clave === activo ? 'page' : false }, texto));
    poner(document.getElementById('menu-principal'), enlaces());
    poner(document.getElementById('menu-movil'), enlaces());
}

function dibujarCuenta() {
    const sesion = store.sesion;
    const caja = document.getElementById('cuenta');
    if (!sesion) {
        poner(caja, h('a', { class: 'boton boton--suave', href: `#/acceso?volver=${encodeURIComponent(location.hash || '#/')}` }, 'Ingresar'));
        return;
    }
    poner(caja,
        h('span', { class: 'cuenta__nombre' }, sesion.nombre),
        h('button', { class: 'boton boton--texto', type: 'button', onclick: salir }, 'Salir'));
}

async function salir() {
    try {
        await api.logout();
    } catch {
        // aunque falle el servidor, se cierra la sesión en este navegador
    }
    store.cerrarSesion();
    avisar('Cerraste sesión.');
    location.hash = '#/';
}

async function mostrar() {
    const ruta = (location.hash.replace(/^#/, '') || '/').split('?');
    const camino = ruta[0];
    const query = new URLSearchParams(ruta[1] || '');
    const actual = ++pedido;

    const encontrada = RUTAS.map((r) => ({ r, m: camino.match(r.patron) })).find((x) => x.m);
    if (!encontrada) {
        poner(raiz, h('div', { class: 'estado estado--vacio' },
            h('p', { class: 'estado__titulo' }, 'Esta página no existe'),
            h('a', { class: 'boton', href: '#/' }, 'Ir al inicio')));
        dibujarMenus(null);
        return;
    }

    const { r, m } = encontrada;
    if (r.privada && !store.sesion) {
        location.replace(`#/acceso?volver=${encodeURIComponent(location.hash)}`);
        return;
    }

    dibujarMenus(r.menu);
    dibujarCuenta();
    document.title = `${r.titulo} – Kasero`;
    await r.vista(raiz, { params: m.slice(1), query });
    if (actual === pedido) {
        raiz.focus({ preventScroll: true });
        window.scrollTo(0, 0);
    }
}

store.alCambiar(dibujarCuenta);
window.addEventListener('hashchange', mostrar);
mostrar();
