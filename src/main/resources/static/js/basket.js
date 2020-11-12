async function updateBasket()
{
    console.log("%cBasket is gonna check itself out now", "color:mediumpurple");
    const basketItems = await fetchBasketItems();
    console.dir(JSON.parse(basketItems));
    console.log("We have " + JSON.parse(basketItems).length + " items in the basket");
    let basketItemCount = JSON.parse(basketItems).length;
    const basket = document.getElementById("basketNavbarText");
    basket.innerText = ' (' + basketItemCount + ')';
}

async function fetchBasketItems()
{
    const response = await fetch(location.origin + "/getTreeOrders?status=active");
    console.log("%cFetching basket items has finished, status: " + response.status, "color:mediumpurple");
    return await response.text();
}

const sizes = new Map([
    ["20x20 cm", 495],
    ["25x25 cm", 695],
    ["30x30 cm", 995]
]);

async function loadOrders()
{
    const defaultRow = document.getElementsByClassName("orderRow")[0];
    const orderTable = document.getElementById("orderTable");
    const ordersJson = await fetchBasketItems();

    if (ordersJson === '[]')
    {
        return;
    }
    let orders = JSON.parse(ordersJson);
    let totalPrice = 0;
    let totalSaved = 0;
    let totalAmount = 0;
    for (let i = 0; i < orders.length; i++)
    {
        let orderRow = defaultRow.cloneNode(true);
        orderRow.style.display = 'table-row';
        let order = orders[i];

        let itemPrice = sizes.get(order['size']);
        let amount = parseInt(order['amount']);
        totalAmount += amount;
        let itemTotal = (itemPrice * amount);
        totalPrice += itemTotal;

        orderRow.getElementsByClassName("orderAmount")[0].innerText = order['amount'];
        orderRow.getElementsByClassName("orderProduct")[0].innerText = 'Family Tree - ' + order['size'];
        orderRow.getElementsByClassName("orderTotal")[0].innerText = itemTotal + 'kr';

        let newRow = orderTable.insertRow(2);
        newRow.innerHTML = orderRow.innerHTML;
    }
    if (totalAmount > 3)
    {
        totalSaved = totalPrice * 0.25;
        totalPrice *= 0.75;
    }

    document.getElementById("totalPrice").innerText = totalPrice + 'kr';
    document.getElementById("totalDiscount").innerText = '-' + totalSaved + 'kr';
    document.getElementById("totalTax").innerText = (totalPrice * 0.2) + 'kr';
}

async function validateBasket()
{
    console.log("Validating Delivery information")
    const orders = await fetchBasketItems();
    if (JSON.parse(orders).length === 0)
    {
        showBasketPopup("There are no orders to purchase", true)
        return false;
    }
    const inputName = document.getElementById('inputName');
    const inputPhoneNumber = document.getElementById('inputPhoneNumber');
    const inputEmail = document.getElementById('inputEmail');
    const inputStreetAddress = document.getElementById('inputStreetAddress');
    const inputCity = document.getElementById('inputCity');
    const inputPostcode = document.getElementById('inputPostcode');

    if (inputName.value === '')
    {
        showBasketPopup("The name is required", true);
        return false;
    }
    if (inputPhoneNumber.value === '')
    {
        showBasketPopup("The phone number is required", true);
        return false;
    }
    if (inputEmail.value === '')
    {
        showBasketPopup("The email is required", true);
        return false;
    }
    if (inputStreetAddress.value === '')
    {
        showBasketPopup("The street address is required", true);
        return false;
    }
    if (inputCity.value === '')
    {
        showBasketPopup("The city is required", true);
        return false;
    }
    if (inputPostcode.value === '')
    {
        showBasketPopup("The postcode is required", true);
        return false;
    }

    const termsCheckbox = document.getElementById('termsAndConditionsCheckbox');

    if (termsCheckbox.checked === false)
    {

        showBasketPopup("You have to accept our terms and conditions", true)
        return false;
    }
    console.log("The basket information is correct")
    return true;
}

// pretty much a copy of the basket.js#showPopup but only uses the normal popup
function showBasketPopup(text, errorPopup)
{
    let popupContainer = document.getElementById("popupContainer");
    let popup = document.getElementById("popup");
    let popupText = document.getElementById("popupText");

    if (errorPopup)
    {
        popup.style.backgroundColor = 'var(--popupErrorColor)';
    } else
    {
        popup.style.backgroundColor = 'var(--popupColor)';
    }

    popupText.innerText = text;

    popupContainer.style.display = 'block';
    popupContainer.style.animation = 'fadeIn 1s';
    setTimeout(() =>
    {
        popupContainer.style.animation = 'fadeOut 1s';
    }, 3000);

    setTimeout(() =>
    {
        popupContainer.style.display = 'none'
    }, 4000);
}

async function goToPayment()
{
    const isValid = await validateBasket();
    if (!isValid)
    {
        return;
    }

    const inputName = document.getElementById("inputName").value;
    const inputPhoneNumber = document.getElementById("inputPhoneNumber").value;
    const inputEmail = document.getElementById("inputEmail").value;
    const inputStreetAddress = document.getElementById("inputStreetAddress").value;
    const inputCity = document.getElementById("inputCity").value;
    const inputPostcode = document.getElementById("inputPostcode").value;
    const inputCountry = document.getElementById("inputCountry").value;
    const inputDiscountCode = document.getElementById("inputDiscountCode").value;

    let paymentParameters = {
        name: inputName,
        phoneNumber: inputPhoneNumber,
        email: inputEmail,
        streetAddress: inputStreetAddress,
        city: inputCity,
        postcode: inputPostcode,
        country: inputCountry,
        discount: inputDiscountCode
    };

    console.log("Going to payment or smth");
    fetch(location.origin + "/startPayment",
        {
            method: "POST",
            headers: {'Content-type': 'application/json'},
            body: JSON.stringify(paymentParameters)
        }
    ).then(response =>
    {
        console.log("Starting a new payment has finished, status: " + response.status);
        if (response.status === 200)
        {
            response.text().then(data =>
            {
                console.log("Returned data: " + data)
                const paymentUrl = JSON.parse(data)['url'];
                window.open(paymentUrl);
                // Redirect to the orders tab
                window.location.replace(window.origin + "/account/orders");
            })
        } else
        {
            response.text().then(data =>
            {
                console.log("Returned data: " + data)
                showBasketPopup(data, true)
            })
        }
    })
}

let discountApplied = false;

async function applyDiscount()
{
    let inputDiscountCode = document.getElementById("inputDiscountCode").value;
    console.log("Verifying the discount code");
    if (inputDiscountCode === undefined || inputDiscountCode === "")
    {
        console.log("The discount code cannot be empty",);
        showBasketPopup("The discount code cannot be empty", true);
        return false;
    }
    const result = await validateDiscountCode(inputDiscountCode);

    if (result.status !== 200)
    {
        console.log("The provided discount code is not valid");
        showBasketPopup("The provided discount code is not valid", true);
        return false;
    } else
    {
        console.log("Found a valid discount code");
        result.text().then(body =>
            {
                const discountCode = JSON.parse(body);
                const isActive = discountCode['active'];
                if (!isActive)
                {

                    console.log("The provided discount code is no longer active");
                    showBasketPopup("The provided discount code is no longer active", true);
                    return false;
                }

                showBasketPopup("Code is okay, applying the discount", false)
                const type = discountCode['discountType'];
                const amount = parseInt(discountCode['discountAmount']);

                if (discountApplied)
                {
                    console.log("Discount has already been applied");
                    return true;
                }

                const totalDiscountRow = document.getElementById("totalDiscount");
                const totalPriceRow = document.getElementById("totalPrice");

                // Obtain actual price and discount values pre-discount
                let currentTotalPrice = parseInt(totalPriceRow.innerText.replace('kr', ''));
                let currentDiscount = parseInt(totalDiscountRow.innerText.replace('-', '')
                    .replace('kr', ''));

                if (type === "minus")
                {
                    console.log("Applying -" + amount + "kr discount");
                    // Add the discount to the totalDiscount row
                    let totalDiscount = currentDiscount + amount;
                    totalDiscountRow.innerText = "-" + totalDiscount + "kr";

                    // Update the totalPrice row
                    let totalPrice = currentTotalPrice - amount;
                    if (totalPrice < 0) totalPrice = 0;
                    totalPriceRow.innerText = totalPrice + "kr";
                } else if (type === "percent")
                {
                    // Apply the %-based discount
                    console.log("Applying " + amount + "% discount");
                    let percent = (100 - parseInt(amount)) / 100;
                    console.log("percent: " + percent);
                    // Update the price row
                    let totalPrice = Math.floor(currentTotalPrice * percent);
                    totalPriceRow.innerText = totalPrice + "kr";

                    // Update the discount row
                    let totalDiscount = currentDiscount + (currentTotalPrice - totalPrice);
                    totalDiscountRow.innerText = "-" + totalDiscount + "kr";
                }

                discountApplied = true;

                return true;
            }
        )
    }

}

async function validateDiscountCode(code)
{
    const response = await fetch(location.origin + "/getDiscountCode?code=" + code);
    console.log("%cFetching discount code has finished, status: " + response.status, "color:mediumpurple");
    return response;
}