async function generateCollections()
{
    console.log("%cHey collection viewer, thanks for summoning me, ze collection generator", "color:mediumpurple");
    const collectionBaseline = document.getElementsByClassName("collectionContainer")[0];
    const detailsPage = document.getElementById("details");
    const ordersJson = await fetchOrders();

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
        console.log(order)
        let collectionItem = collectionBaseline.cloneNode(true);
        collectionItem.style.display = "flex";

        const design = JSON.parse(order['treeDesignById']['designJson']);
        console.dir(design);

        collectionItem.getElementsByClassName("spanBoxText")[0].innerText = "Boxes: " + design['boxes'].length;

        let boxContent = '';
        for (let j = 0; j < design['boxes'].length; j++)
        {
            if (j === 5)
            {
                break;
            }
            let text = design['boxes'][j]['text']
            if (text !== '')
            {
                boxContent += text + ', ';
            }
        }
        boxContent = boxContent.substr(0, boxContent.length - 2);
        collectionItem.getElementsByClassName("boxContent")[0].innerText = boxContent;

        let dateCreated = order['treeDesignById']['dateCreated'];
        dateCreated = dateCreated.replace('T', ' ');
        dateCreated = dateCreated.split('.')[0];
        collectionItem.getElementsByClassName("dateCreated")[0].innerText = "Boxes: " + dateCreated;

        detailsPage.append(collectionItem)
    }
}

async function fetchOrders()
{
    const response = await fetch(location.origin + "/getTreeOrders");
    console.log("%cFetching basket items has finished, status: " + response.status, "color:mediumpurple");
    return await response.text();
}