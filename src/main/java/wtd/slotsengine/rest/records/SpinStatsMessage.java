package wtd.slotsengine.rest.records;

import java.util.LongSummaryStatistics;

public record SpinStatsMessage(long timestampMs, double rtp, LongSummaryStatistics betStats,
                               LongSummaryStatistics winStats) {
}