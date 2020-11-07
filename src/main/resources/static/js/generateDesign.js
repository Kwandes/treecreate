async function generateDesign()
{
    let design = await fetchDesign();
    if (design === '')
    {
        console.log("%cFailed to obtain the design data", "color:red");
        return;
    }
    console.log("Design data: " + design);

    design = JSON.parse(design);
    console.dir(design);

    document.getElementById("bannerDesignInput").getAttribute("value").replace(design["bannerDesign"]); // TODO - What the fuck how do you work
    document.getElementById("bannerTextInput").value = design["bannerText"];
    document.getElementById("bannerTextPath").innerHTML = design["bannerText"]; // The event listener doesn't trigger so I have to manually change the text
    document.getElementById("fontInput").selectedIndex = design["fontStyle"];
    document.getElementById("boxSizeInput").value = design["boxSize"];
    document.getElementById("fontSizeInput").checked = design["isBigFont"];

    let boxes = design["boxes"];
    console.log("Generating boxes...")
    for (let box of boxes)
    {
        console.log("Managing box id: " + box["boxId"]);
        console.log("box text: " + box["text"]);

        let boxDesignBackground = 'url(' + '../img/images/boxDesign/box01/' + box["boxDesign"] + '.svg' + ') 100% 100%';

        let displayedBoxes = document.getElementsByClassName("draggableBox"); // It corresponds to the 'boxes' variable from the familyTree.js

        let clone = displayedBoxes[0].cloneNode(true);
        let boxText = clone.getElementsByClassName("input")[0];
        boxText.innerHTML = box["text"];

        console.log("Doing styling stuff");

        console.log("Assigning position");

        let cursorX = viewportToPixels(box['creationCursorX'] + 'vw');
        let cursorY = viewportToPixels(box['creationCursorY'] + 'vw');

        constructBox(clone, cursorX, cursorY);

        clone.style.background = boxDesignBackground;
        clone.style.backgroundSize = '100% 100%';
    }

    // Fix for the familyTree.js overriding the sizing and setting it to 10
    console.log("Assigning box size")
    let boxSize = design["boxSize"];
    boxSizeY = boxSize / 4;
    boxSizeX = boxSize / 8;
    let i = 10;
    if (i == boxSize)
    {
        console.log("The sizes are the same")
        document.getElementById("decreaseBoxButton").click();
        document.getElementById("increaseBoxButton").click();
    }

    while (i != boxSize)
    {
        if (i < boxSize)
        {
            console.log("Increasing size");
            document.getElementById("increaseBoxButton").click();
            i++;
        } else
        {
            console.log("Decreasing size")
            document.getElementById("decreaseBoxButton").click();
            i--;
        }
    }

    console.log("Apply the font in a very clean fashion")

    const banner = document.getElementById("bannerTextPath");
    const fonts = ["Spectral, sans-serif", "'Sansita Swashed', cursive", "'Roboto Slab', serif"];
    const fontStyleSelect = document.getElementById("fontInput");
    boxes = document.getElementsByClassName("draggableBox");
    banner.style.fontFamily = fonts[fontStyleSelect.options[fontStyleSelect.selectedIndex].value];
    for (let i = 0; i < boxes.length; i++)
    {
        boxes[i].getElementsByClassName("draggableBoxMiddleRow")[0]
            .getElementsByClassName("draggableBoxInput")[0]
            .style.fontFamily = fonts[fontStyleSelect.options[fontStyleSelect.selectedIndex].value];
    }
    console.log("Selected font index: " + fontStyleSelect.options[fontStyleSelect.selectedIndex].value);

    console.log("%c generation finished", "color:mediumpurple")
}

async function fetchDesign()
{
    const response = await fetch(location.origin + "/products/getFamilyTreeDesign" + location.search);
    console.log("%cFetching the design has finished, status: " + response.status, "color:mediumpurple");
    return await response.text();
}