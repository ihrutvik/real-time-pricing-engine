package com.hrutvik.pricing;

import java.util.concurrent.atomic.AtomicLong;

public final class EngineMetrics {
    private final AtomicLong processed = new AtomicLong();
    private final AtomicLong rejected = new AtomicLong();
    private final AtomicLong totalLatencyNanos = new AtomicLong();

    void recordProcessed(long latencyNanos) {
        processed.incrementAndGet();
        totalLatencyNanos.addAndGet(latencyNanos);
    }

    void recordRejected() {
        rejected.incrementAndGet();
    }

    public long processed() { return processed.get(); }
    public long rejected() { return rejected.get(); }
    public double averageLatencyMicros() {
        long count = processed.get();
        return count == 0 ? 0.0 : totalLatencyNanos.get() / 1_000.0 / count;
    }
}
