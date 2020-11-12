function sendRequest()
{
    const email = document.getElementById("emailInput").value;

    fetch(location.origin + "/specialRequest",
        {
            method: "POST",
            headers: {'Content-type': 'application/json'},
            body: email
        }).then(
        response =>
        {
            if (response.status === 200)
            {
                console.log("The email has been sent");
            } else
            {
                console.log("Something went wrong while sending the email, request status: " + response.status);
            }
        }
    )

    document.getElementById("emailInput").value = "";
    const popup = document.getElementById("submitPopup");
    popup.style.visibility = "visible";
    popup.style.opacity = "1";
    popup.style.top = "auto";
    popup.style.left= "46%";
    popup.style.marginTop = "4vw";

    setTimeout(() => {
        popup.style.opacity = "0";
        popup.style.visibility = "invisible";
    }, 1500);
}