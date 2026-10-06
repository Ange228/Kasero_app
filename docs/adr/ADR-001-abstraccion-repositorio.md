# ADR-001: Selección de arquitectura N-Capas

## Estado
Aceptada

## Contexto
El sistema Mercado del Altiplano requiere una arquitectura
que permita separar las responsabilidades de presentación,
lógica de negocio y persistencia.

## Decisión
Se utilizará una arquitectura N-Capas compuesta por:
- Capa de Presentación
- Capa de Servicios
- Capa de Dominio
- Capa de Persistencia

## Alternativas consideradas
- Arquitectura N-Capas
- Arquitectura monolítica sin separación de capas
- Arquitectura de microservicios

## Justificación
Se seleccionó N-Capas porque permite separar responsabilidades,
facilita el mantenimiento y resulta adecuada para el alcance
del MVP y el tiempo disponible de desarrollo.

## Consecuencias
- Facilita la modificación de componentes.
- Mejora la organización del código.
- Permite realizar pruebas de manera más aislada.
- Requiere mantener una separación clara entre las capas.
