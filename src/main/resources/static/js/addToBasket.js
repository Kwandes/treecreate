function addToBasket()
{
    console.log("%cI got triggered yay", "color: mediumpurple");
    // TODo - merge with saving of a design
    // ToDo Actually add it to the basket that is persistent per session/user
}

function increaseAmount()
{
    let amountInput = document.getElementById("amountInput");
    let amount = amountInput.value;
    amountInput.value = ++amount;
}

function decreaseAmount()
{
    let amountInput = document.getElementById("amountInput");
    let amount = amountInput.value;
    console.log(amount)
    if (amount > 1)
    {
        amountInput.value = --amount;
    }
}

const sizeOptions = ["20x20 cm", "25x25 cm", "30x30 cm"];

function increaseSize()
{
    let sizeInput = document.getElementById("sizeInput");
    let currentSizeIndex = sizeOptions.indexOf(sizeInput.value)
    if (currentSizeIndex === -1)
    {
        sizeInput.value = sizeOptions[0];
    } else
    {
        if (currentSizeIndex === sizeOptions.length - 1)
        {
            sizeInput.value = sizeOptions[0];
        } else
        {
            sizeInput.value = sizeOptions[++currentSizeIndex];
        }
    }
}

function decreaseSize()
{
    let sizeInput = document.getElementById("sizeInput");
    let currentSizeIndex = sizeOptions.indexOf(sizeInput.value)
    if (currentSizeIndex === -1)
    {
        sizeInput.value = sizeOptions[0];
    } else
    {
        if (currentSizeIndex === 0)
        {
            sizeInput.value = sizeOptions[sizeOptions.length - 1];
        } else
        {
            sizeInput.value = sizeOptions[--currentSizeIndex];
        }
    }
}

