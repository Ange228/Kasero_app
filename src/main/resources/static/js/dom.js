// Ayudantes para armar la pantalla sin usar innerHTML (así el texto de la API nunca se interpreta como código).

/**
 * h('div', {class: 'caja', onclick: fn}, 'texto', otroNodo)
 * Los atributos con valor false/null/undefined se omiten.
 */
export function h(etiqueta, atributos, ...hijos) {
    const nodo = document.createElement(etiqueta);
    for (const [clave, valor] of Object.entries(atributos || {})) {
        if (valor === false || valor === null || valor === undefined) continue;
        if (clave.startsWith('on') && typeof valor === 'function') {
            nodo.addEventListener(clave.slice(2).toLowerCase(), valor);
        } else if (clave === 'class') {
            nodo.className = valor;
        } else if (valor === true) {
            nodo.setAttribute(clave, '');
        } else {
            nodo.setAttribute(clave, String(valor));
        }
    }
    agregar(nodo, hijos);
    return nodo;
}

function agregar(nodo, hijos) {
    for (const hijo of hijos.flat(Infinity)) {
        if (hijo === null || hijo === undefined || hijo === false) continue;
        nodo.append(hijo instanceof Node ? hijo : document.createTextNode(String(hijo)));
    }
}

export function vaciar(nodo) {
    while (nodo.firstChild) nodo.removeChild(nodo.firstChild);
}

export function poner(nodo, ...hijos) {
    vaciar(nodo);
    agregar(nodo, hijos);
}

const formatoSoles = new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN' });

export function soles(valor) {
    return valor === null || valor === undefined ? '—' : formatoSoles.format(valor);
}

export function cantidad(valor) {
    return new Intl.NumberFormat('es-PE', { maximumFractionDigits: 3 }).format(valor);
}

export function fechaCorta(iso) {
    const fecha = new Date(iso);
    if (Number.isNaN(fecha.getTime())) return '';
    return fecha.toLocaleString('es-PE', {
        day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit'
    });
}
