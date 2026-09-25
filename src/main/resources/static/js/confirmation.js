let confirmAction = null;

function showConfirmation(options) {

    const overlay = document.getElementById("confirmationModal");

    const icon = document.getElementById("confirmIcon");
    const title = document.getElementById("confirmTitle");
    const message = document.getElementById("confirmMessage");
    const okButton = document.getElementById("confirmOk");

    icon.innerHTML = options.icon || "⚠";

    title.textContent =
        options.title || "Confirmation";

    message.textContent =
        options.message || "Are you sure?";

    okButton.textContent =
        options.confirmText || "Confirm";

    okButton.className =
        "confirm-btn " +
        (options.type === "danger"
            ? "confirm-danger"
            : options.type === "success"
                ? "confirm-success"
                : "confirm-ok");

    confirmAction = options.action;

    overlay.classList.add("show");
}
function confirmBudgetAction(formId, actionType) {

    if (actionType === "approve") {

        showConfirmation({
            title: "Approve Budget",
            message: "Are you sure you want to approve this budget requirement?",
            confirmText: "Approve",
            type: "success",
            icon: "✓",

            action: function () {
                document.getElementById(formId).submit();
            }
        });

    } else if (actionType === "reject") {

        showConfirmation({
            title: "Reject Budget",
            message: "Are you sure you want to reject this budget requirement?",
            confirmText: "Reject",
            type: "danger",
            icon: "!",

            action: function () {
                document.getElementById(formId).submit();
            }
        });

    }
}

function closeConfirmation() {

    const overlay =
        document.getElementById("confirmationModal");

    overlay.classList.remove("show");

    confirmAction = null;
}

document.addEventListener("DOMContentLoaded", function () {

    const okButton =
        document.getElementById("confirmOk");

    const cancelButton =
        document.getElementById("confirmCancel");

    const overlay =
        document.getElementById("confirmationModal");

    okButton.addEventListener("click", function () {

        const action = confirmAction;

        closeConfirmation();

        if (action) {
            action();
        }
    });

    cancelButton.addEventListener(
        "click",
        closeConfirmation
    );

    overlay.addEventListener("click", function (event) {

        if (event.target === overlay) {
            closeConfirmation();
        }
    });

    document.addEventListener("keydown", function (event) {

        if (event.key === "Escape") {
            closeConfirmation();
        }
    });
});
function confirmDeleteExpense(id) {

    showConfirmation({
        title: "Delete Expense",
        message: "Are you sure you want to delete this expense? This action cannot be undone.",
        confirmText: "Delete",
        type: "danger",
        icon: "!",
        action: function () {

            document
                .getElementById("deleteExpenseForm-" + id)
                .submit();

        }
    });
}