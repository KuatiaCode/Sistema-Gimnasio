document.addEventListener("DOMContentLoaded", () => {
    const cliente = document.getElementById("id-cliente");
    const membresia = document.getElementById("id-membresia");
    const fechaVencimiento = document.getElementById("fechaVencimiento");

    if (!cliente || !membresia || !fechaVencimiento) {
        return;
    }

    const filtrarMembresias = (limpiarSeleccion) => {
        const clienteId = cliente.value;
        let visibles = 0;

        Array.from(membresia.options).forEach((opcion) => {
            if (!opcion.value) {
                return;
            }
            const visible = Boolean(clienteId) && opcion.dataset.cliente === clienteId;
            opcion.hidden = !visible;
            opcion.disabled = !visible;
            if (visible) {
                visibles += 1;
            }
        });

        if (limpiarSeleccion) {
            membresia.value = "";
            fechaVencimiento.value = "";
        }

        if (visibles === 1 && !membresia.value) {
            membresia.value = Array.from(membresia.options)
                    .find((opcion) => opcion.value && !opcion.disabled).value;
        }

        actualizarVencimiento();
    };

    const actualizarVencimiento = () => {
        const seleccionada = membresia.options[membresia.selectedIndex];
        fechaVencimiento.value = seleccionada && seleccionada.dataset.vencimiento
                ? seleccionada.dataset.vencimiento
                : "";
    };

    cliente.addEventListener("change", () => filtrarMembresias(true));
    membresia.addEventListener("change", actualizarVencimiento);
    filtrarMembresias(false);
});
