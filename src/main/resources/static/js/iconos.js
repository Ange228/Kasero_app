// La API devuelve claves de icono (por ejemplo "papa"); aquí se convierten en emoji.
const ICONOS = {
    verduras: '🥬', frutas: '🍓', carnes: '🥩', pescados: '🐟', abarrotes: '🛒', comida: '🍲', otros: '🧺',
    papa: '🥔', cebolla: '🧅', tomate: '🍅', zanahoria: '🥕', choclo: '🌽',
    fresa: '🍓', manzana: '🍎', platano: '🍌', naranja: '🍊', palta: '🥑',
    pollo: '🍗', res: '🥩', pescado: '🐟', brasa: '🍗', menu: '🍲',
    arroz: '🍚', fideos: '🍝', aceite: '🫒', huevo: '🥚', azucar: '🍬',
    sorteo: '🎁', horario: '🕘'
};

export function icono(clave) {
    return ICONOS[clave] || '🧺';
}
