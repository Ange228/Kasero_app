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

La implementación actual se encuentra enfocada en el **módulo de autenticación**, utilizado como base para el análisis, diagnóstico y mejora de la arquitectura de software.

Actualmente el sistema incluye:

* **Autenticación de usuarios:** inicio de sesión mediante correo y contraseña.
* **Interfaz web:** formulario de login desarrollado con HTML, CSS y JavaScript.
* **API REST:** endpoint de autenticación implementado mediante Spring Boot.
* **Separación por capas:** presentación, aplicación/negocio, acceso a datos y dominio.
* **Abstracción de persistencia:** uso de `UsuarioRepositoryPort`.
* **Manejo centralizado de errores:** `AuthenticationException` y `GlobalExceptionHandler`.
* **Modelo inicial de dominio:** `Usuario`, `Chat` y `Mensaje`.
* **Documentación arquitectónica:** diagramas PlantUML y ADR.

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
│       │               ├── controller/
│       │               │   └── AuthController.java
│       │               │
│       │               ├── dto/
│       │               │   ├── LoginRequest.java
│       │               │   └── LoginResponse.java
│       │               │
│       │               ├── exception/
│       │               │   ├── AuthenticationException.java
│       │               │   └── GlobalExceptionHandler.java
│       │               │
│       │               ├── model/
│       │               │   ├── Usuario.java
│       │               │   ├── Chat.java
│       │               │   └── Mensaje.java
│       │               │
│       │               ├── repository/
│       │               │   ├── UsuarioRepository.java
│       │               │   └── UsuarioRepositoryPort.java
│       │               │
│       │               └── service/
│       │                   └── AuthService.java
│       │
│       └── resources/
│           └── static/
│               ├── index.html
│               ├── styles.css
│               └── app.js
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
  "nombre": "Usuario Demo"
}
```

---

## 6. Interfaz Web

La aplicación dispone de una interfaz web inicial para el módulo de autenticación.

La interfaz se encuentra implementada mediante:

```text
src/main/resources/static/
├── index.html
├── styles.css
└── app.js
```

El frontend es servido directamente por Spring Boot y se encuentra disponible en:

```text
http://localhost:8080
```

El formulario de login consume el endpoint:

```text
POST /api/auth/login
```

permitiendo validar las credenciales y mostrar mensajes de éxito o error.

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

### Detener la aplicación

En la terminal donde se encuentra ejecutándose:

```text
Ctrl + C
```

---

## 9. Credenciales de Prueba

La versión actual utiliza almacenamiento en memoria para fines académicos.

```text
Correo:
demo@kasero.local

Contraseña:
123456
```

> Estas credenciales corresponden únicamente al usuario de prueba utilizado durante el desarrollo. La persistencia definitiva y el almacenamiento seguro de contraseñas quedan pendientes para una versión posterior.

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

### Pendiente

* [ ] Registro de usuarios.
* [ ] Persistencia mediante base de datos.
* [ ] Hash seguro de contraseñas.
* [ ] Chat e historial de mensajes.
* [ ] Cotización.
* [ ] Reservas.
* [ ] Búsqueda y filtros por sector.
* [ ] Calificaciones y reseñas.
* [ ] Seguimiento de pedidos.
* [ ] Información de puestos y productos.

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

**Versión actual:** `v0.2`
**Estado:** En desarrollo
**Tipo:** Proyecto académico
**Módulo implementado:** Autenticación

---
