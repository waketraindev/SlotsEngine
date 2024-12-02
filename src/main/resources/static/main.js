let SlotControls = {
    eventSource: null,
    spin: function () {
        console.log('Spin button clicked!');
        fetch("/api/debugspin").then(response => response.text()).then(data => console.log(data));
    },

    watchLive: function () {
        this.eventSource = new EventSource('/events');
        this.eventSource.onmessage = this.processMessage;
        this.eventSource.onerror = function (error) {
            console.log('EventSource failed:', error);
        }
    },
    processMessage: function (event) {
        console.log('New message:', event.data);
    }
};

window.onload = SlotControls.watchLive;

let spinButton = document.getElementById('spin');
spinButton.onclick = SlotControls.spin;
