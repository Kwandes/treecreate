async function submitNewsletter()
{
    const email = document.getElementById("emailInput").value;
    if (email === "")
    {
        console.log("The email field is empty");
        showBasketPopup("You cannot add an empty email", true)
        return;
    }

    if (email.match("^\\b[\\w.!#$%&’*+\\/=?^`{|}~-]+@[\\w-]+(?:\\.[\\w-]+)+\\b$") == null)
    {
        console.log("Provided email does not seem to be a valid email");
        showBasketPopup("Provided email does not seem to be a valid email", true)
        return;
    }

    let parameters =
        {
            "email": email
        }

    fetch(location.origin + "/createNewsletter",
        {
            method: "POST",
            headers: {'Content-type': 'application/json'},
            body: JSON.stringify(parameters),
        }).then(response =>
    {
        console.log("%Creating a new newsletter entry finished, status: " + response.status, "color:mediumpurple");
        if (response.status === 200)
        {
            console.log(email + " has been added to the newsletter list");
            showBasketPopup(email + " has been added to the newsletter list", false);
        } else
        {
            console.log(email + " has failed to be added");
            showBasketPopup("An issue occurred when trying to join the newsletter", true);
        }
    });
}