let appwindow = document.getElementById('appwin');
let btnSpin = document.getElementById('btnSpin');
let btnIncBet = document.getElementById('btnIncrementBet');
let btnDecBet = document.getElementById('btnDecrementBet');

let btnDeposit = document.getElementById('btnDeposit');
let btnWithdraw = document.getElementById('btnWithdraw');

let lblBalanceAmount = document.getElementById('lblBalanceAmount');
let lblBetAmount = document.getElementById('lblBetAmount');

let lblDisplay = document.getElementById('lblDisplay');

let lastSpin;

let machineState = {
    balance: 0, betAmount: 1
};

function spin() {
    fetch('/api/spin/' + machineState.betAmount, {
        method: 'POST'
    }).then(response => response.json()).then(data => {
        lastSpin = data;
        lblBetAmount.innerText = data.betAmount;
        lblBalanceAmount.innerText = data.balance;
        lblDisplay.innerText = data.result;
        console.log(data);
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

btnDeposit.addEventListener('click', () => {
    let value = prompt("Enter deposit amount", 1000);
})
btnWithdraw.addEventListener('click', () => {
    let value = prompt("Enter withdrawal amount", 1000);
})
btnSpin.addEventListener('click', () => {
    spin();
});
btnIncBet.addEventListener('click', () => {
    machineState.betAmount += 1;
    lblBetAmount.innerText = machineState.betAmount;
});
btnDecBet.addEventListener('click', () => {
    if (machineState.betAmount > 1) {
        machineState.betAmount -= 1;
        lblBetAmount.innerText = machineState.betAmount;
    }
});


// Display UI after loading
window.addEventListener('load', () => {
    appwindow.classList.remove('d-none');
})