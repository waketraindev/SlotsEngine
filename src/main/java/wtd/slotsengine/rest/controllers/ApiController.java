package wtd.slotsengine.rest.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import wtd.slotsengine.rest.records.BalanceMessage;
import wtd.slotsengine.rest.records.ServerVersionMessage;
import wtd.slotsengine.rest.records.SpinResultMessage;
import wtd.slotsengine.services.SlotManager;
import wtd.slotsengine.slots.exceptions.InsufficientFundsException;
import wtd.slotsengine.slots.interfaces.SlotMachine;
import wtd.slotsengine.utils.SlotUtils;

import static wtd.slotsengine.utils.SlotUtils.now;

@RestController
public class ApiController {
    private static final ServerVersionMessage SERVER_BANNER = new ServerVersionMessage(SlotUtils.PROJECT_VERSION);
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
        return new SpinResultMessage(now(), 0, 0, machine.getBalance(), machine.getResult());
    }

    @PostMapping("/api/spin/{amount}")
    public SpinResultMessage spin(@PathVariable("amount") Long amount) {
        if (amount > 0) {
            log.info("Spin request received: {} result {}", amount, machine.getResult());
            try {
                long winAmount = machine.spin(amount);
                return new SpinResultMessage(now(), amount, winAmount, machine.getBalance(), machine.getResult());
            } catch (InsufficientFundsException ex) {
                return new SpinResultMessage(now(), amount, 0L, machine.getBalance(), 0);
            }
        } else {
            return new SpinResultMessage(now(), amount, 0L, machine.getBalance(), 0);
        }
    }

    @RequestMapping(value = "/api/deposit/{amount}")
    public BalanceMessage deposit(@PathVariable("amount") Long amount) {
        if (amount > 0) {
            log.info("Deposit request received: {}", amount);
            return new BalanceMessage(machine.deposit(amount));
        } else {
            return new BalanceMessage(machine.getBalance());
        }
    }

    @RequestMapping("/api/withdraw/{amount}")
    public BalanceMessage withdraw(@PathVariable("amount") Long amount) {
        if (amount > 0) {
            log.info("Withdraw request received: {}", amount);
            try {
                return new BalanceMessage(machine.withdraw(amount));
            } catch (InsufficientFundsException e) {
                return new BalanceMessage(machine.getBalance());
            }
        } else {
            return new BalanceMessage(machine.getBalance());
        }
    }
}
