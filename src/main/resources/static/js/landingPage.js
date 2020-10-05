function submitPopup()
{
    var popup = document.getElementById("landingPagePopup");
    var emailInput = document.getElementById("emailInput");
    var emailValue = emailInput.value.toString();

    // To be replaced with proper regex validation check once that is implemented
    if (emailValue !== '' && emailValue.includes('@'))
    {
        emailInput.value = '';

        popup.classList.toggle("show");
        setTimeout(() => {popup.classList.toggle("hide")}, 2000);
        setTimeout(() => {popup.classList.toggle("disable")}, 2900);

        uploadEmail(emailValue);
    }
}

function uploadEmail(email)
{
    fetch("http://localhost:5000/submitNewsletterEmail",
        {
            method: "POST",
            body: JSON.stringify(email),
            headers:
                {
                'Content-type': 'application/json; charset=UTF-8'
                }
            }
    )
        .then(response => response.json())
        .then(json =>
        {
            console.log(json);
        })
}