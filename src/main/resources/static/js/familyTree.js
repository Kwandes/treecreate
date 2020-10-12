function viewportToPixels(value) {
    var parts = value.match(/([0-9\.]+)(vh|vw)/)
    var q = Number(parts[1])
    var side = window[['innerHeight', 'innerWidth'][['vh', 'vw'].indexOf(parts[2])]]
    return side * (q/100)
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

window.onload = function () {
    const imgPath = "../img/images/boxDesign/box01/";
    const images = getBoxImageSources(9, imgPath);
    const boundaries = document.getElementById("draggableBoxContainer");
    const boxes = document.getElementsByClassName("draggableBox");
    let activeItem = null;
    let active = false;

    boundaries.addEventListener("mousedown", createBox);
    boundaries.addEventListener("mousemove", drag, false);

    // creates a box on the cursor coordinates
    function createBox(e)
    {
        if (e.target === boundaries)
        {
            // limit has to be increased by 1, as the very first box is invisible and
            // cannot be interacted with
            if (boxes.length <= 26)
            {
                let clone = boxes[0].cloneNode(true);

                const boxWidth = 10;
                const boxHeight = 10;

                // box style
                clone.style.display = 'flex';
                clone.style.flexWrap = 'wrap';
                clone.style.position = 'absolute';
                clone.style.width = boxWidth + 'vw';
                clone.style.height = boxHeight + 'vh';

                let cursorX = e.clientX;
                let cursorY = e.clientY;
                let parentX = boundaries.offsetLeft;
                let parentY = boundaries.offsetTop;
                let offsetX = viewportToPixels(boxWidth + 'vw') / 2;
                let offsetY = viewportToPixels(boxHeight + 'vh') / 2;

                // create the box at the cursor coordinates
                boundaries.appendChild(clone);
                clone.style.left = cursorX - parentX - offsetX + 'px';
                clone.style.top = cursorY - parentY - offsetY + 'px';
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
            } else
            {
                alert("Cannot exceed limit of 25 boxes.");
            }
        }
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

            setTranslate(activeItem.currentX, activeItem.currentY, activeItem);
        }
    }

    function setTranslate(xPos, yPos, element)
    {
        element.style.transform = "translate3d(" + xPos + "px, " + yPos + "px, 0)";
    }
}
