let appwindow = document.getElementById('appwin');
let btnSpin = document.getElementById('btnSpin');
let btnIncBet = document.getElementById('btnIncrementBet');
let btnDecBet = document.getElementById('btnDecrementBet');
let lblBalanceAmount = document.getElementById('lblBalanceAmount');
let lblBetAmount = document.getElementById('lblBetAmount');

let lastSpin;

function spin() {
    fetch('/api/spin').then(response => response.json()).then(data => {
        lastSpin = data;
        lblBetAmount.innerText = data.betAmount;
        lblBalanceAmount.innerText = data.balance;
    })
}

document.addEventListener('keyup', (e) => {
    switch (e.key) {
        case 's':
            btnSpin.click();
            break;
        case 'a':
            btnIncBet.click();
            break;
        case 'd':
            btnDecBet.click();
            break;
    }
});

btnSpin.addEventListener('click', () => {
    console.log("Spin");
    spin();
});
btnIncBet.addEventListener('click', () => {
    console.log("Increment");
});
btnDecBet.addEventListener('click', () => {
    console.log("Decrement");
});

// Display UI after loading
window.addEventListener('load', () => {
    appwindow.classList.remove('d-none');
})