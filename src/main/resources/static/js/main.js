let appWindow = document.getElementById('appWindow');
let btnSpin = document.getElementById('btnSpin');
let btnIncBet = document.getElementById('btnIncrementBet');
let btnDecBet = document.getElementById('btnDecrementBet');

let btnDeposit = document.getElementById('btnDeposit');
let btnWithdraw = document.getElementById('btnWithdraw');

let lblBalanceAmount = document.getElementById('lblBalanceAmount');
let lblBetAmount = document.getElementById('lblBetAmount');

let lblDisplay = document.getElementById('lblDisplay');

let lblRollResult = document.getElementById('lblRollResultText');
let lblRollAmount = document.getElementById('lblRollResultAmount');

let lastSpin;

let machineState = {
    balance: 1, betAmount: 1
};

let betRange = [1, 10, 15, 25, 50, 100, 200, 500, 1000, 2000, 5000, 10000];
let betPos = 0;

function initApp() {
    fetch('/api/load').then(response => response.json()).then(data => {
        bindListeners();
        machineState.balance = data.balance;
        machineState.betAmount = 1;
        lblBalanceAmount.innerText = data.balance;
        lblBetAmount.innerText = data.betAmount;
        lblDisplay.innerText = data.result;
        btnSpin.disabled = machineState.betAmount > machineState.balance;
        setStatusLabel('Balance', machineState.balance);
    }).then(() => appWindow.classList.remove('d-none'));
}

function setStatusLabel(label, text, classes) {
    if (classes === undefined) classes = 'text-bg-info';
    lblRollResult.className = `badge ${classes}`;
    lblRollResult.innerText = label;
    lblRollAmount.innerText = text;
}

function setButtonsState(state) {
    [btnSpin, btnIncBet, btnDecBet, btnDeposit, btnWithdraw].forEach((i) => i.disabled = state);
}

function updateMachineState(state) {
    lastSpin = state;
    lblBetAmount.innerText = state.betAmount;
    lblBalanceAmount.innerText = state.balance;
    lblDisplay.innerText = state.result;
    machineState.balance = state.balance;
    setButtonsState(false);
    btnSpin.disabled = machineState.betAmount > machineState.balance;

    let tabBody = document.querySelector("#historyTable tbody");
    let rows = tabBody.getElementsByTagName("tr");
    if (rows.length > 10) tabBody.querySelector("tr:last-child").remove();
    let newRow = document.createElement('tr');
    newRow.innerHTML = `<td>${state.betAmount}</td><td>${state.winAmount}</td><td>${state.result}</td>` + `<td><span class="badge ${state.winAmount > 0 ? 'text-bg-success' : 'text-bg-danger'}">${state.winAmount > 0 ? 'Win' : 'Loss'}</span></td>`;
    tabBody.prepend(newRow);

    lblDisplay.style.color = state.winAmount > 0 ? 'green' : 'red';

    if (state.winAmount > 0)
        setStatusLabel('WIN', state.winAmount, 'text-bg-success');
    else
        setStatusLabel('loss', state.betAmount, 'text-bg-danger');

}

function spin() {
    setButtonsState(true);
    lblDisplay.style.color = '';
    let betAmount = machineState.betAmount;
    setStatusLabel('Spin', machineState.betAmount, 'text-bg-warning');

    let count = 0;
    let animateDisplay = setInterval(() => {
        if (count++ < 7) {
            lblDisplay.innerText = Math.floor(Math.random() * 10).toFixed(0)
        } else {
            fetch(`/api/spin/${betAmount}`, {
                method: 'POST'
            }).then(response => response.json()).then(data => {
                clearInterval(animateDisplay);
                updateMachineState(data);
            })
        }
    }, 150);
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

function calcBetValues() {
    let tb = document.getElementById('payoutTable');
    let body = tb.getElementsByTagName("tbody")[0];
    let rows = body.getElementsByTagName("tr");
    for (let i = 0; i < rows.length; i++) {
        let cells = rows[i].getElementsByTagName("td");
        let value = cells[1];
        value.innerText = ((i >= 10) ? 100 : i) * machineState.betAmount;
    }

}

function bindListeners() {
    btnDeposit.addEventListener('click', () => {
        let value = prompt("Enter deposit amount", "1000");
        fetch('/api/deposit/' + value, {}).then(response => response.json()).then(data => {
            lblBalanceAmount.innerText = data.balance;
            machineState.balance = data.balance;
            btnSpin.disabled = machineState.betAmount > machineState.balance;
        })
    });
    btnWithdraw.addEventListener('click', () => {
        let value = prompt("Enter withdrawal amount", "1000");
        fetch('/api/withdraw/' + value, {}).then(response => response.json()).then(data => {
            lblBalanceAmount.innerText = data.balance;
            machineState.balance = data.balance;
            btnSpin.disabled = machineState.betAmount > machineState.balance;
        })
    });
    btnSpin.addEventListener('click', () => {
        spin();
    });
    btnIncBet.addEventListener('click', () => {
        betPos = Math.min((betPos + 1), betRange.length - 1);
        machineState.betAmount = betRange[betPos];
        lblBetAmount.innerText = machineState.betAmount;
        calcBetValues();
        btnSpin.disabled = machineState.betAmount > machineState.balance;
    });

    btnDecBet.addEventListener('click', () => {
        betPos = Math.max((betPos - 1), 0);
        machineState.betAmount = betRange[betPos];
        lblBetAmount.innerText = machineState.betAmount;
        calcBetValues();
        btnSpin.disabled = !(machineState.balance > machineState.betAmount);
    });
}

(function () {
// Display UI after loading
    window.addEventListener('load', initApp);
})();