package wtd.slotsengine.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import wtd.slotsengine.rest.records.BetResultMessage;

import java.io.*;
import java.util.ArrayList;
import java.util.LongSummaryStatistics;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

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
                betStats.accept(Long.parseLong(cols[1]));
                winStats.accept(Long.parseLong(cols[2]));
            }
            writeStream = new PrintWriter(csvResultsFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
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
                ArrayList<String> cols = new ArrayList<String>();
                cols.add(String.valueOf(bet.timestampMs()));
                cols.add(String.valueOf(bet.betAmount()));
                cols.add(String.valueOf(bet.winAmount()));
                cols.add(String.valueOf(bet.result()));
                String output = String.join(",", cols);
                writeStream.append(output).append("\n");
                writeStream.flush();

                betStats.accept(bet.betAmount());
                winStats.accept(bet.winAmount());

                writeLock.unlock();
            }
        } catch (InterruptedException e) {
            log.error("Failed to acquire write lock", e);
        }
    }
}
