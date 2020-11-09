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
    setTimeout(() =>
    {
        popupContainer.style.animation = 'fadeOut 1s';
    }, 3000);

    setTimeout(() =>
    {
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
    console.log("Checking login status...");
    fetch(location.origin + "/isLoggedIn")
        .then(async response =>
        {
            console.log("%cFetching login info finished, status: " + response.status, "color:mediumpurple");
            response.json().then(data =>
            {
                if (data.toString() === 'true')
                {
                    console.log("User is already logged in - changing Login button to Profile")
                }
                setLoginStatus(data.toString() === 'true');
            });

            // Only trigger once isLoggedIN fetch has finished
            console.log("Validating cookies...");
            await validateCookies();
            console.log("Updating the basket...");
            await updateBasket();
        });
}

function closeLoginModal()
{
    $('#loginModal').modal('hide');
}

function closeSignUpModal()
{
    $('#signUpModal').modal('hide');
}

async function registerUser()
{
    console.log("Registration of a new user started");
    const signUpEmail = document.getElementById("signUpEmail").value.toString();
    const signUpPassword = document.getElementById("signUpPassword").value.toString();
    const signupConfirmPassword = document.getElementById("signupConfirmPassword").value.toString();

    if (signUpEmail === '')
    {
        console.log('yeet')
        showSignupPopup('Email is required to register a new account', true);
        return;
    } else if (signUpPassword === '')
    {
        showSignupPopup('Password is required to register a new account', true);
        return;
    }

    if (signUpPassword !== signupConfirmPassword)
    {
        console.log("The passwords don't match");
        showSignupPopup("The passwords don't match", true);
        return;
    }

    if (signUpEmail.match("^\\b[\\w.!#$%&’*+\\/=?^`{|}~-]+@[\\w-]+(?:\\.[\\w-]+)+\\b$") == null)
    {
        console.log("Provided email does not seem to be a valid email");
        showSignupPopup("Provided email does not seem to be a valid email", true)
        return;
    }
    let result = await submitNewUser(signUpEmail, signUpPassword);

    if (result)
    {
        console.log("Registration successful, you're logged in");
        document.getElementById("signUpModalCloseBtn").click();
        showSignupPopup('Thanks for joining Treecreate!', false);
        setLoginStatus(true);
        updateBasket();
    } else
    {
        showSignupPopup('An account with this email already exists. Try again', true);
    }
}

async function submitNewUser(loginEmail, loginPassword)
{
    let parameters =
        {
            "email": loginEmail,
            "password": loginPassword
        };
    const response = await fetch(location.origin + "/addUser",
        {
            method: "POST",
            headers: {'Content-type': 'application/json'},
            body: JSON.stringify(parameters),
        }
    )
    console.log("%cRegistration submission finished, status: " + response.status, "color:mediumpurple");
    return response.status === 200;
}

function showSignupPopup(text, errorPopup)
{
    let popupContainer
    let popup;
    let popupText;
    if (errorPopup)
    {
        popupContainer = document.getElementById("signupPopupModalContainer");
        popup = document.getElementById("signupPopupModal");
        popupText = document.getElementById("signupPopupModalText");

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
    setTimeout(() =>
    {
        popupContainer.style.animation = 'fadeOut 1s';
    }, 3000);

    setTimeout(() =>
    {
        popupContainer.style.display = 'none'
    }, 4000);
}
