async function updateBasket()
{
    console.log("%cBasket is gonna check itself out now", "color:mediumpurple");
    const basketItems = await fetchBasketItems();
    console.dir(JSON.parse(basketItems));
    console.log("We have " + JSON.parse(basketItems).length + " items in the basket");
    let basketItemCount = JSON.parse(basketItems).length;
    const basket = document.getElementById("basketNavbarText");
    basket.innerText = ' (' + basketItemCount + ') Items';
}

async function fetchBasketItems()
{
    const response = await fetch(location.origin + "/getTreeOrders");
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
    for (let i = 0; i < orders.length; i++)
    {
        let orderRow = defaultRow.cloneNode(true);
        orderRow.style.display = 'table-row'
        let order = orders[i];
        orderRow.getElementsByClassName("orderAmount")[0].innerText = order['amount'];
        orderRow.getElementsByClassName("orderProduct")[0].innerText = 'Family Tree - ' + order['size'];
        let itemPrice = sizes.get(order['size']);
        let itemTotal = (itemPrice * parseInt(order['amount']));
        let itemSaved = 500;
        orderRow.getElementsByClassName("orderPrice")[0].innerText = itemPrice + itemTotal + 'kr';
        orderRow.getElementsByClassName("orderTotal")[0].innerText = itemTotal + 'kr';
        orderRow.getElementsByClassName("orderSaved")[0].innerText = itemSaved + 'kr';

        totalPrice += itemTotal;
        totalSaved += itemSaved;

        let newRow = orderTable.insertRow(2);
        newRow.innerHTML = orderRow.innerHTML;
    }

    document.getElementById("totalPrice").innerText = totalPrice + 'kr';
    document.getElementById("totalSaved").innerText = totalSaved + 'kr';
    document.getElementById("totalTax").innerText = (totalPrice * 0.3) + 'kr';
}

async function pay()
{
    console.log("Validating Delivery information")
    const orders = await fetchBasketItems();
    if (JSON.parse(orders).length === 0)
    {
        showBasketPopup("There are no orders to purchase", true)
        return;
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
        return
    }
    if (inputPhoneNumber.value === '')
    {
        showBasketPopup("The phone number is required", true);
        return
    }
    if (inputEmail.value === '')
    {
        showBasketPopup("The email is required", true);
        return
    }
    if (inputStreetAddress.value === '')
    {
        showBasketPopup("The street address is required", true);
        return
    }
    if (inputCity.value === '')
    {
        showBasketPopup("The city is required", true);
        return
    }
    if (inputPostcode.value === '')
    {
        showBasketPopup("The postcode is required", true);
        return
    }

    const termsCheckbox = document.getElementById('termsAndConditionsCheckbox');

    if (termsCheckbox.checked === false)
    {

        showBasketPopup("You have to accept our terms and conditions", true)
        return;
    }
    console.log("The basket information is correct, proceeding to payment")
    window.open("/payment_temp");
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
