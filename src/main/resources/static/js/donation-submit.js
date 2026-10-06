document.addEventListener("DOMContentLoaded", function () {

    const donationType =
        document.querySelector('select[name="donationType"]');

    const cashSection =
        document.getElementById("cashSection");

    const goodsSection =
        document.getElementById("goodsSection");

    const form =
        document.querySelector("form");


    /* ========================================
       DONATION TYPE
       ======================================== */

    function updateDonationType() {

        if (!donationType) {
            return;
        }

        const type = donationType.value;

        if (type === "CASH") {

            if (cashSection) {
                cashSection.style.display = "block";
            }

            if (goodsSection) {
                goodsSection.style.display = "none";
            }

        } else if (type === "GOODS") {

            if (cashSection) {
                cashSection.style.display = "none";
            }

            if (goodsSection) {
                goodsSection.style.display = "block";
            }

        } else {

            if (cashSection) {
                cashSection.style.display = "none";
            }

            if (goodsSection) {
                goodsSection.style.display = "none";
            }
        }
    }


    if (donationType) {

        donationType.addEventListener(
            "change",
            updateDonationType
        );

        updateDonationType();
    }


    /* ========================================
       NORMAL RELIEF ITEMS
       ======================================== */

    const itemRows =
        document.querySelectorAll(".donation-item-row");


    itemRows.forEach(function (row) {

        const checkbox =
            row.querySelector(
                'input[type="checkbox"][name="selectedItems"]'
            );

        const quantity =
            row.querySelector(
                'input[name="itemQuantities"]'
            );

        const unit =
            row.querySelector(
                'select[name="itemUnits"]'
            );


        if (!checkbox) {
            return;
        }


        function updateItemFields() {

            if (checkbox.checked) {

                if (quantity) {
                    quantity.disabled = false;
                    quantity.required = true;
                }

                if (unit) {
                    unit.disabled = false;
                    unit.required = true;
                }

            } else {

                if (quantity) {
                    quantity.disabled = true;
                    quantity.required = false;
                    quantity.value = "";
                }

                if (unit) {
                    unit.disabled = true;
                    unit.required = false;
                }
            }
        }


        checkbox.addEventListener(
            "change",
            updateItemFields
        );


        updateItemFields();
    });


    /* ========================================
       FORM SUBMIT
       ======================================== */

    if (form) {

        form.addEventListener(
            "submit",
            function (event) {

                const type =
                    donationType
                        ? donationType.value
                        : "";


                /* ====================================
                   CASH DONATION
                   ==================================== */

                if (type === "CASH") {

                    const cashAmount =
                        document.querySelector(
                            'input[name="cashAmount"]'
                        );

                    if (
                        !cashAmount ||
                        !cashAmount.value ||
                        Number(cashAmount.value) <= 0
                    ) {

                        event.preventDefault();

                        alert(
                            "Please enter a valid cash amount."
                        );

                        return;
                    }
                }


                /* ====================================
                   GOODS DONATION
                   ==================================== */

                if (type === "GOODS") {


                    /* --------------------------------
                       CHECK NORMAL ITEMS
                       -------------------------------- */

                    const checkedItems =
                        document.querySelectorAll(
                            'input[name="selectedItems"]:checked'
                        );


                    /* --------------------------------
                       CHECK ADDITIONAL ITEM
                       -------------------------------- */

                    const additionalItemName =
                        document.querySelector(
                            'input[name="additionalItemName"]'
                        );

                    const additionalItemQuantity =
                        document.querySelector(
                            'input[name="additionalItemQuantity"]'
                        );

                    const additionalItemUnit =
                        document.querySelector(
                            'select[name="additionalItemUnit"]'
                        );


                    const hasAdditionalItem =
                        additionalItemName &&
                        additionalItemName.value.trim() !== "";


                    const hasNormalItems =
                        checkedItems.length > 0;


                    /* --------------------------------
                       AT LEAST ONE ITEM
                       -------------------------------- */

                    if (
                        !hasNormalItems &&
                        !hasAdditionalItem
                    ) {

                        event.preventDefault();

                        alert(
                            "Please select at least one relief item or add an additional donation item."
                        );

                        return;
                    }


                    /* --------------------------------
                       NORMAL ITEM VALIDATION
                       -------------------------------- */

                    if (hasNormalItems) {

                        let invalidNormalItem = false;


                        checkedItems.forEach(
                            function (checkbox) {

                                const row =
                                    checkbox.closest(
                                        ".donation-item-row"
                                    );

                                if (!row) {
                                    return;
                                }


                                const quantity =
                                    row.querySelector(
                                        'input[name="itemQuantities"]'
                                    );

                                const unit =
                                    row.querySelector(
                                        'select[name="itemUnits"]'
                                    );


                                if (
                                    !quantity ||
                                    !quantity.value ||
                                    Number(quantity.value) <= 0
                                ) {

                                    invalidNormalItem = true;
                                }


                                if (
                                    !unit ||
                                    !unit.value
                                ) {

                                    invalidNormalItem = true;
                                }
                            }
                        );


                        if (invalidNormalItem) {

                            event.preventDefault();

                            alert(
                                "Please enter a valid quantity and unit for every selected item."
                            );

                            return;
                        }
                    }


                    /* --------------------------------
                       ADDITIONAL ITEM VALIDATION
                       -------------------------------- */

                    if (hasAdditionalItem) {

                        if (
                            !additionalItemQuantity ||
                            !additionalItemQuantity.value ||
                            Number(
                                additionalItemQuantity.value
                            ) <= 0
                        ) {

                            event.preventDefault();

                            alert(
                                "Please enter a valid quantity for the additional item."
                            );

                            return;
                        }


                        if (
                            !additionalItemUnit ||
                            !additionalItemUnit.value
                        ) {

                            event.preventDefault();

                            alert(
                                "Please select a unit for the additional item."
                            );

                            return;
                        }
                    }
                }
            }
        );
    }

});