window.onload = function ()
{
    if (screen.width <= 699)
        {
            location.replace("/mobile.html");
        }
    if ((navigator.userAgent.match(/iPhone/i)) || (navigator.userAgent.match(/iPod/i)))
        {
            location.replace("/mobile.html");
        }
}