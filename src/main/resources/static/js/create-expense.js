document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("expenseForm");

    const dateInput = document.getElementById("expenseDate");
    const amountInput = document.getElementById("amount");
    const descriptionInput = document.getElementById("description");
    const categoryInput = document.getElementById("category");


    // =========================================================
    // PREVENT FUTURE DATE
    // =========================================================

    if (dateInput) {
        const today = new Date().toISOString().split("T")[0];
        dateInput.max = today;
    }


    // =========================================================
    // AMOUNT VALIDATION WHILE TYPING
    // =========================================================

    if (amountInput) {

        amountInput.addEventListener("input", function () {

            if (parseFloat(this.value) < 0) {
                this.value = "";
            }

            clearError(this);
        });
    }


    // =========================================================
    // CLEAR ERROR WHEN USER ENTERS DATA
    // =========================================================

    if (dateInput) {
        dateInput.addEventListener("change", function () {
            clearError(this);
        });
    }

    if (descriptionInput) {
        descriptionInput.addEventListener("input", function () {
            clearError(this);
        });
    }

    if (categoryInput) {
        categoryInput.addEventListener("change", function () {
            clearError(this);
        });
    }


    // =========================================================
    // FORM SUBMIT VALIDATION
    // =========================================================

    if (form) {

        form.addEventListener("submit", function (event) {

            let valid = true;

            clearAllErrors();


            // -------------------------------------------------
            // EXPENSE DATE
            // -------------------------------------------------

            if (!dateInput.value.trim()) {

                showError(
                    dateInput,
                    "Expense date is required"
                );

                valid = false;
            }


            // -------------------------------------------------
            // AMOUNT
            // -------------------------------------------------

            const amount = parseFloat(amountInput.value);

            if (
                amountInput.value.trim() === "" ||
                isNaN(amount) ||
                amount <= 0
            ) {

                showError(
                    amountInput,
                    "Amount must be greater than 0"
                );

                valid = false;
            }


            // -------------------------------------------------
            // DESCRIPTION
            // -------------------------------------------------

            if (!descriptionInput.value.trim()) {

                showError(
                    descriptionInput,
                    "Description is required"
                );

                valid = false;
            }


            // -------------------------------------------------
            // CATEGORY
            // -------------------------------------------------

            if (!categoryInput.value.trim()) {

                showError(
                    categoryInput,
                    "Category is required"
                );

                valid = false;
            }


            // -------------------------------------------------
            // STOP FORM SUBMISSION
            // -------------------------------------------------

            if (!valid) {
                event.preventDefault();
            }

        });
    }


    // =========================================================
    // SHOW ERROR
    // =========================================================

    function showError(input, message) {

        input.classList.add("input-error");

        const errorElement =
            document.getElementById(input.id + "Error");

        if (errorElement) {

            errorElement.textContent = message;
            errorElement.style.display = "block";
        }
    }


    // =========================================================
    // CLEAR ERROR
    // =========================================================

    function clearError(input) {

        if (!input) {
            return;
        }

        input.classList.remove("input-error");

        const errorElement =
            document.getElementById(input.id + "Error");

        if (errorElement) {
            errorElement.style.display = "none";
        }
    }


    // =========================================================
    // CLEAR ALL ERRORS
    // =========================================================

    function clearAllErrors() {

        clearError(dateInput);
        clearError(amountInput);
        clearError(descriptionInput);
        clearError(categoryInput);
    }

});