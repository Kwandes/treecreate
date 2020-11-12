const orderSizes = new Map([
    ["20x20 cm", 495],
    ["25x25 cm", 695],
    ["30x30 cm", 995]
]);

async function generateOrders()
{
    console.log("%cHey orders viewer, thanks for summoning me, ze order generator", "color:mediumpurple");
    const transactionBaseline = document.getElementsByClassName("orderContainer")[0];
    const orderBaseline = document.getElementsByClassName("orderDesignContainer")[0];
    const detailsPage = document.getElementById("details");
    const transactionsJson = await fetchTransactions();

    if (transactionsJson === '[]')
    {
        console.log("%cThe response body was empty", "color: red");
        return;
    }
    let transactions = JSON.parse(transactionsJson);
    console.log("transaction count: " + (transactions.length + 1))
    for (let i = 0; i < transactions.length; i++)
    {
        let transaction = transactions[i];
        console.log("Processing transaction : " + i)
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
        transactionItem.getElementsByClassName('orderTotalPrice')[0].innerText = "Total Price: " + (transaction['price'] / 100) + " dkk";
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

        //console.log("Transaction object:")
        //console.dir(transaction);
        const orderList = transaction['orderList'];
        //console.dir(orderList)
        for (let i = 0; i < orderList.length; i++)
        {
            const order = orderList[i];
            console.log("Processing order " + order['orderId']);
            let orderItem = orderBaseline.cloneNode(true);
            orderItem.style.display = "flex";

            const design = order['treeDesignById'];
            const designJson = JSON.parse(design['designJson'])

            let itemPrice = orderSizes.get(order.size);
            let amount = parseInt(order.amount);
            let itemTotal = (itemPrice * amount);
            let itemSaved = 0;
            if (amount > 3)
            {
                itemSaved = itemTotal * 0.25;
                itemTotal *= 0.75;
            }
            orderItem.getElementsByClassName("orderDesignName")[0].innerText = designJson.name;
            orderItem.getElementsByClassName("orderDesignSize")[0].innerText = order.size;
            orderItem.getElementsByClassName("orderDesignAmount")[0].innerText = 'amount:' + order.amount;
            orderItem.getElementsByClassName("orderDesignPrice")[0].innerText = 'price: ' + itemTotal + 'kr';

            transactionItem.getElementsByClassName("orderDesignInfo")[0].append(orderItem);
        }

        detailsPage.append(transactionItem)
        console.log("Finished generating transaction id: " + i)
    }
}

async function fetchTransactions()
{
    const response = await fetch(location.origin + "/getTransaction");
    console.log("%cFetching transactions has finished, status: " + response.status, "color:mediumpurple");
    return await response.text();
}

function viewDesign(e)
{
    console.log("Viewing X");
}

