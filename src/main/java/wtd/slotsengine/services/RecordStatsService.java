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
 * A service class responsible for managing and recording statistical data related to betting.
 * This includes managing log files, tracking summary statistics for bets and winnings,
 * and persisting betting results to a file. It also handles initialization and destruction
 * tasks to ensure proper resource management and file handling.
 * <p>
 * This class uses a {@link ReentrantLock} to ensure thread-safe operations when recording bets.
 */
@Service
public class RecordStatsService {
    private static final Logger log = LoggerFactory.getLogger(RecordStatsService.class);
    private static final String RESULTS_FILE = "results.csv";
    private final ReentrantLock writeLock = new ReentrantLock();
    private final LongSummaryStatistics winStats = new LongSummaryStatistics(0, 0, 0, 0);
    private final LongSummaryStatistics betStats = new LongSummaryStatistics(0, 0, 0, 0);
    private PrintWriter writeStream;

    @PostConstruct
    public void init() {
        log.info("RecordStats is initialized");
        createResultsFileIfNotExists();
        loadPreviousStats();
    }

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

    private void addStats(long betAmount, long winAmount) {
        betStats.accept(betAmount);
        if (winAmount > 0) winStats.accept(winAmount);
    }

    @PreDestroy
    public void destroy() {
        log.info("RecordStats is destroying");
        writeStream.close();
        log.info("RecordStats is destroyed");
    }

    public LongSummaryStatistics getWinStats() {
        return winStats;
    }

    public LongSummaryStatistics getBetStats() {
        return betStats;
    }

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

    private String formatBetResult(BetResultMessage bet) {
        return Stream.of(bet.timestampMs(), bet.betAmount(), bet.winAmount(), bet.result()).map(
                String::valueOf).collect(Collectors.joining(","));
    }
}