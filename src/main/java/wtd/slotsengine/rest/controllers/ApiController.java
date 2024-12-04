package wtd.slotsengine.rest.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import wtd.slotsengine.rest.records.BalanceMessage;
import wtd.slotsengine.rest.records.ServerVersionMessage;
import wtd.slotsengine.rest.records.SpinResultMessage;
import wtd.slotsengine.services.SlotManager;
import wtd.slotsengine.slots.exceptions.InsufficientFundsException;
import wtd.slotsengine.slots.interfaces.SlotMachine;
import wtd.slotsengine.slots.machines.SpinResult;
import wtd.slotsengine.utils.SlotConstants;

import static wtd.slotsengine.utils.SlotUtils.now;

@RestController
public class ApiController {
    private static final ServerVersionMessage SERVER_BANNER = new ServerVersionMessage(SlotConstants.PROJECT_VERSION);
    private static final Logger log = LoggerFactory.getLogger(ApiController.class);
    private final SlotMachine machine;

    public ApiController(SlotManager slotManager) {
        log.info("API controller is initializing");
        this.machine = slotManager.getSlotMachine();
    }

    @GetMapping("/api")
    public ServerVersionMessage indexAction() {
        return SERVER_BANNER;
    }

    @GetMapping("/api/load")
    public SpinResultMessage load() {
        return new SpinResultMessage(now(), 1, 0, machine.getBalance(), 0);
    }

    @PostMapping("/api/spin/{amount}")
    public SpinResultMessage spin(@PathVariable("amount") Long amount) {
        log.info("Spin request received: {} result {}", amount, machine.getResult());
        try {
            SpinResult spinResult = machine.spin(amount);
            return new SpinResultMessage(now(), spinResult.betAmount(), spinResult.winAmount(), spinResult.balance(), spinResult.result());
        } catch (InsufficientFundsException ex) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Insufficient funds to spin. Required: %d Have: %d".formatted(amount, machine.getBalance()));
        }
    }

    @RequestMapping(value = "/api/deposit/{amount}")
    public BalanceMessage deposit(@PathVariable("amount") Long amount) {
        if (amount < 0) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Only positive numbers are allowed.");
        }
        log.info("Deposit request received: {}", amount);
        return new BalanceMessage(machine.deposit(amount));
    }

    @RequestMapping("/api/withdraw/{amount}")
    public BalanceMessage withdraw(@PathVariable("amount") Long amount) {
        log.info("Withdraw request received: {}", amount);
        try {
            return new BalanceMessage(machine.withdraw(amount));
        } catch (InsufficientFundsException e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Insufficient funds to spin. Required: %d Have: %d".formatted(amount, machine.getBalance()));
        }
    }
}
