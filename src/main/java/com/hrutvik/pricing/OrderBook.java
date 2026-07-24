package com.hrutvik.pricing;

final class OrderBook {
    private long lastSequence = -1;
    private double bestBid;
    private double bestAsk;

    boolean apply(MarketTick tick) {
        if (tick.sequence() <= lastSequence) return false;
        lastSequence = tick.sequence();
        bestBid = tick.bestBid();
        bestAsk = tick.bestAsk();
        return true;
    }

    long lastSequence() { return lastSequence; }
    double bestBid() { return bestBid; }
    double bestAsk() { return bestAsk; }
}
