document.addEventListener("DOMContentLoaded", function () {

    const loginForm = document.getElementById("loginForm");

    if (!loginForm) {
        return;
    }

    loginForm.addEventListener("submit", function (event) {

        let valid = true;

        const username = document.getElementById("username");
        const password = document.getElementById("password");

        const usernameError = document.getElementById("usernameError");
        const passwordError = document.getElementById("passwordError");


        // Reset previous errors
        usernameError.style.display = "none";
        passwordError.style.display = "none";

        username.parentElement.classList.remove("input-error");
        password.parentElement.classList.remove("input-error");


        // Username validation
        if (username.value.trim() === "") {

            usernameError.style.display = "block";
            username.parentElement.classList.add("input-error");

            valid = false;
        }


        // Password validation
        if (password.value.trim() === "") {

            passwordError.style.display = "block";
            password.parentElement.classList.add("input-error");

            valid = false;
        }


        // Prevent submit if validation fails
        if (!valid) {
            event.preventDefault();
        }

    });

});