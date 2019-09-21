function onConsentClick(event) {
    if(event.target.checked) {
        document.getElementById("mc-embedded-subscribe").disabled = false;
    } else {
        document.getElementById("mc-embedded-subscribe").disabled = true;
    }
}