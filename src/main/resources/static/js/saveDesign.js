function saveDesign()
{
    console.log("%cThanks for saving me, I appreciate it", "color:mediumpurple");

    let boxArray = document.getElementsByClassName("draggableBox");

    console.log("Family Tree design settings: ");
    const bannerDesign = document.getElementById("bannerDesignInput").getAttribute("value").toString();
    const bannerText = document.getElementById("bannerTextPath").innerHTML.toString().trim();
    const fontStyle = document.getElementById("fontInput").selectedIndex;
    const isBigFont = document.getElementById("fontSizeInput").checked
    const boxSize = document.getElementById("boxSizeInput").value;
    console.log("Banner Design: " + bannerDesign);
    console.log("Banner text: " + bannerText);
    console.log("Font style: " + fontStyle);
    console.log("Big Font mode: " + isBigFont);
    console.log("Box size: " + boxSize)

    if (boxArray.length === 1)
    {
        console.log("Haven't found any boxes");
        return;
    }
    console.log("Found boxes:");
    for (let i = 1; i < boxArray.length; i++)
    {
        console.groupCollapsed("Box #" + i);
        const boxStyle = boxArray[i].getAttribute("style").toString();
        const text = boxArray[i].getElementsByClassName("draggableBoxInput").item(0).innerHTML.toString();
        console.log("Box text: " + text)

        const positionPattern = 'left:\\s(\\d+\\.\\d+)px;\\stop:\\s(\\d+\\.\\d+)px';
        let positionMatch = boxStyle.match(positionPattern)
        const positionX = positionMatch[1];
        const positionY = positionMatch[2];
        console.log("Position: " + positionX + " / " + positionY)

        const boxStylePattern = '(box\\d+)\\.svg';
        const boxDesign = boxStyle.match(boxStylePattern)[1];
        console.log("Design: " + boxDesign);
        console.groupEnd();
    }
}