package com.hrutvik.pricing;

import java.time.Instant;
import java.util.Objects;

public record MarketTick(
        String eventId,
        String symbol,
        long sequence,
        double bestBid,
        double bestAsk,
        Instant receivedAt
) {
    public MarketTick {
        Objects.requireNonNull(eventId);
        Objects.requireNonNull(symbol);
        Objects.requireNonNull(receivedAt);
        if (eventId.isBlank() || symbol.isBlank()) throw new IllegalArgumentException("eventId and symbol are required");
        if (bestBid <= 0 || bestAsk <= 0 || bestBid > bestAsk) throw new IllegalArgumentException("invalid market prices");
    }
}
