import { api } from '../api.js';
import { h, poner } from '../dom.js';
import { store } from '../store.js';
import { avisar } from '../ui.js';

export function acceso(raiz, { query }) {
    const volver = query.get('volver') || '#/';
    const modo = query.get('modo') === 'registro' ? 'registro' : 'login';
    const esRegistro = modo === 'registro';

    const error = h('p', { class: 'form-error', id: 'error-acceso', role: 'alert' });
    const boton = h('button', { class: 'boton boton--ancho', type: 'submit' }, esRegistro ? 'Crear mi cuenta' : 'Ingresar');

    const enviar = async (e) => {
        e.preventDefault();
        poner(error);
        const datos = new FormData(e.target);
        boton.disabled = true;
        try {
            const respuesta = esRegistro
                ? await api.registro(datos.get('nombre').trim(), datos.get('correo').trim(), datos.get('password'))
                : await api.login(datos.get('correo').trim(), datos.get('password'));
            store.iniciarSesion(respuesta);
            avisar(`Hola, ${respuesta.nombre}.`);
            location.hash = volver;
        } catch (falla) {
            poner(error, falla.message);
            boton.disabled = false;
        }
    };

    const otroModo = `#/acceso?${new URLSearchParams({ modo: esRegistro ? 'login' : 'registro', volver })}`;

    poner(raiz,
        h('div', { class: 'acceso' },
            h('h1', null, esRegistro ? 'Crea tu cuenta' : 'Ingresa a Kasero'),
            h('p', { class: 'suave' }, 'Necesitas una cuenta para pedir cotizaciones y ver tu historial. Buscar y comparar es libre.'),
            h('form', { class: 'formulario', onsubmit: enviar, novalidate: true },
                esRegistro && campo('nombre', 'Nombre', 'text', 'name'),
                campo('correo', 'Correo', 'email', 'email'),
                campo('password', esRegistro ? 'Contraseña (mínimo 8 caracteres)' : 'Contraseña', 'password', esRegistro ? 'new-password' : 'current-password'),
                error,
                boton),
            h('p', { class: 'acceso__cambio' },
                esRegistro ? '¿Ya tienes cuenta? ' : '¿Aún no tienes cuenta? ',
                h('a', { href: otroModo }, esRegistro ? 'Ingresa' : 'Regístrate')),
            !esRegistro && h('p', { class: 'acceso__demo' },
                h('strong', null, 'Cuenta de prueba: '), 'demo@kasero.local · contraseña 123456')));
}

function campo(nombre, etiqueta, tipo, autocompletar) {
    const id = `a-${nombre}`;
    return h('div', { class: 'campo' },
        h('label', { for: id }, etiqueta),
        h('input', { id, name: nombre, type: tipo, required: true, autocomplete: autocompletar, 'aria-describedby': 'error-acceso' }));
}
