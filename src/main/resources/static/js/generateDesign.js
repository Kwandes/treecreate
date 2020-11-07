function viewportToPixels(value)
{
    let parts = value.match(/([0-9\.]+)(vh|vw)/)
    let q = Number(parts[1])
    let side = window[['innerHeight', 'innerWidth'][['vh', 'vw'].indexOf(parts[2])]]
    return side * (q / 100)
}

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
        let boxSizeX = 40 / 4;
        let boxSizeY = 40 / 8;
        let boxSize = 40;
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
        clone.style.height = boxSize + 'vw';

        console.log("Assigning position");
        //let cursorX = box["positionX"];
        //let cursorY = box["positionY"];

        console.log("PositionX, vw: " + box["positionX"]);
        console.log("PositionY, vw: " + box["positionY"]);
        console.log("PositionX, px: " + viewportToPixels(box["positionX"] + 'vw'));
        console.log("PositionY, px: " + viewportToPixels(box["positionY"] + 'vw'));
        const scrollOffsetX = window.scrollX;
        const scrollOffsetY = window.scrollY;
        let boxPositionX = parseInt(box["positionX"]);
        let boxPositionY = parseInt(box["positionY"]);
        let cursorX = viewportToPixels(box['creationCursorX'] + 'vw');
        let cursorY = viewportToPixels(box['creationCursorY'] + 'vw');
        let parentX = boundaries.offsetLeft;
        let parentY = boundaries.offsetTop;
        let offsetX = viewportToPixels(boxSizeX + 'vw') / 2;
        let offsetY = viewportToPixels(boxSizeY + 'vw') / 2;
        // create the box at the cursor coordinates
        boundaries.appendChild(clone);
        //console.log("Cursor: " + cursorX + " / " + cursorY);
        //console.log("Parent: " + parentX + " / " + parentY);
        //console.log("Offset: " + offsetX + " / " + offsetY);
        setTranslate(box["positionX"], box["positionY"], clone);
        clone.xOffset = cursorX - parentX - offsetX + scrollOffsetX;
        console.log("xOffset : " + clone.xOffset + " CursorX : " + cursorX + " ParentX : " + parentX + " OffsetX : " + offsetX)
        clone.yOffset = cursorY - parentY - offsetY + scrollOffsetY;
        console.log("yOffset : " + clone.yOffset + " CursorY : " + cursorY + " ParentY : " + parentY + " OffsetY : " + offsetY)

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
        console.log("Set initial X and Y to 0");
        // Family Tree copy because I need it because fuck GRASP and single responsibility
        function dragStart(e)
        {
            active = true;

            // this is the item we are interacting with
            activeItem = e.target.parentNode.parentNode;

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
                    console.log("Drag start: xOffset: " + activeItem.xOffset);
                    console.log("Drag start: yOffset: " + activeItem.yOffset);
                    activeItem.initialX = e.clientX - activeItem.xOffset
                    activeItem.initialY = e.clientY - activeItem.yOffset;
                    console.log("Drag start: initialX: " + activeItem.initialX);
                    console.log("Drag start: initialY: " + activeItem.initialY);
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

                    activeItem.currentX = e.touches[0].clientX - activeItem.initialX + window.scrollX;
                    activeItem.currentY = e.touches[0].clientY - activeItem.initialY + window.scrollY;
                } else
                {
                    //console.log("Initials: " + activeItem.initialX + window.scrollX + ', ' + activeItem.initialY);
                    activeItem.currentX = e.clientX - activeItem.initialX;
                    activeItem.currentY = e.clientY - activeItem.initialY;
                }
                console.log("I got triggered")
                activeItem.xOffset = activeItem.currentX;
                activeItem.yOffset = activeItem.currentY;

                //console.log("Current: " + activeItem.currentX + ', ' + activeItem.currentY)
                //console.log("Window Scroll: " + window.scrollX + ', ' + window.scrollY)
                //console.log("CLick: " + e.clientX + ' ' + e.clientY)

                //console.log("Offset Left: " + activeItem.currentX)
                //console.log("Offset Top: " + activeItem.currentY)
                //console.log("Offset Left: " + pixelsToViewportWidth(activeItem.currentX))
                //console.log("Offset Top: " + pixelsToViewportWidth(activeItem.currentY))

                if (!isWithinOuterBoundaries(boundaries, activeItem.currentX, activeItem.currentY, boxSizeX, boxSizeY))
                {
                    console.log("Outside of drag bouncries")
                    return;
                }

                setTranslate(pixelsToViewportWidth(activeItem.currentX),
                    pixelsToViewportWidth(activeItem.currentY), activeItem);
            }
        }
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