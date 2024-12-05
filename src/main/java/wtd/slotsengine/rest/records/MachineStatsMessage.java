package wtd.slotsengine.rest.records;

import java.util.LongSummaryStatistics;

public record MachineStatsMessage(long timestampMs, LongSummaryStatistics betStats, LongSummaryStatistics winStats) {
}
