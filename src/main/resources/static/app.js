const loginForm = document.getElementById("loginForm");
const loginButton = document.getElementById("loginButton");
const message = document.getElementById("message");
const userInfo = document.getElementById("userInfo");
const userName = document.getElementById("userName");

function mostrarMensaje(texto, tipo) {
    message.textContent = texto;
    message.className = `message ${tipo}`;
}

loginForm.addEventListener("submit", async function (event) {

    event.preventDefault();

    const correo = document.getElementById("correo").value;
    const password = document.getElementById("password").value;

    loginButton.disabled = true;
    loginButton.textContent = "Ingresando...";

    message.className = "message hidden";
    userInfo.classList.add("hidden");

    try {

        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                correo: correo,
                password: password
            })
        });

        const data = await response.json();

        if (response.ok && data.exitoso) {

            mostrarMensaje(
                "Inicio de sesión exitoso",
                "success"
            );

            userName.textContent = `Bienvenida, ${data.nombre}`;
            userInfo.classList.remove("hidden");

        } else {

            mostrarMensaje(
                data.mensaje || "No fue posible iniciar sesión.",
                "error"
            );
        }

    } catch (error) {

        mostrarMensaje(
            "No se pudo conectar con el servidor.",
            "error"
        );

        console.error(error);

    } finally {

        loginButton.disabled = false;
        loginButton.textContent = "Ingresar";
    }
});