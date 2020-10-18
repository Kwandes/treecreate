function submitPopup()
{
    var popup = document.getElementById("popupContainer");
    var emailInput = document.getElementById("emailInput");
    var emailValue = emailInput.value.toString();

    // To be replaced with proper regex validation check once that is implemented
    if (emailValue !== '' && emailValue.includes('@'))
    {
        emailInput.value = '';

        popup.style.display = 'block';
        popup.style.animation = 'fadeIn 1s';
        setTimeout(() => {
            popup.style.animation = 'fadeOut 1s';
            }, 3000);

        setTimeout(() => {
            popup.style.display = 'none'
        }, 4000);

        uploadEmail(emailValue);
    }
}

function uploadEmail(email)
{
    fetch(location.origin + "/submitNewsletterEmail",
        {
            method: "POST",
            body: email,
            headers:
                {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }
    )
        .then(response => response.json());
}