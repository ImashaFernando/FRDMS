document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("expenseForm");
    const dateInput = document.getElementById("expenseDate");
    const amountInput = document.getElementById("amount");

    // Prevent selecting a future date
    if (dateInput) {
        const today = new Date().toISOString().split("T")[0];
        dateInput.max = today;
    }

    // Prevent invalid amount
    if (amountInput) {
        amountInput.addEventListener("input", function () {

            if (parseFloat(this.value) < 0) {
                this.value = "";
            }
        });
    }

    // Client-side validation
    if (form) {
        form.addEventListener("submit", function (event) {

            const amount = parseFloat(amountInput.value);

            if (isNaN(amount) || amount <= 0) {
                event.preventDefault();
                alert("Please enter a valid expense amount greater than 0.");
                amountInput.focus();
                return;
            }

            if (!dateInput.value) {
                event.preventDefault();
                alert("Please select the expense date.");
                dateInput.focus();
            }
        });
    }

});