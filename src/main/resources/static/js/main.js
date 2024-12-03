let appwindow = document.getElementById('appwin');
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

btnSpin.addEventListener('click', () => {
    console.log("Spin");
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