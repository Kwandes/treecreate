function getRandomInt(min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min) + min); //The maximum is exclusive and the minimum is inclusive
}

// triggered by deleteDraggableBoxButton
function deleteBox(button)
{
    button.parentNode.parentNode.removeChild(button.parentNode);
}

function getBoxImageSources(i, path)
{
    var imgArray = new Array();
    for (var j = 1; j <= i; j ++)
    {
        var imgSrc = path + "box" + j + ".svg";
        imgArray.push(imgSrc);
    }
    return imgArray;
}

function assignBoxBackground(imgArray)
{
    var imageNumber = getRandomInt(0, imgArray.length - 1);
    var backgroundSrc = 'url(' + imgArray[imageNumber] + ') 100% 100%';
    return backgroundSrc;
}

window.onload = function ()
{
    var imgPath = "../img/images/boxDesign/box01/";
    var images = getBoxImageSources(9, imgPath);
    var boundaries = document.getElementById("draggableBoxContainer");
    var boxes = document.getElementsByClassName("draggableBox");
    var activeItem = null;
    var active = false;

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
                clone = boxes[0].cloneNode(true);

                var boxWidth = 500;
                var boxHeight = 250;

                // box style
                clone.style.display = 'flex';
                clone.style.position = 'absolute';
                clone.style.width = boxWidth + 'px';
                clone.style.height = boxHeight + 'px';

                var cursorX = e.clientX;
                var cursorY = e.clientY;
                var offsetX = boxWidth / 2;
                var offsetY = boxHeight / 2;

                // create the box at the cursor coordinates
                boundaries.appendChild(clone);
                clone.style.top = cursorY - offsetY + 'px';
                clone.style.left = cursorX - offsetX + 'px';
                //clone.style.backgroundImage = assignBoxBackground(images);
                clone.style.background = assignBoxBackground(images);
                clone.style.backgroundSize = '100% 100%';

                // add events listeners on the drag button
                var button = clone.getElementsByClassName("dragDraggableBoxButton")[0];
                // touch input
                button.addEventListener("touchstart", dragStart, false);
                button.addEventListener("touchend", dragEnd, false);
                // mouse input
                button.addEventListener("mousedown", dragStart, false);
                button.addEventListener("mouseup", dragEnd, false);
            }
            else
            {
                alert("Cannot exceed limit of 25 boxes.");
            }
        }
    }

    function dragStart(e)
    {
        active = true;

        // this is the item we are interacting with
        activeItem = e.target.parentNode;

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
            }
            else
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
            }
            else
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
