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
    console.log("Loading basket orders")
    const defaultRow = document.getElementsByClassName("orderRow")[0];
    const orderTable = document.getElementById("orderTable");
    console.log("Default row: " + defaultRow)
    const ordersJson = await fetchBasketItems();

    if (ordersJson === '[]')
    {
        console.log("%cThe response body was empty", "color: red");
        return;
    }
    let orders = JSON.parse(ordersJson);
    console.log("Order count: " + (orders.length + 1))
    let totalPrice = 0;
    let totalSaved = 0;
    for (let i = 0; i < orders.length; i++)
    {
        let orderRow = defaultRow.cloneNode(true);
        orderRow.style.display = 'table-row'
        let order = orders[i];
        console.log("Order: " + i);
        console.log("Id: " + order["orderId"]);
        const design = JSON.parse(order['treeDesignById']['designJson']);
        //console.dir(design);
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