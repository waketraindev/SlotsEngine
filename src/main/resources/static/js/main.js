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
    balance: 1, betAmount: 1
};

let betRange = [1, 10, 15, 25, 50, 100, 200, 500, 1000, 2000, 5000, 10000];
let betPos = 0;

function setButtonsState(state) {
    [btnSpin, btnIncBet, btnDecBet, btnDeposit, btnWithdraw].forEach((i) => i.disabled = state);
}

function spin() {
    setButtonsState(true);
    let count = 0;
    let animateDisplay = setInterval(() => {
        if (count++ < 7) {
            lblDisplay.innerText = Math.floor(Math.random() * 10).toFixed(0)
        } else {
            fetch('/api/spin/' + machineState.betAmount, {
                method: 'POST'
            }).then(response => response.json()).then(data => {
                clearInterval(animateDisplay);
                lastSpin = data;
                lblBetAmount.innerText = data.betAmount;
                lblBalanceAmount.innerText = data.balance;
                lblDisplay.innerText = data.result;
                machineState.balance = data.balance;
                setButtonsState(false);
                btnSpin.disabled = machineState.betAmount > machineState.balance;

                let tabBody = document.querySelector("#historyTable tbody");
                let rows = tabBody.getElementsByTagName("tr");
                if (rows.length > 10) tabBody.querySelector("tr:last-child").remove();
                let newRow = document.createElement('tr');
                newRow.innerHTML = `<td>${data.betAmount}</td><td>${data.winAmount}</td><td>${data.result}</td>` +
                    `<td><span class="badge ${data.winAmount > 0 ? 'text-bg-success' : 'text-bg-danger'}">${data.winAmount > 0 ? 'Win' : 'Loss'}</span></td>`;
                tabBody.prepend(newRow);
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


// Display UI after loading
window.addEventListener('load', () => {
    fetch('/api/load').then(response => response.json()).then(data => {
        machineState.balance = data.balance;
        machineState.betAmount = 1;
        lblBalanceAmount.innerText = data.balance;
        lblBetAmount.innerText = data.betAmount;
        lblDisplay.innerText = data.result;
        btnSpin.disabled = machineState.betAmount > machineState.balance;
    }).then(() => appwindow.classList.remove('d-none'));
})