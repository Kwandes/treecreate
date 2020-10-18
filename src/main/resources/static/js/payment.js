function updateHash()
{
    const merchantId = document.getElementById("merchant_id").value.toString();
    const paymentRef = document.getElementById("payment_ref").value.toString();
    const customerRef = document.getElementById("customer_ref").value.toString();
    const amount = document.getElementById("amount").value.toString();
    const currency = document.getElementById("currency").value.toString();
    const test = document.getElementById("test").value.toString();

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