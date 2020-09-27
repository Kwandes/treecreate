function deleteBox(button)
{
    button.parentNode.parentNode.removeChild(button.parentNode);
}

window.onload = function ()
{
    var boundaries = document.getElementById("productContainer");
    var spawnzone = document.getElementById("draggableBoxSpawnzone");
    var boxes = document.getElementsByClassName("draggableBox");

    var container = document.querySelector("#draggableBoxContainer");
    var activeItem = null;

    var active = false;

    container.addEventListener("touchstart", dragStart, false);
    container.addEventListener("touchend", dragEnd, false);
    container.addEventListener("touchmove", drag, false);

    container.addEventListener("mousedown", dragStart, false);
    container.addEventListener("mouseup", dragEnd, false);
    container.addEventListener("mousemove", drag, false);

    boundaries.addEventListener("click", createBox);

    function createBox(e) {
        // limit has to be increased by 1, as the very first box is invisible and cannot be interacted with
        if (boxes.length <= 26)
        {
            if (e.target === boundaries)
            {
                clone = boxes[0].cloneNode(true)

                clone.style.top = 50 + '%';
                clone.style.left = 50 + '%';
                clone.style.display = 'flex';
                clone.style.position = 'absolute';
                clone.style.height = 250 + 'px';
                clone.style.width = 500 + 'px';

                spawnzone.appendChild(clone);

            }
        }
        else
        {
            alert("Cannot exceed limit of 25 boxes.");
        }

    }

    function dragStart(e) {

        if (e.target !== e.currentTarget)
        {
            active = true;

            // this is the item we are interacting with
            activeItem = e.target;

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
        }
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
