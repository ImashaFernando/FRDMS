document.addEventListener("DOMContentLoaded", function () {

    const donationType = document.getElementById("donationType");

    const cashSection = document.getElementById("cashSection");
    const goodsSection = document.getElementById("goodsSection");

    const cashAmount = document.getElementById("cashAmount");

    const goodsDescription = document.getElementById("goodsDescription");
    const goodsQuantity = document.getElementById("goodsQuantity");


    function updateDonationFields() {

        const selectedType = donationType.value;


        if (selectedType === "CASH") {

            // Show cash fields
            cashSection.style.display = "block";

            // Hide goods fields
            goodsSection.style.display = "none";

            // Clear goods values
            goodsDescription.value = "";
            goodsQuantity.value = "";

            // Required only for cash
            cashAmount.required = true;

            goodsDescription.required = false;
            goodsQuantity.required = false;

        }

        else if (selectedType === "GOODS") {

            // Hide cash fields
            cashSection.style.display = "none";

            // Show goods fields
            goodsSection.style.display = "block";

            // Clear cash value
            cashAmount.value = "";

            // Required only for goods
            cashAmount.required = false;

            goodsDescription.required = true;
            goodsQuantity.required = true;

        }

        else {

            // No donation type selected
            cashSection.style.display = "none";
            goodsSection.style.display = "none";

            // Clear all donation-specific values
            cashAmount.value = "";
            goodsDescription.value = "";
            goodsQuantity.value = "";

            cashAmount.required = false;
            goodsDescription.required = false;
            goodsQuantity.required = false;
        }
    }


    // Run when donation type changes
    donationType.addEventListener("change", updateDonationFields);


    // Run once when page loads
    updateDonationFields();

});