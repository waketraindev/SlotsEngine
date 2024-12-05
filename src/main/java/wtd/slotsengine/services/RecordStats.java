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
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class RecordStats {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(RecordStats.class);
    private final ReentrantLock writeLock;
    private PrintWriter writeStream;

    public RecordStats() {
        log.info("RecordStats is initializing");
        this.writeLock = new ReentrantLock();
    }

    @PostConstruct
    public void init() {
        log.info("RecordStats is initialized");
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
                writeLock.unlock();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Error writing results to file");
        }
    }
}
