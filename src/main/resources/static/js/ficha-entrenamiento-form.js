document.addEventListener("DOMContentLoaded", () => {
    const formulario = document.getElementById("form-ficha");
    const cliente = document.getElementById("id-cliente");
    const membresia = document.getElementById("id-membresia");
    const aviso = document.getElementById("aviso-membresia");
    const sinDetalle = document.getElementById("sin-detalle");
    const detalles = document.querySelectorAll(".detalle-membresia");
    const botonGuardar = formulario ? formulario.querySelector("button[type='submit']") : null;

    if (!formulario || !cliente || !membresia) {
        return;
    }

    const esNueva = formulario.dataset.nueva === "true";

    const actualizarDetalle = () => {
        const seleccionada = membresia.value;
        let encontrada = false;

        detalles.forEach((detalle) => {
            const coincide = detalle.dataset.membresia === seleccionada;
            detalle.classList.toggle("d-none", !coincide);
            encontrada = encontrada || coincide;
        });

        if (sinDetalle) {
            sinDetalle.classList.toggle("d-none", encontrada);
        }
    };

    const filtrarMembresias = (limpiarSeleccion) => {
        const clienteId = cliente.value;
        let visibles = 0;
        let opcionActiva = null;

        Array.from(membresia.options).forEach((opcion) => {
            if (!opcion.value) {
                return;
            }
            const mismoCliente = opcion.dataset.cliente === clienteId;
            const activa = opcion.dataset.estado === "1";
            const visible = Boolean(clienteId) && mismoCliente && (!esNueva || activa);
            opcion.hidden = !visible;
            opcion.disabled = !visible;
            if (visible) {
                visibles += 1;
                if (activa && !opcionActiva) {
                    opcionActiva = opcion;
                }
            }
        });

        if (limpiarSeleccion) {
            membresia.value = "";
        }

        if (esNueva && visibles === 1 && opcionActiva) {
            membresia.value = opcionActiva.value;
        }

        const sinMembresiaActiva = Boolean(clienteId) && esNueva && visibles === 0;
        if (aviso) {
            aviso.classList.toggle("d-none", !sinMembresiaActiva);
        }
        if (botonGuardar && esNueva) {
            botonGuardar.disabled = !clienteId || sinMembresiaActiva;
        }

        actualizarDetalle();
    };

    cliente.addEventListener("change", () => filtrarMembresias(true));
    membresia.addEventListener("change", actualizarDetalle);
    filtrarMembresias(false);
});
