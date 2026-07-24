package com.hrutvik.pricing;

import java.time.Instant;

public record PriceSnapshot(
        String symbol,
        long sequence,
        double bestBid,
        double bestAsk,
        double theoreticalPrice,
        Instant calculatedAt
) {}
