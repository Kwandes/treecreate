async function submitLogin()
{
    //const popup = document.getElementById("popupContainer");
    const loginEmail = document.getElementById("loginEmail").value.toString();
    const loginPassword = document.getElementById("loginPassword").value.toString();

    console.log("Credentials: " + loginEmail + " / " + loginPassword);

    if (loginEmail === '')
    {
        console.log("Email was empty");
        showPopup('Email is required to login', true);
        return;
    } else if (loginPassword === '')
    {
        console.log("Password is empty");
        showPopup('Password is required to login', true);
        return;
    }

    let result = await validateCredentials(loginEmail, loginPassword);
    console.log("Result: " + result)

    if (result)
    {
        console.log("You're logged in");
        showPopup('Welcome Back', false);

    } else
    {
        console.log("You have fucked up");
        showPopup('You have fucked up your credentials', true);
    }
}

async function validateCredentials(email, password)
{
    let parameters =
        {
            "email": email,
            "password": password
        };
    const response = await fetch("http://localhost:5000/submitLogin",
        {
            method: "POST",
            headers: {'Content-type': 'application/json'},
            body: JSON.stringify(parameters),
        }
    )
    console.log("Fetching finished, status: " + response.status);
    return response.status === 200;
}

function showPopup(text, errorPopup)
{
    const popupContainer = document.getElementById("popupContainer");
    const popup = document.getElementById("popup");
    const popupText = document.getElementById("popupText");

    popupText.innerText = text;

    if (errorPopup)
    {
        popup.style.backgroundColor = 'var(--popupErrorColor)';
    } else popup.style.backgroundColor = 'var(--popupColor)';

    popupContainer.style.display = 'block';
    popupContainer.style.animation = 'fadeIn 1s';
    setTimeout(() => {
        popupContainer.style.animation = 'fadeOut 1s';
    }, 3000);

    setTimeout(() => {
        popupContainer.style.display = 'none'
    }, 4000);
}