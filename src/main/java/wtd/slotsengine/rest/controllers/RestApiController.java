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
import wtd.slotsengine.services.RecordStatsService;
import wtd.slotsengine.services.SlotManagerService;
import wtd.slotsengine.slots.exceptions.InsufficientFundsException;
import wtd.slotsengine.slots.interfaces.SlotMachine;
import wtd.slotsengine.slots.machines.records.SpinOutcome;

import static wtd.slotsengine.utils.SlotUtils.now;

/**
 * This class serves as a REST API controller to manage slot machine operations and related functionalities.
 * It provides endpoints for viewing the server version, interacting with the slot machine (spinning, loading state),
 * and managing account balance (deposit and withdrawal).
 * <p>
 * The controller relies on SlotMachine for slot machine operations and RecordStatsService for managing statistics.
 * Configuration properties, such as application version, are injected via @Value annotations.
 */
@RestController
public class RestApiController {
    /**
     * Logger instance for recording and tracking events, errors, and other
     * significant information within the RestApiController class.
     * Utilizes the LoggerFactory to ensure proper configuration and management
     * of logging capabilities.
     */
    private static final Logger log = LoggerFactory.getLogger(RestApiController.class);
    /**
     * Represents a slot machine instance used in the application.
     * This variable is a final reference to a specific SlotMachine object,
     * ensuring that the reference cannot be changed after initialization.
     * <p>
     * The SlotMachine class is likely to encapsulate functionality related
     * to simulating a slot machine game, such as spinning reels, handling
     * payouts, and maintaining state.
     */
    private final SlotMachine machine;
    /**
     * A service object responsible for managing and retrieving
     * statistical data related to records.
     */
    private final RecordStatsService stats;

    /**
     * Represents the current version of the application.
     * Retrieved from the configuration property `slots-engine.version`.
     * This value is typically used to identify the software version during runtime.
     */
    @Value("${slots-engine.version}")
    private String appVersion;

    /**
     * Constructs a new RestApiController instance.
     *
     * @param slotManagerService the service responsible for managing slot machine operations
     * @param stats              the service responsible for recording and managing statistics
     */
    public RestApiController(SlotManagerService slotManagerService, RecordStatsService stats) {
        log.info("API controller is initializing");
        this.machine = slotManagerService.getSlotMachine();
        this.stats = stats;
    }

    /**
     * Handles HTTP GET requests to the "/api" endpoint and returns a server version message.
     *
     * @return a {@code ServerVersionMessage} containing the application version.
     */
    @GetMapping("/api")
    public ServerVersionMessage indexAction() {
        return new ServerVersionMessage(appVersion);
    }

    /**
     * Handles the HTTP GET request for loading the application state.
     *
     * @return a StateMessage object containing the current application state, including
     * app version, current time, machine RTP, state details, and balance information.
     */
    @GetMapping("/api/load")
    public StateMessage load() {
        return new StateMessage(appVersion, now(), machine.getMachineRtp(), 1, 0, machine.getBalance(), 0);
    }

    /**
     * Retrieves the current machine statistics, including RTP (Return to Player),
     * bet statistics, and win statistics.
     *
     * @return a SpinStatsMessage object containing the current machine statistics.
     */
    public @GetMapping("/api/machine-stats") SpinStatsMessage getMachineStats() {
        return new SpinStatsMessage(now(), machine.getMachineRtp(), stats.getBetStats(), stats.getWinStats());
    }

    /**
     * Spins the machine with the specified bet amount and returns the result of the spin.
     *
     * @param amount the amount to bet for the spin
     * @return a BetResultMessage containing details of the spin outcome, including bet amount,
     * win amount, updated balance, and spin result symbols
     * @throws ResponseStatusException when there are insufficient funds to complete the spin
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
     * Handles the deposit operation for the given amount.
     *
     * @param amount the amount to be deposited; must be a positive number
     * @return a BalanceMessage object containing the updated balance information
     * @throws ResponseStatusException if the amount is negative
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
     * Processes a withdrawal request with the specified amount.
     *
     * @param amount the amount to be withdrawn.
     * @return a {@code BalanceMessage} containing the updated balance after the withdrawal.
     * @throws ResponseStatusException if there are insufficient funds to fulfill the withdrawal.
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