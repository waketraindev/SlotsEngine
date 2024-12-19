package wtd.slotsengine.rest.records;

import java.util.LongSummaryStatistics;

/**
 * Represents statistical information related to spins in a slot engine.
 * This record encapsulates performance and summary statistics over a given
 * time period.
 * <p>
 * Fields:
 * - timestampMs: The timestamp in milliseconds representing when the data was recorded.
 * - rtp: Real-Time Performance (RTP), usually expressed as a ratio or percentage,
 * which reflects the payout performance.
 * - betStats: Summary statistics for bets placed during the recorded period
 * (e.g., count, sum, average, min, max).
 * - winStats: Summary statistics for wins accrued during the recorded period
 **/
public record SpinStatsMessage(long timestampMs, double rtp, LongSummaryStatistics betStats,
                               LongSummaryStatistics winStats) {
}