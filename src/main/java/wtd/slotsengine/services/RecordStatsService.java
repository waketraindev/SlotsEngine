package wtd.slotsengine.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import wtd.slotsengine.rest.records.BetResultMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LongSummaryStatistics;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service for recording and managing statistics related to bets and wins.
 * This class maintains cumulative statistics for bets and wins, persists the data in a CSV
 * file, and supports safe concurrent updates.
 * <p>
 * The statistics are loaded and saved automatically using a results file, ensuring
 * the system updates persist across application restarts.
 */
@Service
public class RecordStatsService {
    private static final Logger log = LoggerFactory.getLogger(RecordStatsService.class);
    private static final String RESULTS_FILE = "results.csv";
    /**
     * Lock for managing concurrent write operations in a thread-safe manner.
     * Ensures that only one thread can execute critical sections involving
     * write access, such as recording and persisting bet data, at any given time.
     */
    private final ReentrantLock writeLock = new ReentrantLock();
    private final LongSummaryStatistics winStats = new LongSummaryStatistics(0, 0, 0, 0);
    private final LongSummaryStatistics betStats = new LongSummaryStatistics(0, 0, 0, 0);
    private PrintWriter writeStream;

    /**
     * Initializes the RecordStatsService by performing the following tasks:
     * - Logs an initialization message to indicate the service is starting.
     * - Ensures the results file exists by creating it if it does not.
     * - Loads previously recorded statistics from the results file to restore state.
     * <p>
     * This method is automatically invoked after the bean initialization phase as
     * it is annotated with {@code @PostConstruct}.
     */
    @PostConstruct
    public void init() {
        log.info("RecordStats is initialized");
        createResultsFileIfNotExists();
        loadPreviousStats();
    }

    /**
     * Ensures that the results file used for persisting statistics exists in the system.
     * If the file does not exist, this method creates a new file with the predefined name.
     * If the file creation fails, an exception is thrown.
     * <p>
     * This method is used during the initialization process to guarantee that the results
     * file is available for loading and saving statistics.
     * <p>
     * Throws:
     * - RuntimeException if the file cannot be created due to an IOException or a
     * failure in the file creation process.
     */
    private void createResultsFileIfNotExists() {
        File csvResultsFile = new File(RESULTS_FILE);
        if (!csvResultsFile.exists()) {
            try {
                if (!csvResultsFile.createNewFile()) {
                    throw new RuntimeException("Failed to create %s file".formatted(RESULTS_FILE));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Loads previous statistics from a file and processes the data.
     * This method reads lines from the specified file, splits each line
     * by commas, and then processes the extracted columns as numerical data.
     * Each line is assumed to represent a record, with specific fields
     * at positions defined in the code.
     * <p>
     * If the file cannot be found, a {@link RuntimeException} is thrown.
     * <p>
     * The method uses a try-with-resources block to ensure proper handling
     * and closing of resources such as the {@link Scanner}. It also initializes
     * a {@link PrintWriter} for writing back to the file after processing.
     * <p>
     * Throws:
     * - RuntimeException if the specified file does not exist or cannot be read.
     */
    private void loadPreviousStats() {
        try (Scanner scanner = new Scanner(new File(RESULTS_FILE))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] cols = line.split(",");
                addStats(Long.parseLong(cols[1]), Long.parseLong(cols[2]));
            }
            writeStream = new PrintWriter(RESULTS_FILE);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates statistics for bets and wins by recording the bet amount and win amount.
     *
     * @param betAmount the amount of the bet placed
     * @param winAmount the amount won, should be greater than zero to be recorded
     */
    private void addStats(long betAmount, long winAmount) {
        betStats.accept(betAmount);
        if (winAmount > 0) winStats.accept(winAmount);
    }

    /**
     * Cleans up resources and performs necessary shutdown tasks when the object is being destroyed.
     * This method is annotated with @PreDestroy to ensure it is invoked during the destruction phase of the lifecycle.
     * It logs the initiation and completion of the destruction process as well as closes the stream resource.
     */
    @PreDestroy
    public void destroy() {
        log.info("RecordStats is destroying");
        writeStream.close();
        log.info("RecordStats is destroyed");
    }

    /**
     * Retrieves the statistics of win records.
     *
     * @return a LongSummaryStatistics object representing the statistical summary of win data,
     * including count, sum, min, average, and max values.
     */
    public LongSummaryStatistics getWinStats() {
        return winStats;
    }

    /**
     * Retrieves the betting statistics.
     *
     * @return a LongSummaryStatistics object containing statistical data on bets,
     * including count, sum, min, average, and max.
     */
    public LongSummaryStatistics getBetStats() {
        return betStats;
    }

    /**
     * Records the result of a bet by formatting the bet result, writing it to the output stream,
     * updating statistics, and ensuring thread safety using a write lock.
     *
     * @param bet the bet result message containing information about the bet, such as bet amount and win amount
     */
    public void recordBet(BetResultMessage bet) {
        try {
            if (writeLock.tryLock(1, TimeUnit.SECONDS)) {
                String output = formatBetResult(bet);
                writeStream.append(output).append("\n");
                writeStream.flush();
                addStats(bet.betAmount(), bet.winAmount());
                writeLock.unlock();
            }
        } catch (InterruptedException e) {
            log.error("Failed to acquire write lock", e);
        }
    }

    /**
     * Formats the given BetResultMessage into a comma-separated string.
     *
     * @param bet the BetResultMessage object containing bet details, such as timestamp,
     *            bet amount, win amount, and result.
     * @return a string representation of the bet details, with each field separated by a comma.
     */
    private String formatBetResult(BetResultMessage bet) {
        return Stream.of(bet.timestampMs(), bet.betAmount(), bet.winAmount(), bet.result()).map(
                String::valueOf).collect(Collectors.joining(","));
    }
}