import {MainApp} from "./app.js";

(function () {
    let app = new MainApp();
    app.start();
    window.app = app;
})();