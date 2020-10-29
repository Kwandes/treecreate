async function submitLogin()
{
    const loginEmail = document.getElementById("loginEmail").value.toString();
    const loginPassword = document.getElementById("loginPassword").value.toString();

    if (loginEmail === '')
    {
        showPopup('Email is required to login', true);
        return;
    } else if (loginPassword === '')
    {
        showPopup('Password is required to login', true);
        return;
    }

    let result = await validateCredentials(loginEmail, loginPassword);

    if (result)
    {
        document.getElementById("loginModalCloseBtn").click();
        showPopup('Welcome Back!', false);
        setLoginStatus(true);
        updateBasket();
    } else
    {
        showPopup('The credentials are invalid. Try again', true);
    }
}

async function validateCredentials(email, password)
{
    let parameters =
        {
            "email": email,
            "password": password
        };
    const response = await fetch(location.origin + "/submitLogin",
        {
            method: "POST",
            headers: {'Content-type': 'application/json'},
            body: JSON.stringify(parameters),
        }
    )
    console.log("Credentials Validation fetching finished, status: " + response.status);
    return response.status === 200;
}

function showPopup(text, errorPopup)
{
    let popupContainer
    let popup;
    let popupText;
    if (errorPopup)
    {
        popupContainer = document.getElementById("popupModalContainer");
        popup = document.getElementById("popupModal");
        popupText = document.getElementById("popupModalText");

        popup.style.backgroundColor = 'var(--popupErrorColor)';
    } else
    {
        popupContainer = document.getElementById("popupContainer");
        popup = document.getElementById("popup");
        popupText = document.getElementById("popupText");

        popup.style.backgroundColor = 'var(--popupColor)';
    }

    popupText.innerText = text;

    popupContainer.style.display = 'block';
    popupContainer.style.animation = 'fadeIn 1s';
    setTimeout(() => {
        popupContainer.style.animation = 'fadeOut 1s';
    }, 3000);

    setTimeout(() => {
        popupContainer.style.display = 'none'
    }, 4000);
}

function setLoginStatus(isLoggedIn)
{
    if (isLoggedIn)
    {
        document.getElementById("loginButton").style.display = 'none';
        document.getElementById("profileButton").style.display = 'inline-block';

    } else
    {
        document.getElementById("loginButton").style.display = 'inline-block';
        document.getElementById("profileButton").style.display = 'none';
    }
}

function isLoggedIn()
{
    fetch(location.origin + "/isLoggedIn")
        .then(response => {
            response.json().then(data => {
                if (data.toString() === 'true')
                {
                    console.log("User is already logged in - changing Login button to Profile")
                }
                setLoginStatus(data.toString() === 'true');
            });
        });
}