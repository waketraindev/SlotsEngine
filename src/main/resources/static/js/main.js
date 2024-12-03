let btnSpin = document.getElementById('btnSpin');
let btnIncBet = document.getElementById('btnIncrementBet');
let btnDecBet = document.getElementById('btnDecrementBet');

document.addEventListener('keyup', (e) => {
    switch (e.key) {
        case 'a':
            btnSpin.click();
            break;
        case 's':
            btnIncBet.click();
            break;
        case 'd':
            btnDecBet.click();
            break;
    }
});