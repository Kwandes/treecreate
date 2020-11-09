function updateHash()
{
    const merchantId = document.getElementById("merchant_id").value.toString();
    const paymentRef = document.getElementById("payment_ref").value.toString();
    const customerRef = document.getElementById("customer_ref").value.toString();
    const amount = document.getElementById("amount").value.toString();
    const currency = document.getElementById("currency").value.toString();
    let test = document.getElementById("test").value.toString();

    // Because fuck my brain, if you want to test you mark test as true and add test to the hash...
    // Thanks nordea, I've spent an hour on this
    if (test === "true")
    {
        test = "test";
    } else
    {
        test = "";
    }

    let combinedString = merchantId + paymentRef + customerRef + amount + currency + test;
    console.log("Combined string: " + combinedString)

    fetch(location.origin + "/getPaymentHash",
        {
            method: "POST",
            headers: {'Content-type': 'text/plain'},
            body: combinedString,
        }
    ).then(response => {
        console.log("Fetching hash has finished, status: " + response.status);
        response.text().then(data => {
            console.log("Fetched data: " + data)
            document.getElementById("hash").value = data;
        })
    })
}