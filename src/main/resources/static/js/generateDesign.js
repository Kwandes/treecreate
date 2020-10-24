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
        // Copy over the variables from familyTree.js because they are not global/accessible...
        const boundaries = document.getElementById("draggableBoxContainer");
        let activeItem = null;
        let active = false;
        let boxSize = 10;
        let boxDesignBackground = 'url(' + '../img/images/boxDesign/box01/' + box["boxDesign"] + '.svg' + ') 100% 100%';

        let displayedBoxes = document.getElementsByClassName("draggableBox"); // It corresponds to the 'boxes' variable from the familyTree.js

        let clone = displayedBoxes[0].cloneNode(true);
        let boxText = clone.getElementsByClassName("input")[0];
        boxText.innerHTML = box["text"];


        console.log("Doing styling stuff");
        // Copied code, not much new
        // box style
        clone.style.display = 'flex';
        clone.style.flexWrap = 'wrap';
        clone.style.position = 'absolute';
        clone.style.width = boxSize + 'vw';
        clone.style.height = boxSize + 'vh';

        console.log("Assigning position");
        //let cursorX = box["positionX"];
        //let cursorY = box["positionY"];

        let cursorX = viewportToPixels(box["positionX"] + 'vw');
        let cursorY = viewportToPixels(box["positionY"] + 'vh');
        let parentX = boundaries.offsetLeft;
        let parentY = boundaries.offsetTop;
        let offsetX = viewportToPixels(boxSize + 'vw') / 2;
        let offsetY = viewportToPixels(boxSize + 'vh') / 2;

        // create the box at the cursor coordinates
        boundaries.appendChild(clone);
        console.log("Cursor: " + cursorX + " / " + cursorY);
        console.log("Parent: " + parentX + " / " + parentY);
        console.log("Offset: " + offsetX + " / " + offsetY);
        setTranslate(box["positionX"], box["positionY"], clone);
        clone.xOffset = cursorX - parentX - offsetX;
        clone.yOffset = cursorY - parentY - offsetY;

        clone.style.background = boxDesignBackground;
        clone.style.backgroundSize = '100% 100%';

        // add events listeners on the drag button
        let button = clone.getElementsByClassName("dragDraggableBoxButton")[0];
        // touch input
        console.log("Adding listeners");
        button.addEventListener("touchstart", dragStart, false);
        button.addEventListener("touchend", dragEnd, false);
        // mouse input
        button.addEventListener("mousedown", dragStart, false);
        button.addEventListener("mouseup", dragEnd, false);

        boundaries.addEventListener("mousemove", drag, false);

        console.log("Listeners have been added");

        // Family Tree copy because I need it because fuck GRASP and single responsibility
        function dragStart(e)
        {
            active = true;

            // this is the item we are interacting with
            activeItem = e.target.parentNode.parentNode;
            activeItemInitialX = activeItem.offsetLeft;
            activeItemInitialY = activeItem.offsetTop;

            if (activeItem !== null)
            {
                if (!activeItem.xOffset)
                {
                    activeItem.xOffset = 0;
                }

                if (!activeItem.yOffset)
                {
                    activeItem.yOffset = 0;
                }

                if (e.type === "touchstart")
                {
                    activeItem.initialX = e.touches[0].clientX - activeItem.xOffset;
                    activeItem.initialY = e.touches[0].clientY - activeItem.yOffset;
                } else
                {
                    activeItem.initialX = e.clientX - activeItem.xOffset;
                    activeItem.initialY = e.clientY - activeItem.yOffset;
                }
            }
        }

        function dragEnd(e)
        {
            if (activeItem !== null)
            {
                activeItem.initialX = activeItem.currentX;
                activeItem.initialY = activeItem.currentY;
            }

            active = false;
            activeItem = null;
        }

        function drag(e)
        {
            if (active)
            {
                if (e.type === "touchmove")
                {
                    e.preventDefault();

                    activeItem.currentX = e.touches[0].clientX - activeItem.initialX;
                    activeItem.currentY = e.touches[0].clientY - activeItem.initialY;
                } else
                {
                    activeItem.currentX = e.clientX - activeItem.initialX;
                    activeItem.currentY = e.clientY - activeItem.initialY;
                }

                activeItem.xOffset = activeItem.currentX;
                activeItem.yOffset = activeItem.currentY;

                setTranslate(pixelsToViewportWidth(activeItem.currentX),
                    pixelsToViewportHeight(activeItem.currentY), activeItem);
            }
        }
    }

    // Fix for the familyTree.js overriding the sizing and setting it to 10
    console.log("Assigning box size")
    let boxSize = design["boxSize"];
    for (let i = 10; i != boxSize; i++)
    {
        if (i < boxSize)
        {
            console.log("Increasing size");
            document.getElementById("increaseBoxButton").click();
        } else
        {
            console.log("Decreasing size")
            document.getElementById("decreaseBoxButton").click();
        }
    }
    console.log("%c generation finished", "color:mediumpurple")
}

async function fetchDesign()
{
    const response = await fetch(location.origin + "/products/getFamilyTreeDesign" + location.search);
    console.log("%cFetching the design has finished, status: " + response.status, "color:mediumpurple");
    return await response.text();
}