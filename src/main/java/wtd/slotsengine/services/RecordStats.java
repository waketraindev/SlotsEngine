package wtd.slotsengine.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
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

@Service
public class RecordStats {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(RecordStats.class);
    private final ReentrantLock writeLock;
    private PrintWriter writeStream;
    private static final LongSummaryStatistics winStats = new LongSummaryStatistics();
    private static final LongSummaryStatistics betStats = new LongSummaryStatistics();

    public RecordStats() {
        log.info("RecordStats is initializing");
        this.writeLock = new ReentrantLock();
    }

    @PostConstruct
    public void init() {
        log.info("RecordStats is initialized");
        winStats.accept(0);
        betStats.accept(0);
        File csvResultsFile = new File("results.csv");
        if (!csvResultsFile.exists()) {
            try {
                boolean fileCreated = csvResultsFile.createNewFile();
                if (!fileCreated) {
                    throw new RuntimeException("Failed to create results.csv file");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            Scanner scanner = new Scanner(new File("results.csv"));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] cols = line.split(",");
                addStats(Long.parseLong(cols[1]), Long.parseLong(cols[2]));
            }
            writeStream = new PrintWriter(csvResultsFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addStats(long betAmount, long winAmount) {
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
                String output = Stream.of(bet.timestampMs(), bet.betAmount(), bet.winAmount(), bet.result()).map(String::valueOf).collect(Collectors.joining(","));
                writeStream.append(output).append("\n");
                writeStream.flush();
                addStats(bet.betAmount(), bet.winAmount());
                writeLock.unlock();
            }
        } catch (InterruptedException e) {
            log.error("Failed to acquire write lock", e);
        }
    }
}
