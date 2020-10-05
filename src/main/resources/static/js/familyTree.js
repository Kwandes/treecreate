function deleteBox(button)
{
    button.parentNode.parentNode.removeChild(button.parentNode);
}

window.onload = function ()
{
    var boundaries = document.getElementById("draggableBoxContainer");
    var spawnzone = document.getElementById("draggableBoxContainer");
    var boxes = document.getElementsByClassName("draggableBox");

    var container = document.querySelector("#draggableBoxContainer");
    var activeItem = null;

    var active = false;

    boundaries.addEventListener("click", createBox);

    function createBox(e)
    {
        if (e.target === boundaries)
        {
            // limit has to be increased by 1, as the very first box is invisible and cannot be interacted with
            if (boxes.length <= 26)
            {
                clone = boxes[0].cloneNode(true)

                var boxWidth = 500;
                var boxHeight = 250;

                var cursorX = e.clientX;
                var cursorY = e.clientY;
                var offsetX = boxWidth / 2;
                var offsetY = boxHeight / 2;

                clone.style.display = 'flex';
                clone.style.position = 'absolute';
                clone.style.width = boxWidth + 'px';
                clone.style.height = boxHeight + 'px';

                spawnzone.appendChild(clone);
                clone.style.top = cursorY - offsetY + 'px';
                clone.style.left = cursorX - offsetX + 'px';

                var button = clone.getElementsByClassName("dragDraggableBoxButton")[0];

                console.log(button.className);
                button.addEventListener("touchstart", dragStart, false);
                button.addEventListener("touchend", dragEnd, false);
                button.addEventListener("touchmove", drag, false);

                button.addEventListener("mousedown", dragStart, false);
                button.addEventListener("mouseup", dragEnd, false);
                button.addEventListener("mousemove", drag, false);

            }
            else
            {
                alert("Cannot exceed limit of 25 boxes.");
            }
        }
    }

    function dragStart(e) {
        //if (e.target !== e.currentTarget)
        //{
            active = true;

            // this is the item we are interacting with
            activeItem = e.target.parentNode;
            console.log(activeItem.className);

            if (activeItem !== null) {
                if (!activeItem.xOffset) {
                    activeItem.xOffset = 0;
                }

                if (!activeItem.yOffset) {
                    activeItem.yOffset = 0;
                }

                if (e.type === "touchstart") {
                    activeItem.initialX = e.touches[0].clientX - activeItem.xOffset;
                    activeItem.initialY = e.touches[0].clientY - activeItem.yOffset;
                } else {
                    activeItem.initialX = e.clientX - activeItem.xOffset;
                    activeItem.initialY = e.clientY - activeItem.yOffset;
                }
            }
        //}
    }

    function dragEnd(e) {
        if (activeItem !== null) {
            activeItem.initialX = activeItem.currentX;
            activeItem.initialY = activeItem.currentY;
        }

        active = false;
        activeItem = null;
    }

    function drag(e) {
        if (active) {
            if (e.type === "touchmove") {
                e.preventDefault();

                activeItem.currentX = e.touches[0].clientX - activeItem.initialX;
                activeItem.currentY = e.touches[0].clientY - activeItem.initialY;
            } else {
                activeItem.currentX = e.clientX - activeItem.initialX;
                activeItem.currentY = e.clientY - activeItem.initialY;
            }

            activeItem.xOffset = activeItem.currentX;
            activeItem.yOffset = activeItem.currentY;

            setTranslate(activeItem.currentX, activeItem.currentY, activeItem);
        }
    }

    function setTranslate(xPos, yPos, el) {
        el.style.transform = "translate3d(" + xPos + "px, " + yPos + "px, 0)";
    }
}
