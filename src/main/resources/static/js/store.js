// Estado del navegador: sesión y canasta en borrador.
// Se guarda en sessionStorage (se borra al cerrar la pestaña). Si el navegador lo bloquea, funciona en memoria.

const memoria = new Map();

function leer(clave) {
    try {
        return sessionStorage.getItem(clave);
    } catch {
        return memoria.get(clave) ?? null;
    }
}

function escribir(clave, valor) {
    try {
        if (valor === null) sessionStorage.removeItem(clave);
        else sessionStorage.setItem(clave, valor);
    } catch {
        if (valor === null) memoria.delete(clave);
        else memoria.set(clave, valor);
    }
}

function leerJson(clave, porDefecto) {
    try {
        const texto = leer(clave);
        return texto ? JSON.parse(texto) : porDefecto;
    } catch {
        return porDefecto;
    }
}

const oyentes = new Set();
const avisar = () => oyentes.forEach((f) => f());

export const store = {
    /** { token, nombre, usuarioId } o null */
    get sesion() {
        return leerJson('kasero.sesion', null);
    },
    iniciarSesion(respuesta) {
        escribir('kasero.sesion', JSON.stringify({
            token: respuesta.token,
            nombre: respuesta.nombre,
            usuarioId: respuesta.usuarioId
        }));
        avisar();
    },
    cerrarSesion() {
        escribir('kasero.sesion', null);
        avisar();
    },

    /** Canasta en borrador: [{ productoId, nombre, unidad, icono, cantidad }] */
    get canasta() {
        return leerJson('kasero.canasta', []);
    },
    guardarCanasta(items) {
        escribir('kasero.canasta', JSON.stringify(items));
        escribir('kasero.clave', null); // si cambia la canasta, la solicitud es otra
        avisar();
    },

    /** Clave para no duplicar una cotización si se toca "enviar" dos veces. */
    claveSolicitud() {
        let clave = leer('kasero.clave');
        if (!clave) {
            clave = (globalThis.crypto && crypto.randomUUID)
                ? crypto.randomUUID()
                : `k-${Date.now()}-${Math.random().toString(16).slice(2)}`;
            escribir('kasero.clave', clave);
        }
        return clave;
    },
    olvidarClave() {
        escribir('kasero.clave', null);
    },

    alCambiar(funcion) {
        oyentes.add(funcion);
        return () => oyentes.delete(funcion);
    }
};
