let boxInputLimit = 29;

function setTranslate(xPos, yPos, element) // Uses viewport
{
    console.log("Translate: " + xPos + 'vw, ' + yPos + 'vw, 0');
    element.style.transform = "translate3d(" + xPos + "vw, "
        + yPos + "vw, 0)";
}

function viewportToPixels(value)
{
    let parts = value.match(/([0-9\.]+)(vh|vw)/)
    let q = Number(parts[1])
    let side = window[['innerHeight', 'innerWidth'][['vh', 'vw'].indexOf(parts[2])]]
    return side * (q / 100)
}

function pixelsToViewportHeight(value)
{
    return (value * (100 / document.documentElement.clientHeight)).toFixed(3);
}

function pixelsToViewportWidth(value)
{
    return (value * (100 / document.documentElement.clientWidth)).toFixed(3);
}

function getRandomInt(min, max)
{
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min) + min); //The maximum is exclusive and the minimum is inclusive
}

// triggered by deleteDraggableBoxButton
function deleteBox(button)
{
    button.parentNode.parentNode.parentNode.removeChild(button.parentNode.parentNode);
}

function getBoxImageSources(i, path)
{
    let imgArray = [];
    for (let j = 1; j <= i; j++)
    {
        const imgSrc = path + "box" + j + ".svg";
        imgArray.push(imgSrc);
    }
    return imgArray;
}

function assignBoxBackground(imgArray)
{
    let imageNumber = getRandomInt(0, imgArray.length - 1);
    return 'url(' + imgArray[imageNumber] + ') 100% 100%';
}


    const imgPath = "../img/images/boxDesign/box01/";
    const bannerPath = "../img/images/banner/banner";
    const images = getBoxImageSources(9, imgPath);
    const boxes = document.getElementsByClassName("draggableBox");
    const boundaries = document.getElementById("draggableBoxContainer");
    const bannerInput = document.getElementById("bannerTextInput");
    const banner = document.getElementById("bannerTextPath");
    const bannerContainer = document.getElementById("familyTreeBanner");
    const bannerNextButton = document.getElementById("nextBannerDesign");
    const bannerPreviousButton = document.getElementById("previousBannerDesign");
    const bannerDesignInput = document.getElementById("bannerDesignInput");
    const boxIncreaseButton = document.getElementById("increaseBoxButton");
    const boxDecreaseButton = document.getElementById("decreaseBoxButton");
    const boxSizeInput = document.getElementById("boxSizeInput");
    const bigFontInput = document.getElementById("fontSizeInput");
    const fontStyleSelect = document.getElementById("fontInput");
    const fonts = ["Spectral, sans-serif", "'Sansita Swashed', cursive", "'Roboto Slab', serif"];
    let activeItem = null;
    let bannerSelector = 1;
    let bannerOptions = 1;
    let boxSizeX = 40 / 4;
    let boxSizeY = 40 /8;
    let boxSize = 40; //size 1  = 4:3
    let boxFontSize = 0.9;
    let boxLineHeight = 1.1;
    let boxInputX = 6;
    let boxInputY = 2.5;
    let boxMiddleRowPadding = 0.35;
    let bigFont = false;
    // size to font ratio 44.444;
    // size to line height ratio 36.363;
    // size to input height ratio 16;
    // size to input width ratio 6.666;
    // size to padding ratio 28.57;
    let active = false;

    boundaries.addEventListener("mousedown", createBox);
    boundaries.addEventListener("mousemove", drag, false);
    boundaries.addEventListener("touchmove", drag, false);
    bannerInput.addEventListener("input", updateBannerText, false);
    bannerNextButton.addEventListener("click", nextBanner, false);
    bannerPreviousButton.addEventListener("click", previousBanner, false);
    boxIncreaseButton.addEventListener("click", increaseBox, false);
    boxDecreaseButton.addEventListener("click", decreaseBox, false);
    bigFontInput.addEventListener("click", convertToBigFont, false);
    fontStyleSelect.addEventListener("change", changeStyle, false);

    function changeStyle()
    {
        banner.style.fontFamily = fonts[fontStyleSelect.options[fontStyleSelect.selectedIndex].value];
        for (let i = 0; i < boxes.length; i++)
        {
            boxes[i].getElementsByClassName("draggableBoxMiddleRow")[0]
                .getElementsByClassName("draggableBoxInput")[0]
                .style.fontFamily = fonts[fontStyleSelect.options[fontStyleSelect.selectedIndex].value];
        }
        //console.log("Selected font index: " + fontStyleSelect.options[fontStyleSelect.selectedIndex].value);
    }

    function convertToBigFont()
    {
        bigFont = bigFontInput.checked;
        setBoxSizeButActualSize();
    }

    function setBoxSizeButActualSize()
    {
        boxSizeX = boxSize / 4;
        boxSizeY = boxSize / 8;
        setBoxSize();
    }

    function setBoxSize()
    {
        boxInputY = boxSize / 16;
        boxInputX = boxSize / 6.666;
        boxLineHeight = boxSize / 36.363;
        boxMiddleRowPadding = boxSize / 28.57;

        if (bigFont)
        {
            boxFontSize = boxSize / 31.333;
            boxInputLimit = 9;
        } else
        {
            boxFontSize = boxSize / 44.444;
            boxInputLimit = 29;
        }

        for (let i = 0; i < boxes.length; i++)
        {
            boxes[i].style.width = boxSizeX + 'vw';
            boxes[i].style.height = boxSizeY + 'vw';
            boxes[i].getElementsByClassName("draggableBoxMiddleRow")[0]
                .getElementsByClassName("draggableBoxInput")[0].style.fontSize = boxFontSize + 'vw';
            boxes[i].getElementsByClassName("draggableBoxMiddleRow")[0]
                .getElementsByClassName("draggableBoxInput")[0].style.lineHeight = boxLineHeight + 'vw';
            boxes[i].getElementsByClassName("draggableBoxMiddleRow")[0]
                .getElementsByClassName("draggableBoxInput")[0].style.height = boxInputY + 'vw';
            boxes[i].getElementsByClassName("draggableBoxMiddleRow")[0]
                .getElementsByClassName("draggableBoxInput")[0].style.width = boxInputX + 'vw';
            // boxes[i].getElementsByClassName("draggableBoxMiddleRow")[0]
            //     .style.paddingLeft = boxMiddleRowPadding + 'vw';
            boxSizeInput.value = boxSize - 30;
        }
    }

    function increaseBox()
    {
        if (boxSize < 70)
        {
            boxSize++;
            setBoxSizeButActualSize();
        }
    }

    function decreaseBox()
    {
        if (boxSize > 30)
        {
            boxSize--;
            setBoxSizeButActualSize();
        }
    }

    function selectBanner(selector)
    {
        switch (selector)
        {
            case 0:
                bannerContainer.style.display = 'none';
                banner.style.visibility = 'hidden';
                bannerDesignInput.value = 'None';
                break;

            case 1:
                bannerContainer.style.background = 'url(' + bannerPath + selector + '.svg)';
                bannerContainer.style.backgroundPosition = 'center';
                bannerContainer.style.backgroundRepeat = 'no-repeat';
                bannerContainer.style.display = 'flex';
                banner.style.visibility = 'visible';
                bannerDesignInput.value = 'Banner Style ' + selector;
        }
    }

    function nextBanner()
    {
        if (bannerSelector === bannerOptions)
        {
            bannerSelector = 0;
        } else
        {
            bannerSelector++;
        }
        selectBanner(bannerSelector);
    }

    function previousBanner()
    {
        if (bannerSelector === 0)
        {
            bannerSelector = bannerOptions;
        } else
        {
            bannerSelector--;
        }
        selectBanner(bannerSelector);
    }

    function updateBannerText()
    {
        banner.textContent = bannerInput.value;
        if (bannerInput.value === "")
        {
            banner.textContent = localeFamilyTree;
        }
    }

    // creates a box on the cursor coordinates
    function createBox(e)
    {
        if (e.target === boundaries)
        {

            let clone = boxes[0].cloneNode(true);
            let cursorX = pixelsToViewportWidth(e.clientX);
            let cursorY = pixelsToViewportWidth(e.clientY);
            const scrollOffsetX = pixelsToViewportWidth(window.scrollX);
            const scrollOffsetY = pixelsToViewportWidth(window.scrollY);
            clone.getElementsByClassName("creationCursorX")[0].value = parseInt(cursorX) + parseInt(scrollOffsetX);
            clone.getElementsByClassName("creationCursorY")[0].value = parseInt(cursorY) + parseInt(scrollOffsetY);


            constructBox(clone, cursorX, cursorY, false);
        }
    }

    function constructBox(clone, cursorX, cursorY, isGenerated)
    {
        // box style
        clone.style.display = 'flex';
        clone.style.flexWrap = 'wrap';
        clone.style.position = 'absolute';
        clone.style.width = boxSizeX + 'vw';
        clone.style.height = boxSizeY + 'vw';
        //clone.style.top = '0vh';
        //clone.style.left = '0vw';

        const scrollOffsetX = pixelsToViewportWidth(window.scrollX);
        const scrollOffsetY = pixelsToViewportWidth(window.scrollY);
        let parentX = pixelsToViewportWidth(boundaries.offsetLeft);
        let parentY = pixelsToViewportWidth(boundaries.offsetTop);
        let offsetX = boxSizeX / 2;
        let offsetY = boxSizeY / 2;
        // console.log("%cScroll Offset Left: " + scrollOffsetX, "color:mediumpurple");
        // console.log("%cScroll Offset Top: " + scrollOffsetY, "color:mediumpurple");
        if (!isGenerated)
        {
            if (!isWithinInnerBoundaries(boundaries, cursorX, cursorY, boxSizeX, boxSizeY))
            {
                console.log("Outside of bouncries")
                return;
            }
        }


        // create the box at the cursor coordinates
        boundaries.appendChild(clone);
        //clone.style.left = pixelsToViewportWidth(cursorX - parentX - offsetX) + 'vw';
        //clone.style.top = pixelsToViewportHeight(cursorY - parentY - offsetY)  + 'vh';
        setTranslate(cursorX - parentX - offsetX + parseInt(scrollOffsetX),
            cursorY - parentY - offsetY + parseInt(scrollOffsetY), clone);
        clone.xOffset = cursorX - parentX - offsetX + parseInt(scrollOffsetX);
        console.log("xOffset : " + clone.xOffset + " CursorX : " + cursorX + " ParentX : " + parentX + " OffsetX : " + offsetX);
        clone.yOffset = cursorY - parentY - offsetY  + parseInt(scrollOffsetY);
        console.log("yOffset : " + clone.yOffset + " CursorY : " + cursorY + " ParentY : " + parentY + " OffsetY : " + offsetY);

        clone.style.background = assignBoxBackground(images);
        clone.style.backgroundSize = '100% 100%';

        // add events listeners on the drag button
        let button = clone.getElementsByClassName("dragDraggableBoxButton")[0];
        // touch input
        button.addEventListener("touchstart", dragStart, false);
        button.addEventListener("touchend", dragEnd, false);
        // mouse input
        button.addEventListener("mousedown", dragStart, false);
        button.addEventListener("mouseup", dragEnd, false);
    }


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
                console.log("TouchStart Coords :");
                activeItem.initialX = pixelsToViewportWidth(e.touches[0].clientX) - activeItem.xOffset;
                activeItem.initialY = pixelsToViewportWidth(e.touches[0].clientY) - activeItem.yOffset;
                console.log("X: " + activeItem.initialX);
                console.log("Y: " + activeItem.initialY);
            } else
            {
                // console.log("Drag start: xOffset: " + activeItem.xOffset);
                // console.log("Drag start: yOffset: " + activeItem.yOffset);

                activeItem.initialX = pixelsToViewportWidth(e.clientX) - activeItem.xOffset
                activeItem.initialY = pixelsToViewportWidth(e.clientY) - activeItem.yOffset;
                // console.log("Drag start: clientX: " + pixelsToViewportWidth(e.clientX));
                // console.log("Drag start: clientY: " + pixelsToViewportWidth(e.clientY));
                // console.log("Drag start: initialX: " + activeItem.initialX);
                // console.log("Drag start: initialY: " + activeItem.initialY);
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
                console.log("Touchmove Coords : ");
                e.preventDefault();

                activeItem.currentX = pixelsToViewportWidth(e.touches[0].clientX) - activeItem.initialX;
                activeItem.currentY = pixelsToViewportWidth(e.touches[0].clientY) - activeItem.initialY;
                console.log("X: " + activeItem.currentX);
                console.log("Y: " + activeItem.currentY);
            } else
            {
                //console.log("Initials: " + activeItem.initialX + window.scrollX + ', ' + activeItem.initialY);
                activeItem.currentX = pixelsToViewportWidth(e.clientX) - activeItem.initialX;
                activeItem.currentY = pixelsToViewportWidth(e.clientY) - activeItem.initialY;
            }

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
                //console.log("Outside of drag bouncries")
                return;
            }

                setTranslate(activeItem.currentX,
                    activeItem.currentY, activeItem);
        }
    }


function isWithinInnerBoundaries(boundaries, cursorX, cursorY, boxSizeX, boxSizeY)
{
    const scrollOffsetX = pixelsToViewportWidth(window.scrollX);
    const scrollOffsetY = pixelsToViewportWidth(window.scrollY);
    let top = parseInt(pixelsToViewportWidth(boundaries.offsetTop)) + parseInt(boxSizeY) / 2;
    let bottom = parseInt('50') + parseInt(pixelsToViewportWidth(boundaries.offsetTop)) - parseInt(boxSizeY) / 2;
    let left = parseInt(pixelsToViewportWidth(boundaries.offsetLeft)) + parseInt(boxSizeX) / 2;
    let right = parseInt('50') + parseInt(pixelsToViewportWidth(boundaries.offsetLeft)) - parseInt(boxSizeX) / 2;

    console.log ("CursorY + scrollOffset " + parseInt(cursorY + scrollOffsetY));
    console.log ("CursorX + scrollOffset " + parseInt(cursorX + scrollOffsetX));
    console.log ("Boundaries : Top = " + top + ", Bottom = " + bottom + ", Left = " + left + ", Right = " + right);

    return !(!(parseInt(cursorY) + parseInt(scrollOffsetY) > top &&
            parseInt(cursorY) + parseInt(scrollOffsetY) < bottom)
        ||
        !(parseInt(cursorX) + parseInt(scrollOffsetX) > left &&
            parseInt(cursorX) + parseInt(scrollOffsetX) < right)
    )
}

function isWithinOuterBoundaries(boundaries, currentX, currentY, boxSizeX, boxSizeY)
{
    const scrollOffsetX = pixelsToViewportWidth(window.scrollX);
    const scrollOffsetY = pixelsToViewportWidth(window.scrollY);
    //let top = parseInt(pixelsToViewportWidth(boundaries.offsetTop));
    //console.log(parseInt(boxSizeY));
    let top = 0;
    let bottom = parseInt('50') - parseInt(boxSizeY);
    //let left = parseInt(pixelsToViewportWidth(boundaries.offsetLeft));
    let left = 0;
    let right = parseInt('50') - parseInt(boxSizeX);

    // console.log("Current Y: " + pixelsToViewportWidth(currentY));
    // console.log("Current Z: " + pixelsToViewportWidth(currentX));
    // console.log("Coords: " + top + ' ' +  left + ' ' + right + ' ' + bottom);
    //console.log("Right: " + right);

    return ((currentY > top &&
            currentY < bottom)
        &&
        (currentX > left &&
            currentX < right)
    )
}

