package com.hrutvik.pricing;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class MarketTickSimulator {
    public MarketTick next(String symbol, long sequence, double basePrice) {
        double movement = ThreadLocalRandom.current().nextDouble(-0.25, 0.25);
        double mid = basePrice + movement;
        return new MarketTick(
                UUID.randomUUID().toString(),
                symbol,
                sequence,
                round(mid - 0.05),
                round(mid + 0.05),
                Instant.now());
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
