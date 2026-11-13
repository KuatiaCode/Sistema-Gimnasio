document.addEventListener("DOMContentLoaded", () => {
    const formulario = document.querySelector("form[data-nueva]");
    const precio = document.getElementById("precioAPagar");
    const planes = document.querySelectorAll(".plan-membresia");

    if (!formulario || !precio || planes.length === 0) {
        return;
    }

    const calcularTotal = () => {
        let total = 0;
        planes.forEach((plan) => {
            if (plan.checked) {
                total += Number.parseInt(plan.dataset.precio || "0", 10);
            }
        });
        precio.value = total;
    };

    planes.forEach((plan) => plan.addEventListener("change", calcularTotal));

    if (formulario.dataset.nueva === "true") {
        calcularTotal();
    }
});
