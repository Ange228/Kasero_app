import { store } from './store.js';

export class ErrorApi extends Error {
    constructor(mensaje, estado) {
        super(mensaje);
        this.estado = estado;
    }
}

async function pedir(metodo, ruta, cuerpo) {
    const cabeceras = { Accept: 'application/json' };
    if (cuerpo !== undefined) cabeceras['Content-Type'] = 'application/json';
    if (store.sesion) cabeceras.Authorization = `Bearer ${store.sesion.token}`;

    let respuesta;
    try {
        respuesta = await fetch(ruta, {
            method: metodo,
            headers: cabeceras,
            body: cuerpo === undefined ? undefined : JSON.stringify(cuerpo)
        });
    } catch {
        throw new ErrorApi('No pudimos conectar con el servidor. Revisa tu internet e intenta otra vez.', 0);
    }

    if (respuesta.status === 204) return null;

    let datos = null;
    try {
        datos = await respuesta.json();
    } catch {
        // respuesta sin cuerpo JSON
    }

    if (!respuesta.ok) {
        if (respuesta.status === 401 && store.sesion) {
            store.cerrarSesion(); // el token venció: se pide ingresar de nuevo
        }
        const mensaje = (datos && (datos.mensaje || datos.error)) || 'Algo salió mal. Intenta de nuevo.';
        throw new ErrorApi(mensaje, respuesta.status);
    }
    return datos;
}

const parametros = (objeto) => {
    const p = new URLSearchParams();
    for (const [k, v] of Object.entries(objeto)) {
        if (v !== undefined && v !== null && String(v).trim() !== '') p.set(k, v);
    }
    const texto = p.toString();
    return texto ? `?${texto}` : '';
};

export const api = {
    sectores: () => pedir('GET', '/api/sectores'),
    productos: (sector) => pedir('GET', `/api/productos${parametros({ sector })}`),
    comerciantes: (filtros) => pedir('GET', `/api/comerciantes${parametros(filtros)}`),
    comerciante: (id) => pedir('GET', `/api/comerciantes/${encodeURIComponent(id)}`),
    promociones: () => pedir('GET', '/api/promociones'),
    noticias: () => pedir('GET', '/api/noticias'),
    canastaReferencia: () => pedir('GET', '/api/canasta-referencia'),
    comparar: (items, orden) => pedir('POST', '/api/comparaciones', { items, orden }),
    cotizar: (comercianteId, items, claveSolicitud) =>
        pedir('POST', '/api/cotizaciones', { comercianteId, items, claveSolicitud }),
    cotizaciones: () => pedir('GET', '/api/cotizaciones'),
    login: (correo, password) => pedir('POST', '/api/auth/login', { correo, password }),
    registro: (nombre, correo, password) => pedir('POST', '/api/auth/registro', { nombre, correo, password }),
    logout: () => pedir('POST', '/api/auth/logout')
};
