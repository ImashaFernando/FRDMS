document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("editExpenseForm");
    const amountInput = document.getElementById("amount");
    const dateInput = document.getElementById("expenseDate");

    if (dateInput) {
        const today = new Date().toISOString().split("T")[0];
        dateInput.max = today;
    }

    if (amountInput) {
        amountInput.addEventListener("input", function () {

            if (parseFloat(this.value) < 0) {
                this.value = "";
            }

        });
    }

    if (form) {

        form.addEventListener("submit", function (event) {

            const amount = parseFloat(amountInput.value);

            if (isNaN(amount) || amount <= 0) {

                event.preventDefault();

                alert(
                    "Please enter a valid expense amount greater than 0."
                );

                amountInput.focus();
            }

        });

    }

});