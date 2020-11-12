async function addToBasket()
{
    console.log("%cI got triggered yay", "color: mediumpurple");
    const designJson = await getDesign();
    if (designJson == null)
    {
        console.log("No design available, returning");
        document.getElementById("addToBasketBackBtn").click();
        showBasketPopup("There is no design to add to the basket", true);
        return;
    }
    const amount = document.getElementById("amountInput").value;
    const treeSize = document.getElementById("sizeInput").value;
    console.log("Saving a new order");
    console.log("Amount: " + amount);
    console.log("Size: " + treeSize);
    console.log("Design: " + designJson);
    console.dir(designJson)

    console.log("-")

    console.log("Saving the design");
    let designId = await saveDesign(designJson);
    let userId = await getCurrentUser();
    console.log("Design saved, ID: " + designId);
    console.log("Adding a new order for user: " + userId)
    let orderInfo = JSON.stringify({
        "amount": amount,
        "size": treeSize,
        "status": "active",
        "treeDesignById": {"id": designId},
        "userByUserId": {"id": userId}
    });
    console.dir(JSON.parse((orderInfo)));
    let orderId = await saveOrder(orderInfo);
    console.log("Order added, Order id: " + orderId)
    console.log("Updating the basket item count");
    updateBasket();
    document.getElementById("addToBasketBackBtn").click();
    showBasketPopup("The order has been added to the basket", false);

}

async function changeDesign()
{
    console.log("%Updating design", "color: mediumpurple");
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
    console.dir(designJson)

    console.log("-")

    console.log("Saving the order");
    let designId = await updateDesign(designJson);
    let userId = await getCurrentUser();
    console.log("Design saved, ID: " + designId);
    console.log("Adding a new order for user: " + userId)
    let orderInfo = JSON.stringify({
        "amount": amount,
        "size": treeSize,
        "status": "active",
        "treeDesignById": {"id": designId},
        "userByUserId": {"id": userId}
    });
    console.log("Design saved, ID: " + designId);
    console.log("Adding a new order for user: " + userId);
    console.dir(JSON.parse((orderInfo)));
    let orderId = await updateOrder(orderInfo);
    console.log("Order updated, Order id: " + orderId)
    location.replace("/account/collections");
}

function getDesign()
{
    console.log("%cThanks for saving me, I appreciate it", "color:blue");

    let boxArray = document.getElementsByClassName("draggableBox");

    let designId;
    if (location.search.includes("designId="))
    {
        designId = location.search.match("designId=(\\d+)")[1];
    } else
    {
        designId = 1;
    }
    const bannerDesign = document.getElementById("bannerDesignInput").getAttribute("value").toString();
    const bannerText = document.getElementById("bannerTextPath").innerHTML.toString().trim();
    const fontStyle = document.getElementById("fontInput").selectedIndex;
    const isBigFont = document.getElementById("fontSizeInput").checked
    const boxSize = document.getElementById("boxSizeInput").value;
    const nameInput = document.getElementById("nameInput").value;

    let familyTreeDesign = JSON.parse(JSON.stringify({
        id: designId,
        name: nameInput,
        bannerDesign: bannerDesign,
        bannerText: bannerText,
        fontStyle: fontStyle,
        isBigFont: isBigFont,
        boxSize: boxSize,
        boxes: []
    }));
    if (boxArray.length === 1)
    {
        return;
    }

    for (let i = 1; i < boxArray.length; i++)
    {
        const boxStyle = boxArray[i].getAttribute("style").toString();
        const text = boxArray[i].getElementsByClassName("draggableBoxInput").item(0).innerHTML.toString();
        const creationCursorX = boxArray[i].getElementsByClassName("creationCursorX")[0].value;
        const creationCursorY = boxArray[i].getElementsByClassName("creationCursorY")[0].value;

        console.log("Creation cursor X: " + creationCursorX)
        console.log("Creation cursor Y: " + creationCursorY)
        const positionPattern = 'translate3d\\((-?\\d+\\.\\d+)vw,\\s(-?\\d+\\.\\d+)vw';
        let positionMatch = boxStyle.match(positionPattern)
        const positionX = positionMatch[1];
        const positionY = positionMatch[2];


        const boxStylePattern = '(box\\d+)\\.svg';
        const boxDesign = boxStyle.match(boxStylePattern)[1];

        let boxInfo = JSON.stringify({
            boxId: i - 1,
            text: text,
            positionX: positionX,
            positionY: positionY,
            creationCursorX: creationCursorX,
            creationCursorY: creationCursorY,
            boxDesign: boxDesign
        });
        familyTreeDesign.boxes.push(JSON.parse(boxInfo));
    }

    return JSON.stringify(familyTreeDesign);
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

async function updateDesign(designJson)
{
    const response = await fetch(location.origin + "/updateTreeDesign",
        {
            method: "POST",
            headers: {'Content-type': 'application/json'},
            body: designJson,
        });
    console.log("%Updating family design has finished, status: " + response.status, "color:mediumpurple");
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

async function updateOrder(orderInfo)
{
    const response = await fetch(location.origin + "/updateTreeOrder",
        {
            method: "POST",
            headers: {'Content-type': 'application/json'},
            body: orderInfo,
        });
    console.log("%Updating tree order has finished, status: " + response.status, "color:mediumpurple");
    return await response.text();
}


function increaseAmount()
{
    let amountInput = document.getElementById("amountInput");
    let amount = amountInput.value;
    amountInput.value = ++amount;
    updatePrice()
}

function decreaseAmount()
{
    let amountInput = document.getElementById("amountInput");
    let amount = amountInput.value;
    console.log(amount)
    if (amount > 1)
    {
        amountInput.value = --amount;
        updatePrice()
    }
}

//TODO - DONE (combine the sizes and prices into a map) -> Implement the map
let sizeAndPrice = new Map();
sizeAndPrice.set("20x20 cm", "495");
sizeAndPrice.set("25x25 cm", "695");
sizeAndPrice.set("30x30 cm","995");

const sizeOptions = ["20x20 cm", "25x25 cm", "30x30 cm"];
const sizePrices = ["495", "695", "995"]

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

        updatePrice()
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

        updatePrice()
    }
}

//TODO - apply discounts on the Nth amount
function updatePrice()
{
    const priceInput = document.getElementById("priceInput");
    const sizeInput = document.getElementById("sizeInput");
    const currentSizeIndex = sizeOptions.indexOf(sizeInput.value)
    const sizePrice = sizePrices[currentSizeIndex];
    const amount = document.getElementById("amountInput").value;
    let price = amount * sizePrice;

    priceInput.value = price + 'kr';
}
