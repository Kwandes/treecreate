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
            status = localeOrdersGenerateOrdersStatusInitial;
        }
        if (status === "new")
        {
            status = localeOrdersGenerateOrdersStatusNew;
        }

        transactionItem.getElementsByClassName('orderStatus')[0]
            .innerText = localeOrdersGenerateOrdersStatus + status;
        transactionItem.getElementsByClassName('orderTotalPrice')[0]
            .innerText = localeOrdersGenerateOrdersTotalPrice + (transaction['price'] / 100) + " dkk";
        console.log("transaction id: " + transaction['id']);
        transactionItem.getElementsByClassName("orderTransactionId")[0]
            .innerText = localeOrdersGenerateOrdersTransactionId + transaction['id'];
        if (status === localeOrdersGenerateOrdersStatusInitial)
        {
            console.log("Payment was not completed - adding a complete payment button with link: " + transaction['paymentLink'])
            transactionItem.getElementsByClassName("orderCompletePaymentBtn")[0].style.display = "flex";
            transactionItem.getElementsByClassName("orderCompletePaymentBtn")[0].setAttribute("onclick", "redirect(\"" + transaction['paymentLink'] + "\")");
        }

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
        transactionItem.getElementsByClassName("orderDateCreated")[0].innerText = localeOrdersGenerateOrdersCreatedOn + createdOn;
        transactionItem.getElementsByClassName("orderExpectedDelivery")[0].innerText = localeOrdersGenerateOrdersExpectedDelivery + transaction['expectedDeliveryDate'];
        transactionItem.getElementsByClassName("orderAddressStreetName")[0].innerText = localeOrdersGenerateOrdersAddress + transaction['streetAddress'];
        if (transaction['streetAddress2'] !== null)
        {
            transactionItem.getElementsByClassName("orderAddressStreetName2")[0].style.display = "flex";
            transactionItem.getElementsByClassName("orderAddressStreetName2")[0].innerText = localeOrdersGenerateOrdersAddress2 + transaction['streetAddress2'];
        }
        transactionItem.getElementsByClassName("orderAddressCity")[0].innerText = localeOrdersGenerateOrdersCity + transaction['city'];
        transactionItem.getElementsByClassName("orderAddressPostcode")[0].innerText = localeOrdersGenerateOrdersPostcode + transaction['postcode'];

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
            const designJson = JSON.parse(design['designJson']);
            const designId = design.id;
            console.dir(designJson);

            let itemPrice = orderSizes.get(order.size);
            let amount = parseInt(order.amount);
            let itemTotal = (itemPrice * amount);
            let itemSaved = 0;
            if (amount > 3)
            {
                itemSaved = itemTotal * 0.25;
                itemTotal *= 0.75;
            }
            orderItem.getElementsByClassName("orderDesignId")[0].innerText = designId;
            orderItem.getElementsByClassName("orderDesignName")[0].innerText = designJson.name;
            orderItem.getElementsByClassName("orderDesignSize")[0].innerText = order.size;
            orderItem.getElementsByClassName("orderDesignAmount")[0].innerText = localeOrdersGenerateOrdersAmount + order.amount;
            orderItem.getElementsByClassName("orderDesignPrice")[0].innerText = localeOrdersGenerateOrdersPrice + itemTotal + 'kr';

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

async function viewDesign(e)
{
    const id = e.parentNode.getElementsByClassName("orderDesignId")[0].innerText;
    console.log(id);
    const response = await fetch(location.origin + "/products/readOnlyFamilyTree?designId=" + id);
    console.log("%Displaying read only tree, status: " + response.status, "color:mediumpurple");
    window.location.href = location.origin + "/products/readOnlyFamilyTree?designId=" + id;
}

function redirect(link)
{
    window.location.href = link;
}

