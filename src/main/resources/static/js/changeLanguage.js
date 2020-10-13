function changeLanguage()
{
    const cookieName = "Language-Locale";
    let splitCookies = decodeURIComponent(document.cookie).split(';');
    let locale = "dk";
    for (let i = 0; i < splitCookies.length; i++)
    {
        if (splitCookies[i].includes(cookieName))
        {
            locale = splitCookies[i].toString().replace(cookieName + '=', '').trim();
            break;
        }
    }

    if (locale === "dk")
    {
        console.log("Changing locale to 'en'");
        document.cookie = cookieName + '=en';
    } else
    {
        console.log("Changing locale to 'dk'");
        document.cookie = cookieName + '=dk';
    }
    location.reload();
}