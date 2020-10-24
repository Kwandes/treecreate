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