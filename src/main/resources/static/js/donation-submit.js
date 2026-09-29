document.addEventListener("DOMContentLoaded", function () {

    const donationType = document.getElementById("donationType");

    const cashSection = document.getElementById("cashSection");
    const goodsSection = document.getElementById("goodsSection");

    const cashAmount = document.getElementById("cashAmount");
    const goodsDescription = document.getElementById("goodsDescription");
    const goodsQuantity = document.getElementById("goodsQuantity");


    function updateDonationFields() {

        const selectedType = donationType.value;

        // ==============================
        // CASH DONATION
        // ==============================

        if (selectedType === "CASH") {

            cashSection.style.display = "block";
            goodsSection.style.display = "none";

            // Clear goods fields
            goodsDescription.value = "";
            goodsQuantity.value = "";

        }

            // ==============================
            // GOODS DONATION
        // ==============================

        else if (selectedType === "GOODS") {

            cashSection.style.display = "none";
            goodsSection.style.display = "block";

            // Clear cash field
            cashAmount.value = "";

        }

            // ==============================
            // NO TYPE SELECTED
        // ==============================

        else {

            cashSection.style.display = "none";
            goodsSection.style.display = "none";

            cashAmount.value = "";
            goodsDescription.value = "";
            goodsQuantity.value = "";
        }
    }


    // When donation type changes
    donationType.addEventListener(
        "change",
        updateDonationFields
    );


    // Run when page loads
    updateDonationFields();

});