/* jshint esversion: 6 */
(() => {
    "use strict";
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

    let lblVersion = document.getElementById('lblVersion');

    let lastSpin = {winAmount: 0};

    let machineState = {
        balance: 1, betAmount: 1
    };

    let betRange = [1, 10, 15, 25, 50, 100, 200, 500, 1000, 2000, 5000, 10000];
    let betPos = 0;

    let numFormat = new Intl.NumberFormat('en-US', {});

    function prettyNumber(num) {
        return numFormat.format(num);
    }

    function initApp() {
        sendCall((data) => {
            bindListeners();
            machineState.balance = data.balance;
            machineState.betAmount = 1;
            lblBalanceAmount.innerText = prettyNumber(data.balance);
            lblBetAmount.innerText = prettyNumber(data.betAmount);
            lblDisplay.innerText = prettyNumber(data.result);
            lblVersion.innerText = data.version;
            btnSpin.disabled = machineState.betAmount > machineState.balance;
            setStatusLabel('Balance', prettyNumber(machineState.balance));
            refreshStats();

        }, '/api/load').then(() => appWindow.classList.remove('d-none'));
    }

    function sendCall(callback, path, options) {
        return fetch(path, options).then((rsp) => {
            if (!rsp.ok) {
                throw new Error();
            }
            return rsp;
        }).then((response) => response.json())
            .then(data => callback(data)).catch(ignored => window.alert(`Error running API call`));
    }

    function setStatusLabel(label, text, classes) {
        if (classes === undefined) {
            classes = 'text-bg-info';
        }
        lblRollResult.className = `badge ${classes}`;
        lblRollResult.innerText = label;
        lblRollAmount.innerText = text;
    }

    function refreshStats() {
        let lblBetStats = document.getElementById("lblBetStats");
        let lblWinStats = document.getElementById("lblWinStats");
        let lblRtpStats = document.getElementById("lblRtpStats");

        sendCall((data) => {
            let newText = `Bets: ${prettyNumber(data["betStats"]["count"])} `;
            newText += `Max: ${prettyNumber(data["betStats"]["max"])} `;
            newText += `Sum: ${prettyNumber(data["betStats"]["sum"])} `;
            lblBetStats.innerText = newText;

            newText = `Wins: ${prettyNumber(data["winStats"]["count"])} `;
            newText += `Max: ${prettyNumber(data["winStats"]["max"])} `;
            newText += `Sum: ${prettyNumber(data["winStats"]["sum"])} `;
            lblWinStats.innerText = newText;
            lblRtpStats.innerText = `RTP: ${(data["rtp"] * 100.0).toFixed(2)}%`;
        }, "/api/machine-stats", {}).then();
    }

    function setButtonsState(state) {
        [btnSpin, btnIncBet, btnDecBet, btnDeposit, btnWithdraw].forEach((i) => i.disabled = state);
    }

    function isWin() {
        return lastSpin.winAmount > 0;
    }

    function updateMachineState(state) {
        lastSpin = state;
        lblBetAmount.innerText = prettyNumber(state.betAmount);
        lblBalanceAmount.innerText = prettyNumber(state.balance);
        lblDisplay.innerText = prettyNumber(state.result);
        machineState.balance = state.balance;
        setButtonsState(false);
        btnSpin.disabled = machineState.betAmount > machineState.balance;

        let tabBody = document.querySelector("#historyTable tbody");
        let rows = tabBody.getElementsByTagName("tr");
        if (rows.length > 10) {
            tabBody.querySelector("tr:last-child").remove();
        }
        let newRow = document.createElement('tr');
        newRow.innerHTML = `<td>${prettyNumber(state.betAmount)}</td><td>${prettyNumber(state.winAmount)}</td><td>${state.result}</td>` + `<td><span class="badge ${isWin() ? 'text-bg-success' : 'text-bg-danger'}">${isWin() ? 'Win' : 'Loss'}</span></td>`;
        tabBody.prepend(newRow);

        lblDisplay.style.color = isWin() ? 'green' : 'red';

        if (isWin() > 0) {
            setStatusLabel('WIN', prettyNumber(state.winAmount), 'text-bg-success');
        } else {
            setStatusLabel('LOSS', prettyNumber(state.betAmount), 'text-bg-danger');
        }
        setTimeout(refreshStats, 0);
    }

    function spin() {
        setButtonsState(true);
        lblDisplay.style.color = 'orange';
        let betAmount = machineState.betAmount;
        setStatusLabel('Spin', prettyNumber(machineState.betAmount), 'text-bg-warning');

        let count = 0;
        let animateDisplay = setInterval(() => {
            if (count++ < 7) {
                lblDisplay.innerText = Math.floor(Math.random() * 10).toFixed(0);
            } else {
                sendCall(data => {
                    clearInterval(animateDisplay);
                    updateMachineState(data);
                }, `/api/spin/${betAmount}`, {
                    method: 'POST'
                }).then();
            }
        }, 47);
    }

    function calcBetValues() {
        let tb = document.getElementById('payoutTable');
        let body = tb.getElementsByTagName("tbody")[0];
        let rows = body.getElementsByTagName("tr");
        for (let i = 0; i < rows.length; i++) {
            let cells = rows[i].getElementsByTagName("td");
            let value = cells[1];
            value.innerText = prettyNumber(((i >= 10) ? 100 : i) * machineState.betAmount);
        }

    }

    function bindListeners() {
        btnDeposit.addEventListener('click', () => {
            let value = window.prompt("Enter deposit amount", "1000");
            sendCall(data => {
                lblBalanceAmount.innerText = data.balance;
                machineState.balance = data.balance;
                btnSpin.disabled = machineState.betAmount > machineState.balance;
            }, '/api/deposit/' + value, {
                method: 'POST'
            }).then();

        });
        btnWithdraw.addEventListener('click', () => {
            let value = window.prompt("Enter withdrawal amount", "1000");
            sendCall(data => {
                lblBalanceAmount.innerText = data.balance;
                machineState.balance = data.balance;
                btnSpin.disabled = machineState.betAmount > machineState.balance;
            }, '/api/withdraw/' + value, {
                method: 'POST'
            }).then();
        });
        btnSpin.addEventListener('click', () => {
            spin();
        });
        btnIncBet.addEventListener('click', () => {
            betPos = Math.min((betPos + 1), betRange.length - 1);
            machineState.betAmount = betRange[betPos];
            lblBetAmount.innerText = prettyNumber(machineState.betAmount);
            calcBetValues();
            btnSpin.disabled = machineState.betAmount > machineState.balance;
        });

        btnDecBet.addEventListener('click', () => {
            betPos = Math.max((betPos - 1), 0);
            machineState.betAmount = betRange[betPos];
            lblBetAmount.innerText = prettyNumber(machineState.betAmount);
            calcBetValues();
            btnSpin.disabled = !(machineState.balance > machineState.betAmount);
        });

        document.addEventListener('keydown', (e) => {
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
    }

    (() => window.onload = initApp)();
})();