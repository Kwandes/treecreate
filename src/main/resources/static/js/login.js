async function submitLogin()
{
    const loginEmail = document.getElementById("loginEmail").value.toString();
    const loginPassword = document.getElementById("loginPassword").value.toString();

    if (loginEmail === '')
    {
        showPopup(localeLoginSubmitLoginEmail, true);
        return;
    } else if (loginPassword === '')
    {
        showPopup(localeLoginSubmitLoginPassword, true);
        return;
    }

    let result = await validateCredentials(loginEmail, loginPassword);

    if (result)
    {
        document.getElementById("loginModalCloseBtn").click();
        showPopup(localeLoginSubmitLoginValid, false);
        setLoginStatus(true);
        updateBasket();
    } else
    {
        showPopup(localeLoginSubmitLoginInvalid, true);
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
            // these are done sequentially in order to avoid overloading the API and making multiple copies of the same session user
            // Call them in order of which has to show first
            console.log("Validating cookies...");
            await validateCookies();
            console.log("Updating the basket...");
            await updateBasket();
            console.log("Checking the user verification status...")
            await checkVerificationStatus();
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
        console.log('User has not input an Email')
        showSignupPopup(localeLoginRegisterUserEmail, true);
        return;
    } else if (signUpPassword === '')
    {
        console.log('User has not input an Email')
        showSignupPopup(localeLoginRegisterUserPassword, true);
        return;
    }

    if (signUpPassword !== signupConfirmPassword)
    {
        console.log("The passwords don't match");
        showSignupPopup(localeLoginRegisterUserPasswordMismatch, true);
        return;
    }

    if (signUpEmail.match("^\\b[\\w.!#$%&’*+\\/=?^`{|}~-]+@[\\w-]+(?:\\.[\\w-]+)+\\b$") == null)
    {
        console.log("Provided email does not seem to be a valid email");
        showSignupPopup(localeLoginRegisterUserEmailInvalid, true)
        return;
    }
    let result = await submitNewUser(signUpEmail, signUpPassword);

    if (result)
    {
        console.log("Registration successful, you're logged in");
        document.getElementById("signUpModalCloseBtn").click();
        showSignupPopup(localeLoginRegisterUserSuccess, false);
        setLoginStatus(true);
        await updateBasket();
    } else
    {
        showSignupPopup(localeLoginRegisterUserAlreadyExists, true);
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

// Gets current users verification status, and if they are not verified and are logged it, it displays a reminder for them to verify
function checkVerificationStatus()
{
    // Check if the user is logged in
    if (document.getElementById("profileButton").style.display === "none")
    {
        return;
    }

    fetch(location.origin + "/isVerified").then(
        response =>
        {
            console.log("%cFetching verification status finished, status: " + response.status, "color:mediumpurple");
            response.json().then(data =>
            {
                if (data.toString() === 'false')
                {
                    console.log("User is not verified - displaying a verification reminder");
                    document.getElementById("verificationBanner").style.display = "flex";
                }
            });
        }
    )
}

function redirectVerificationPage()
{
    window.open(location.origin + "/verificationGuide", "tab");
}

function sendVerificationEmail()
{
    fetch(location.origin + "/sendVerificationEmail").then(
        response =>
        {
            if (response.status === 200)
            {
                console.log("The email has been sent");
                showBasketPopup(localeLoginSendVerificationEmailSuccess);
            } else
            {
                console.log("Something went wrong while sending the email, request status: " + response.status);
                showBasketPopup(localeLoginSendVerificationEmailFailure);
            }
        }
    )
}
