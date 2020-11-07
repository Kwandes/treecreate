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

        let status = order['status'];
        if (status === "initial")
        {
            status = "Waiting for payment";
        }
        if (status === "new")
        {
            status = "Payment Successful";
        }

        orderItem.getElementsByClassName('orderStatus')[0].innerText = "Status: " + status;
        orderItem.getElementsByClassName('orderTotalPrice')[0].innerText = "Total Price: " + (order['price']/100) + " dkk";
        console.log("Order id: " + order['id']);
        orderItem.getElementsByClassName("orderTransactionId")[0].innerText = "Order ID: " + order['id'];

        let createdOn = order['createdOn'];
        try
        {
            createdOn = createdOn.split(".")[0];
            createdOn = createdOn.replace("T", " ");
        } catch (e)
        {
            console.log("%cFailed to handle createdOn properly: " + e, "color:red");
            createdOn = "N/A";
        }
        orderItem.getElementsByClassName("orderDateCreated")[0].innerText = "Created on: " + createdOn;
        orderItem.getElementsByClassName("orderExpectedDelivery")[0].innerText = "Expected Delivery Date: " + order['expectedDeliveryDate'];

        orderItem.getElementsByClassName("orderAddressStreetName")[0].innerText = "Street: " + order['streetAddress'];
        orderItem.getElementsByClassName("orderAddressCity")[0].innerText = "City: " + order['city'];
        orderItem.getElementsByClassName("orderAddressPostcode")[0].innerText = "Postcode: " + order['postcode'];

        detailsPage.append(orderItem)
    }
}

async function fetchTransactions()
{
    const response = await fetch(location.origin + "/getTransaction");
    console.log("%cFetching transactions has finished, status: " + response.status, "color:mediumpurple");
    return await response.text();
}