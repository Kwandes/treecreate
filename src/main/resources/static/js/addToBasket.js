async function addToBasket()
{
    console.log("%cI got triggered yay", "color: mediumpurple");
    // TODo - merge with saving of a design
    // ToDo Actually add it to the basket that is persistent per session/user
    const designJson = await getDesign();
    if (designJson == null)
    {
        console.log("No design available, returning");
        return;
    }
    const amount = document.getElementById("amountInput").value;
    const treeSize = document.getElementById("sizeInput").value;
    console.log("Saving a new order");
    console.log("Amount: " + amount);
    console.log("Size: " + treeSize);
    console.log("Design: " + designJson);

    console.log("-")

    console.log("Saving the design");
    let designId = await saveDesign(designJson);
    let userId = await getCurrentUser();
    console.log("Design saved, ID: " + designId);
    console.log("Adding a new order for user: " + userId)
    let orderInfo = JSON.stringify({
        "amount": amount,
        "size": treeSize,
        "treeDesignById": {"id": designId},
        "userByUserId": {"id": userId}
    });
    console.dir(JSON.parse((orderInfo)));
    let orderId = await saveOrder(orderInfo);
    console.log("Order added, Order id: " + orderId)
}

async function getCurrentUser()
{
    const response = await fetch(location.origin + "/getCurrentUser")
    console.log("%cFetching current user has finished, status: " + response.status, "color:mediumpurple");
    return await response.text();
}

async function saveDesign(designJson)
{
    const response = await fetch(location.origin + "/addTreeDesign",
        {
            method: "POST",
            headers: {'Content-type': 'application/json'},
            body: designJson,
        });
    console.log("%cSaving family design has finished, status: " + response.status, "color:mediumpurple");
    return await response.text();
}

async function saveOrder(orderInfo)
{
    const response = await fetch(location.origin + "/addTreeOrder",
        {
            method: "POST",
            headers: {'Content-type': 'application/json'},
            body: orderInfo,
        });
    console.log("%cSaving tree order has finished, status: " + response.status, "color:mediumpurple");
    return await response.text();
}

function increaseAmount()
{
    let amountInput = document.getElementById("amountInput");
    let amount = amountInput.value;
    amountInput.value = ++amount;
}

function decreaseAmount()
{
    let amountInput = document.getElementById("amountInput");
    let amount = amountInput.value;
    console.log(amount)
    if (amount > 1)
    {
        amountInput.value = --amount;
    }
}

const sizeOptions = ["20x20 cm", "25x25 cm", "30x30 cm"];

function increaseSize()
{
    let sizeInput = document.getElementById("sizeInput");
    let currentSizeIndex = sizeOptions.indexOf(sizeInput.value)
    if (currentSizeIndex === -1)
    {
        sizeInput.value = sizeOptions[0];
    } else
    {
        if (currentSizeIndex === sizeOptions.length - 1)
        {
            sizeInput.value = sizeOptions[0];
        } else
        {
            sizeInput.value = sizeOptions[++currentSizeIndex];
        }
    }
}

function decreaseSize()
{
    let sizeInput = document.getElementById("sizeInput");
    let currentSizeIndex = sizeOptions.indexOf(sizeInput.value)
    if (currentSizeIndex === -1)
    {
        sizeInput.value = sizeOptions[0];
    } else
    {
        if (currentSizeIndex === 0)
        {
            sizeInput.value = sizeOptions[sizeOptions.length - 1];
        } else
        {
            sizeInput.value = sizeOptions[--currentSizeIndex];
        }
    }
}

