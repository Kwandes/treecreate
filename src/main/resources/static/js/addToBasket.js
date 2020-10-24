async function addToBasket()
{
    console.log("%cI got triggered yay", "color: mediumpurple");
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

function getDesign()
{
    console.log("%cThanks for saving me, I appreciate it", "color:mediumpurple");

    let boxArray = document.getElementsByClassName("draggableBox");

    const bannerDesign = document.getElementById("bannerDesignInput").getAttribute("value").toString();
    const bannerText = document.getElementById("bannerTextPath").innerHTML.toString().trim();
    const fontStyle = document.getElementById("fontInput").selectedIndex;
    const isBigFont = document.getElementById("fontSizeInput").checked
    const boxSize = document.getElementById("boxSizeInput").value;

    let familyTreeDesign = JSON.parse(JSON.stringify({
        id: 1,
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

        const positionPattern = 'translate3d\\((-?\\d+\\.\\d+)vw,\\s(-?\\d+\\.\\d+)vh';
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

