function hideCookiePopup()
{
    document.getElementById("cookieCover").style.display = "none";
    $('#cookiePromptModal').modal('hide');
}

function showCookiePopup()
{
    $('#cookiePromptModal').modal();
}

async function getCookiesValidation()
{
    const response = await fetch(location.origin + "/cookiesValidation");
    return await response.text();
}

async function acceptCookies()
{
    const response = await fetch(location.origin + "/acceptCookies");
    hidePopup();
    return await response.text();
}

async function validateCookies()
{
    const response = await getCookiesValidation();
    if (response === "false")
    {
        console.log("Showing Popup");
        showCookiePopup();
    }
    else
    {
        console.log("Hiding Popup");
        hideCookiePopup();
    }
}