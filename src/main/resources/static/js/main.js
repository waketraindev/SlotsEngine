/* jshint esversion: 6 */
(function () {
    "use strict";
    const appWindow = document.getElementById("appWindow"),
        btnSpin = document.getElementById("btnSpin"),
        btnIncBet = document.getElementById("btnIncrementBet"),
        btnDecBet = document.getElementById("btnDecrementBet"),
        btnDeposit = document.getElementById("btnDeposit"),
        btnWithdraw = document.getElementById("btnWithdraw"),
        lblBalanceAmount = document.getElementById("lblBalanceAmount"),
        lblBetAmount = document.getElementById("lblBetAmount"),
        lblDisplay = document.getElementById("lblDisplay"),
        lblRollResult = document.getElementById("lblRollResultText"),
        lblRollAmount = document.getElementById("lblRollResultAmount"),
        lblVersion = document.getElementById("lblVersion"),
        blkDisplay = document.getElementById("blkDisplay"),
        lblBetStats = document.getElementById("lblBetStats"),
        lblWinStats = document.getElementById("lblWinStats"),
        lblRtpStats = document.getElementById("lblRtpStats");

    const tabPayoutTable = document.getElementById("payoutTable");
    const tabPayoutTableBody = tabPayoutTable.getElementsByTagName("tbody")[0];
    const tabPayoutTableRows = tabPayoutTableBody.getElementsByTagName("tr");

    const betRange = [1,10,15,25,50,100,150,200,250,500,1000,1500,2000,2500,5000,10000,];
    const numFormat = new Intl.NumberFormat("en-US",{});

    let lastSpin = {winAmount: 0,};
    let machineState = {
        balance: 1,betAmount: 1,
    };
    let betPos = 0;

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
            setStatusLabel("Balance",prettyNumber(machineState.balance));
            refreshStats();

        },"/api/load").then(() => appWindow.classList.remove("d-none"));
    }

    function sendCall(callback,path,options) {
        return fetch(path,options).then((rsp) => {
            if (!rsp.ok) {
                throw new Error();
            }
            return rsp;
        }).then((response) => response.json())
            .then((data) => callback(data)).catch((ignored) => {
                window.alert(`Error running API call`);
                //console.log(ignored);
            });
    }

    function setStatusLabel(label,text,classes) {
        if (classes === undefined) {
            classes = "text-bg-info";
        }
        lblRollResult.className = `badge ${classes}`;
        lblRollResult.innerText = label;
        lblRollAmount.innerText = text;
    }

    function refreshStats() {
        sendCall((data) => {
            if (data === undefined) {
                data = {betStats: {count: 0,max: 0,sum: 0,},winStats: {count: 0,max: 0,sum: 0,},rtp: 0,};
            }
            let newText = `Bets: ${prettyNumber(data.betStats.count)} `;
            newText += `Max: ${prettyNumber(data.betStats.max)} `;
            newText += `Sum: ${prettyNumber(data.betStats.sum)} `;
            lblBetStats.innerText = newText;

            newText = `Wins: ${prettyNumber(data.winStats.count)} `;
            newText += `Max: ${prettyNumber(data.winStats.max)} `;
            newText += `Sum: ${prettyNumber(data.winStats.sum)} `;
            lblWinStats.innerText = newText;
            lblRtpStats.innerText = `RTP: ${(data.rtp * 100.0).toFixed(2)}%`;
        },"/api/machine-stats",{}).then();
    }

    function setButtonsState(state) {
        [btnSpin,btnIncBet,btnDecBet,btnDeposit,btnWithdraw,].forEach((i) => i.disabled = state);
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
        const tabBody = document.querySelector("#historyTable tbody");
        const rows = tabBody.getElementsByTagName("tr");
        if (rows.length > 10) {
            tabBody.querySelector("tr:last-child").remove();
        }
        const newRow = document.createElement("tr");
        newRow.innerHTML = `<td>${prettyNumber(state.betAmount)}</td>
<td>${prettyNumber(state.winAmount)}</td>
<td>${state.result}</td>`;
        newRow.innerHTML += `<td><span class="badge 
${isWin() ? "text-bg-success" : "text-bg-danger"}">
${isWin() ? "Win" : "Loss"}</span></td>`;
        tabBody.prepend(newRow);

        if (isWin() > 0) {
            setStatusLabel("WIN",prettyNumber(state.winAmount),"text-bg-success");
            blkDisplay.className = "animate-spin-win";
        } else {
            setStatusLabel("LOSS",prettyNumber(state.betAmount),"text-bg-danger");
            blkDisplay.className = "animate-spin-loss";
        }
        setButtonsState(false);
        btnSpin.disabled = machineState.betAmount > machineState.balance;
        setTimeout(refreshStats,0);
    }

    function spin() {
        setButtonsState(true);
        blkDisplay.className = "animate-spin";
        const betAmount = machineState.betAmount;
        setStatusLabel("Spin",prettyNumber(machineState.betAmount),"text-bg-warning");

        let count = 0;
        const animateDisplay = setInterval(() => {
            if (count++ < 7) {
                lblDisplay.innerText = Math.floor(Math.random() * 10).toFixed(0);
            } else {
                sendCall((data) => {
                    clearInterval(animateDisplay);
                    updateMachineState(data);
                },`/api/spin/${betAmount}`,{
                    method: "POST",
                }).then();
            }
        },47);
    }

    function calcBetValues() {
        let i;
        for (i = 0; i < tabPayoutTableRows.length; i++) {
            const cells = tabPayoutTableRows[i].getElementsByTagName("td");
            const value = cells[1];
            value.innerText = prettyNumber(((i >= 10) ? 100 : i) * machineState.betAmount);
        }
    }

    function bindListeners() {
        btnDeposit.addEventListener("click",() => {
            const value = window.prompt("Enter deposit amount","1000");
            sendCall((data) => {
                lblBalanceAmount.innerText = data.balance;
                machineState.balance = data.balance;
                btnSpin.disabled = machineState.betAmount > machineState.balance;
            },"/api/deposit/" + value,{
                method: "POST",
            }).then();

        });
        btnWithdraw.addEventListener("click",() => {
            const value = window.prompt("Enter withdrawal amount","1000");
            sendCall((data) => {
                lblBalanceAmount.innerText = data.balance;
                machineState.balance = data.balance;
                btnSpin.disabled = machineState.betAmount > machineState.balance;
            },"/api/withdraw/" + value,{
                method: "POST",
            }).then();
        });
        btnSpin.addEventListener("click",() => {
            spin();
        });
        btnIncBet.addEventListener("click",() => {
            betPos = Math.min((betPos + 1),betRange.length - 1);
            machineState.betAmount = betRange[betPos];
            lblBetAmount.innerText = prettyNumber(machineState.betAmount);
            calcBetValues();
            btnSpin.disabled = machineState.betAmount > machineState.balance;
        });

        btnDecBet.addEventListener("click",() => {
            betPos = Math.max((betPos - 1),0);
            machineState.betAmount = betRange[betPos];
            lblBetAmount.innerText = prettyNumber(machineState.betAmount);
            calcBetValues();
            const isEnabled = (machineState.balance > machineState.betAmount);
            /* This needs negation for the correct behaviour */
            btnSpin.disabled = !isEnabled;
        });

        document.addEventListener("keydown",(e) => {
            switch (e.key) {
                case "s":
                    btnSpin.click();
                    break;
                case "a":
                    btnIncBet.click();
                    break;
                case "d":
                    btnDecBet.click();
                    break;
            }
        });
    }

    (() => window.onload = initApp)();
})();