# Kasero - Mercado del Altiplano

Sistema web orientado a la digitalización y gestión de los puestos del **Mercado del Altiplano**, desarrollado como proyecto académico para la evaluación de arquitectura y calidad de software.

![Java](https://img.shields.io/badge/JAVA-21-ED8B00?style=for-the-badge\&logo=openjdk\&logoColor=white)
![Spring Boot](https://img.shields.io/badge/SPRING_BOOT-4.1.1-6DB33F?style=for-the-badge\&logo=springboot\&logoColor=white)
![Maven](https://img.shields.io/badge/MAVEN-3.10.0-C71A36?style=for-the-badge\&logo=apachemaven\&logoColor=white)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge\&logo=html5\&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge\&logo=css3\&logoColor=white)
![JavaScript](https://img.shields.io/badge/JAVASCRIPT-F7DF1E?style=for-the-badge\&logo=javascript\&logoColor=black)
![PlantUML](https://img.shields.io/badge/PLANTUML-FFFFFF?style=for-the-badge\&logo=uml\&logoColor=black)
![Status](https://img.shields.io/badge/STATUS-En%20desarrollo-F2C94C?style=for-the-badge)

---

## 1. Descripcion del Sistema

**Kasero** es una aplicación web orientada a facilitar la interacción entre clientes y puestos del Mercado del Altiplano, proporcionando una base para la consulta de información, comunicación y futura gestión de pedidos y reservas.

La idea central: el cliente **compara cuánto le cuesta su canasta en cada puesto** y **pide una cotización** al comerciante que más le conviene.

Actualmente el sistema incluye (avance del Hito 1):

* **Cuenta de usuario:** registro e inicio de sesión (contraseñas con PBKDF2, sesión con token que vence a las 8 horas).
* **Catálogo del mercado:** sectores, productos, comerciantes, promociones y avisos.
* **Búsqueda y filtros:** por nombre, producto, sector o número de puesto; orden por precio, calificación o más cotizados.
* **Perfil del puesto:** ubicación (pasillo y puesto), métodos de pago, formas de entrega y precios con su fecha.
* **Comparador de canasta:** compara solo a quienes venden todos los productos elegidos.
* **Cotizaciones:** crear una cotización (sin duplicados si se envía dos veces) y ver el historial.
* **Interfaz web:** inicio, buscar, perfil, comparar, cotizar, historial e ingreso; se adapta al celular.
* **Separación por capas:** presentación, aplicación/negocio, acceso a datos y dominio.
* **Abstracción de persistencia:** puertos (`...RepositoryPort`) con implementación en memoria.
* **Manejo centralizado de errores:** `GlobalExceptionHandler`.
* **Documentación arquitectónica:** diagramas PlantUML y ADR.

> **Datos de demostración:** los comerciantes, productos y precios del catálogo son inventados para probar el sistema. Todavía no se cargan datos reales (el dataset de Datos Abiertos Perú se integrará en el Hito 2).

---

## 2. Arquitectura del Proyecto

El proyecto utiliza una arquitectura organizada en capas, con separación entre la presentación, lógica de aplicación, acceso a datos y modelo de dominio.

```text
┌─────────────────────────────────────────────┐
│           CAPA DE PRESENTACION              │
│                                             │
│ HTML / CSS / JavaScript                     │
│ AuthController                              │
└──────────────────────┬──────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────┐
│       CAPA DE APLICACION / NEGOCIO          │
│                                             │
│ AuthService                                 │
│ AuthenticationException                     │
└──────────────────────┬──────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────┐
│           CAPA DE ACCESO A DATOS            │
│                                             │
│ UsuarioRepositoryPort                       │
│ UsuarioRepository                           │
└──────────────────────┬──────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────┐
│             CAPA DE DOMINIO                 │
│                                             │
│ Usuario / Chat / Mensaje                    │
└─────────────────────────────────────────────┘
```

La arquitectura también incorpora componentes de soporte para el manejo de errores y objetos de transferencia:

```text
LoginRequest
LoginResponse
GlobalExceptionHandler
```

---

## 3. Estructura de Directorios

La organización física actual del proyecto es la siguiente:

```text
Kasero_project/
├── docs/
│   └── adr/
│       └── ADR-001-abstraccion-repositorio.md
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── mercado/
│       │           └── altiplano/
│       │               ├── MercadoApplication.java
│       │               │
│       │               ├── config/        (usuario de prueba)
│       │               ├── controller/    (Auth, Catalogo, Comparador, Cotizacion)
│       │               ├── dto/           (datos que entran y salen de la API)
│       │               ├── exception/     (errores y GlobalExceptionHandler)
│       │               ├── model/         (Usuario, Comerciante, Producto, Cotizacion...)
│       │               ├── repository/    (puertos + implementación en memoria)
│       │               ├── security/      (PasswordHasher)
│       │               └── service/       (Auth, Sesion, Catalogo, Comparador, Cotizacion)
│       │
│       └── resources/
│           └── static/
│               ├── index.html
│               ├── styles.css
│               ├── app.js          (rutas y menús)
│               └── js/             (api, store, ui y una vista por pantalla)
│
│   └── test/java/...               (pruebas unitarias de servicios)
│
├── .gitignore
├── pom.xml
└── README.md
```

---

## 4. Tecnologias y Herramientas

| Tecnología / Herramienta | Versión | Uso                                    |
| :----------------------- | :------ | :------------------------------------- |
| **Java**                 | 21      | Lenguaje de desarrollo                 |
| **Spring Boot**          | 4.1.1   | Backend y API REST                     |
| **Maven**                | 3.10.0  | Gestión de dependencias y construcción |
| **HTML5**                | -       | Estructura de la interfaz              |
| **CSS3**                 | -       | Diseño visual                          |
| **JavaScript**           | -       | Comunicación con la API                |
| **PlantUML**             | -       | Modelado de arquitectura y clases      |
| **Markdown**             | -       | Documentación y ADR                    |

---

## 5. Modulo de Autenticacion

El módulo de autenticación implementa actualmente el flujo de inicio de sesión.

### Flujo de autenticación

```text
Usuario
   │
   ▼
Frontend HTML/CSS/JavaScript
   │
   │ POST /api/auth/login
   ▼
AuthController
   │
   ▼
AuthService
   │
   ▼
UsuarioRepositoryPort
   │
   ▼
UsuarioRepository
```

### Flujo de manejo de errores

```text
AuthService
    │
    ▼
AuthenticationException
    │
    ▼
GlobalExceptionHandler
    │
    ▼
Respuesta HTTP
```

### Endpoint

```http
POST /api/auth/login
```

### Cuerpo de la solicitud

```json
{
  "correo": "demo@kasero.local",
  "password": "123456"
}
```

### Respuesta exitosa

```json
{
  "exitoso": true,
  "mensaje": "Inicio de sesión exitoso",
  "usuarioId": 1,
  "nombre": "Usuario Demo",
  "token": "..."
}
```

El `token` se envía después en la cabecera `Authorization: Bearer <token>` para pedir y ver cotizaciones.

### Todos los endpoints

| Método | Ruta | ¿Pide sesión? | Qué hace |
| :----- | :--- | :-----------: | :------- |
| POST | `/api/auth/registro` | No | Crea una cuenta (nombre, correo, contraseña de 8+ caracteres) |
| POST | `/api/auth/login` | No | Inicia sesión y devuelve el token |
| POST | `/api/auth/logout` | Sí | Cierra la sesión |
| GET | `/api/sectores` | No | Sectores del mercado |
| GET | `/api/productos?sector=` | No | Productos, opcionalmente de un sector |
| GET | `/api/comerciantes?q=&sector=&orden=` | No | Busca comerciantes. `orden`: `precio_asc`, `precio_desc`, `calificacion`, `cotizaciones` |
| GET | `/api/comerciantes/{id}` | No | Perfil del puesto con precios |
| GET | `/api/promociones` | No | Promociones con precio ya descontado |
| GET | `/api/noticias` | No | Avisos del mercado |
| GET | `/api/canasta-referencia` | No | Canasta de ejemplo para comparar |
| POST | `/api/comparaciones` | No | Compara una canasta (`items`, `orden`: `asc` o `desc`) |
| POST | `/api/cotizaciones` | Sí | Crea una cotización (`claveSolicitud` evita duplicados) |
| GET | `/api/cotizaciones` | Sí | Historial del usuario, la más nueva primero |

---

## 6. Interfaz Web

La interfaz es una página única (sin librerías externas) que cambia de pantalla con la parte `#` de la dirección:

| Pantalla | Dirección | Requiere ingresar |
| :------- | :-------- | :---------------: |
| Inicio | `#/` | No |
| Buscar y filtrar | `#/buscar` | No |
| Perfil del puesto | `#/comerciante/1` | No |
| Comparar canasta | `#/comparar` | No |
| Cotizar | `#/cotizar/1` | Sí |
| Mis cotizaciones | `#/cotizaciones` | Sí |
| Ingresar / registrarse | `#/acceso` | No |

Cuidados de la interfaz: el texto que viene de la API nunca se inserta como HTML, funciona con teclado, tiene enlace para saltar al contenido, menú inferior en celular y mensajes claros para estados de carga, vacío y error. Los totales se muestran en una "pizarra de precios", como los precios escritos con tiza en un puesto.

El frontend es servido directamente por Spring Boot y se encuentra disponible en:

```text
http://localhost:8080
```

---

## 7. Decisiones Arquitectonicas

Como parte de la mejora del diseño se introdujo una abstracción para desacoplar la lógica de autenticación de la implementación concreta de persistencia.

### Antes

```text
AuthService
     │
     ▼
UsuarioRepository
```

### Después

```text
AuthService
     │
     ▼
UsuarioRepositoryPort
     ▲
     │
UsuarioRepository
```

También se separó el tratamiento de errores HTTP del controlador mediante:

```text
AuthenticationException
        │
        ▼
GlobalExceptionHandler
```

Estas decisiones buscan mejorar principalmente:

* Bajo acoplamiento.
* Modificabilidad.
* Separación de responsabilidades.
* Reutilización.
* Comprobabilidad.

La decisión principal se encuentra documentada en:

```text
docs/adr/ADR-001-abstraccion-repositorio.md
```

---

## 8. Ejecucion del Proyecto

### Requisitos

Se requiere disponer de:

```text
Java 21
Maven 3.10.0
```

Comprobar Java:

```powershell
java -version
```

Comprobar Maven:

```powershell
mvn -version
```

### Construcción del proyecto

Ejecutar desde la raíz de `Kasero_project`:

```powershell
mvn clean package
```

### Ejecución

```powershell
mvn spring-boot:run
```

Al iniciar correctamente, Spring Boot levanta el servidor en:

```text
http://localhost:8080
```

### Pruebas

```powershell
mvn test
```

Hay pruebas unitarias para contraseñas, sesiones, registro/login, búsqueda, comparador y cotizaciones.

> Nota: mientras se escribió esto no se pudo descargar Maven Central desde el entorno de trabajo, así que las pruebas se corrieron con un arnés local propio y la aplicación se vio funcionando con un servidor de prueba. **Falta confirmar `mvn clean package` y `mvn spring-boot:run` en una máquina con internet.**

### Detener la aplicación

En la terminal donde se encuentra ejecutándose:

```text
Ctrl + C
```

---

## 9. Credenciales de Prueba

La versión actual utiliza almacenamiento en memoria para fines académicos (al reiniciar se pierden las cuentas y cotizaciones nuevas). Las contraseñas se guardan con hash PBKDF2, nunca en texto plano.

```text
Correo:
demo@kasero.local

Contraseña:
123456
```

> Estas credenciales corresponden únicamente al usuario de prueba utilizado durante el desarrollo. También se puede crear una cuenta desde la pantalla de registro. La persistencia definitiva en base de datos queda pendiente.

---

## 10. Evaluacion de Calidad y Arquitectura

El proyecto forma parte de una actividad de evaluación arquitectónica que contempla:

### Diagnostico

* Modelado del diseño actual.
* Lista de revisión aplicada a ocho clases.
* Cálculo de métricas de diseño.
* Identificación de problemas arquitectónicos.

### Mejora

* Priorización de cinco hallazgos.
* Refactorización de dos problemas.
* Comparación del diseño antes y después.
* Evaluación mediante un escenario de cambio.

### Documentacion

* ADR de la decisión arquitectónica principal.
* Registro de evidencias.
* Registro de limitaciones.
* Diagramas PlantUML.

---

## 11. Modelo de Clases Evaluado

Las ocho clases seleccionadas para el diagnóstico inicial fueron:

```text
1. AuthController
2. AuthService
3. UsuarioRepository
4. Usuario
5. LoginRequest
6. LoginResponse
7. Chat
8. Mensaje
```

El diseño inicial corresponde a la versión **v0.1**.

La versión refactorizada corresponde a **v0.2** e incorpora:

```text
UsuarioRepositoryPort
AuthenticationException
GlobalExceptionHandler
```

---

## 12. Alcance Actual

### Implementado

* [x] Estructura inicial de Spring Boot.
* [x] Arquitectura por capas.
* [x] Inicio de sesión.
* [x] Interfaz web de autenticación.
* [x] Comunicación frontend-backend.
* [x] Abstracción del repositorio.
* [x] Manejo centralizado de errores.
* [x] Modelo inicial de usuarios.
* [x] Modelo inicial de chats y mensajes.
* [x] ADR de la decisión arquitectónica.
* [x] Registro de usuarios y hash seguro de contraseñas.
* [x] Búsqueda, filtro por sector y orden de resultados.
* [x] Perfil del puesto con productos y precios.
* [x] Comparador de canasta.
* [x] Cotización e historial de cotizaciones.
* [x] Interfaz web completa para lo anterior.
* [x] Pruebas unitarias de los servicios.

### Pendiente

* [ ] Persistencia mediante base de datos (PostgreSQL).
* [ ] Datos reales desde Datos Abiertos Perú.
* [ ] Confirmación de la cotización por el comerciante.
* [ ] Elegir y guardar la forma de entrega en el pedido.
* [ ] Chat e historial de mensajes.
* [ ] Reservas y seguimiento de pedidos.
* [ ] Calificaciones y reseñas.
* [ ] Despliegue en la nube.

---

## 13. Evidencias del Laboratorio

La evaluación arquitectónica cuenta con las siguientes evidencias principales:

| Evidencia | Descripción                             |
| :-------- | :-------------------------------------- |
| **E-08**  | Arquitectura N Capas del proyecto       |
| **E-09**  | Diagrama de clases del diseño v0.1      |
| **E-10**  | Diagrama de componentes del diseño v0.2 |
| **E-11**  | Abstracción del repositorio             |
| **E-12**  | Manejo centralizado de errores          |
| **E-13**  | Matriz de hallazgos                     |
| **E-14**  | Métricas de diseño                      |
| **E-15**  | ADR-001                                 |
| **E-16**  | Evidencia de compilación y ejecución    |
| **E-17**  | Evidencia funcional del login           |

---

## 14. Estado del Proyecto

**Versión actual:** `v0.3` (avance del Hito 1)
**Estado:** En desarrollo
**Tipo:** Proyecto académico
**Módulos implementados:** Autenticación, catálogo y búsqueda, comparador, cotizaciones

---
