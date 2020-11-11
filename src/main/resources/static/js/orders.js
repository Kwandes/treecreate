async function generateOrders()
{
    console.log("%cHey orders viewer, thanks for summoning me, ze order generator", "color:mediumpurple");
    const transactionBaseline = document.getElementsByClassName("orderContainer")[0];
    const detailsPage = document.getElementById("details");
    const transactionsJson = await fetchTransactions();

    if (transactionsJson === '[]')
    {
        console.log("%cThe response body was empty", "color: red");
        return;
    }
    let transactions = JSON.parse(transactionsJson);
    //console.log("Order: " + transactionsJson)
    console.log("transaction count: " + (transactions.length + 1))
    for (let i = 0; i < transactions.length; i++)
    {
        let transaction = transactions[i];
        console.log("Processing transaction : " + i)
        //console.log(transaction)
        let transactionItem = transactionBaseline.cloneNode(true);
        transactionItem.style.display = "flex";

        let status = transaction['status'];
        if (status === "initial")
        {
            status = "Waiting for payment";
        }
        if (status === "new")
        {
            status = "Payment Successful";
        }

        transactionItem.getElementsByClassName('orderStatus')[0].innerText = "Status: " + status;
        transactionItem.getElementsByClassName('orderTotalPrice')[0].innerText = "Total Price: " + (transaction['price']/100) + " dkk";
        console.log("transaction id: " + transaction['id']);
        transactionItem.getElementsByClassName("orderTransactionId")[0].innerText = "transaction ID: " + transaction['id'];

        let createdOn = transaction['createdOn'];
        try
        {
            createdOn = createdOn.split(".")[0];
            createdOn = createdOn.replace("T", " ");
        } catch (e)
        {
            console.log("%cFailed to handle createdOn properly: " + e, "color:red");
            createdOn = "N/A";
        }
        transactionItem.getElementsByClassName("orderDateCreated")[0].innerText = "Created on: " + createdOn;
        transactionItem.getElementsByClassName("orderExpectedDelivery")[0].innerText = "Expected Delivery Date: " + transaction['expectedDeliveryDate'];

        transactionItem.getElementsByClassName("orderAddressStreetName")[0].innerText = "Street: " + transaction['streetAddress'];
        transactionItem.getElementsByClassName("orderAddressCity")[0].innerText = "City: " + transaction['city'];
        transactionItem.getElementsByClassName("orderAddressPostcode")[0].innerText = "Postcode: " + transaction['postcode'];

        detailsPage.append(transactionItem)
    }
}

async function fetchTransactions()
{
    const response = await fetch(location.origin + "/getTransaction");
    console.log("%cFetching transactions has finished, status: " + response.status, "color:mediumpurple");
    return await response.text();
}