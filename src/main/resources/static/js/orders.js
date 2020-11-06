async function generateOrders()
{
    console.log("%cHey orders viewer, thanks for summoning me, ze order generator", "color:mediumpurple");
    const orderBaseline = document.getElementsByClassName("orderContainer")[0];
    const detailsPage = document.getElementById("details");
    const ordersJson = await fetchTransactions();

    if (ordersJson === '[]')
    {
        console.log("%cThe response body was empty", "color: red");
        return;
    }
    let orders = JSON.parse(ordersJson);
    //console.log("Order: " + ordersJson)
    console.log("Order count: " + (orders.length + 1))
    for (let i = 0; i < orders.length; i++)
    {
        let order = orders[i];
        console.log("Processing order : " + i)
        //console.log(order)
        let orderItem = orderBaseline.cloneNode(true);
        orderItem.style.display = "flex";

        orderItem.getElementsByClassName('orderStatus')[0].innerText = "Status: " + order['status'];
        orderItem.getElementsByClassName('orderTotalPrice')[0].innerText = "Total Price: " + (order['price']/100) + " dkk";
        console.log("Order id: " + order['id']);
        orderItem.getElementsByClassName("orderTransactionId")[0].innerText = "Order ID: " + order['id'];

        detailsPage.append(orderItem)
    }
}

async function fetchTransactions()
{
    const response = await fetch(location.origin + "/getTransaction");
    console.log("%cFetching transactions has finished, status: " + response.status, "color:mediumpurple");
    return await response.text();
}