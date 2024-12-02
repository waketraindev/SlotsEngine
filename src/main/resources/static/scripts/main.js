window.onload = (function () {
    startApp();
});

function startApp() {
    console.log("Starting app");
    const spinButton = document.getElementById("spinButton");
    const spinResult = document.getElementById("spinResult");

    spinButton.addEventListener("click", () => {
        spinResult.innerHTML = "Spinning...";
        fetch("/api/debugspin").then(response => response.text()).then(text => {
            spinResult.innerHTML = text;
            console.log(text);
        })
    });

    const liveEvents = new EventSource("/events");
    liveEvents.onmessage = function () {
        console.log("New message");
        console.log(event);
    };
    liveEvents.onopen = function (event) {
        console.log("Connection opened");
    }
    window.es = liveEvents;
    console.log(liveEvents)

}