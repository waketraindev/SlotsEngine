package wtd.slotsengine.rest.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import wtd.slotsengine.rest.records.*;
import wtd.slotsengine.services.RecordStats;
import wtd.slotsengine.services.SlotManager;
import wtd.slotsengine.slots.exceptions.InsufficientFundsException;
import wtd.slotsengine.slots.interfaces.SlotMachine;
import wtd.slotsengine.slots.machines.records.SpinOutcome;

import static wtd.slotsengine.utils.SlotUtils.now;

/**
 * The ApiController class is responsible for handling API requests related to a slot machine application.
 * It provides endpoints for retrieving server version information, loading the slot machine state,
 * performing spin operations, and managing the user's balance through deposit and withdrawal actions.
 * Each method is mapped to a specific HTTP request type and URL path, enabling interaction with the slot machine.
 * <p>
 * The controller ensures that each endpoint is appropriately logged and handles exceptions that may occur
 * during operations, such as insufficient funds for a spin or withdrawal.
 * <p>
 * The class leverages a SlotMachine instance to perform the core operations and depends on configuration
 * values for versioning information.
 */
@RestController
public class ApiController {
    private static final Logger log = LoggerFactory.getLogger(ApiController.class);
    private final SlotMachine machine;
    private final RecordStats stats;

    @Value("${slots-engine.version}")
    private String appVersion;

    /**
     * Initializes an instance of ApiController.
     * This constructor sets up the necessary configurations
     * for managing slot machine operations through the SlotManager.
     *
     * @param slotManager an instance of SlotManager that provides access
     *                    to the slot machine used by the API controller
     */
    public ApiController(SlotManager slotManager, RecordStats stats) {
        log.info("API controller is initializing");
        this.machine = slotManager.getSlotMachine();
        this.stats = stats;
    }

    /**
     * Handles the HTTP GET request for the root API endpoint and provides the server version information.
     *
     * @return a {@link ServerVersionMessage} object containing the current version of the application.
     */
    @GetMapping("/api")
    public ServerVersionMessage indexAction() {
        return new ServerVersionMessage(appVersion);
    }

    /**
     * Handles the HTTP GET request for loading the current state of the slot machine.
     *
     * @return a {@link MachineStateMessage} object containing the current timestamp,
     * machine's return to player (RTP), bet amount, win amount, balance, and result.
     */
    @GetMapping("/api/load")
    public MachineStateMessage load() {
        return new MachineStateMessage(appVersion, now(), machine.getMachineRtp(), 1, 0, machine.getBalance(), 0);
    }

    /**
     * Retrieves the current machine statistics, including the timestamp, bet statistics, and win statistics.
     *
     * @return a MachineStatsMessage object containing the current timestamp, bet statistics, and win statistics.
     */
    public @GetMapping("/api/machine-stats") MachineStatsMessage getMachineStats() {
        return new MachineStatsMessage(now(), machine.getMachineRtp(), stats.getBetStats(), stats.getWinStats());
    }

    /**
     * Initiates a spin operation on the slot machine for the specified bet amount.
     *
     * @param amount the amount to bet on the spin.
     * @return a {@link BetResultMessage} object containing the timestamp, bet amount, win amount,
     * balance after the spin, and the resulting symbol from the spin.
     * @throws ResponseStatusException with HTTP status 400 if there are insufficient funds to perform the spin.
     */
    @PostMapping("/api/spin/{amount}")
    public BetResultMessage spin(@PathVariable("amount") Long amount) {
        try {
            SpinOutcome spinResult = machine.spin(amount);

            BetResultMessage betResultMessage =
                    new BetResultMessage(
                            now(), spinResult.betAmount(), spinResult.winAmount(), spinResult.balance(),
                            spinResult.symbol());
            stats.recordBet(betResultMessage);
            return betResultMessage;
        } catch (InsufficientFundsException ex) {
            throw new ResponseStatusException(
                    HttpStatusCode.valueOf(400),
                    "Insufficient funds to spin. Required: %d Have: %d".formatted(amount, machine.getBalance()));
        }
    }

    /**
     * Handles the HTTP request to deposit a specified amount into the slot machine.
     *
     * @param amount a positive Long value representing the amount to deposit.
     * @return a {@link BalanceMessage} object containing the updated balance after the deposit.
     * @throws ResponseStatusException with HTTP status 400 if the specified amount is negative.
     */
    @PostMapping(value = "/api/deposit/{amount}")
    public BalanceMessage deposit(@PathVariable("amount") Long amount) {
        if (amount < 0) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Only positive numbers are allowed.");
        }
        log.info("Deposit request received: {}", amount);
        return new BalanceMessage(machine.deposit(amount));
    }

    /**
     * Handles the HTTP request to withdraw a specified amount from the slot machine's balance.
     *
     * @param amount the amount to withdraw from the balance. Must be a positive Long value.
     * @return a {@link BalanceMessage} object containing the updated balance after the withdrawal.
     * @throws ResponseStatusException with HTTP status 400 if there are insufficient funds to perform the withdrawal.
     */
    @PostMapping("/api/withdraw/{amount}")
    public BalanceMessage withdraw(@PathVariable("amount") Long amount) {
        log.info("Withdraw request received: {}", amount);
        try {
            return new BalanceMessage(machine.withdraw(amount));
        } catch (InsufficientFundsException e) {
            throw new ResponseStatusException(
                    HttpStatusCode.valueOf(400),
                    "Insufficient funds to spin. Required: %d Have: %d".formatted(amount, machine.getBalance()));
        }
    }
}