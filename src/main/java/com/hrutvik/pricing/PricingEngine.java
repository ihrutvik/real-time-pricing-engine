package com.hrutvik.pricing;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public final class PricingEngine implements AutoCloseable {
    private final PricingModel pricingModel;
    private final ConcurrentMap<String, ExecutorService> executors = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, OrderBook> books = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Set<String>> processedEventIds = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, AtomicReference<PriceSnapshot>> snapshots = new ConcurrentHashMap<>();
    private final EngineMetrics metrics = new EngineMetrics();

    public PricingEngine(PricingModel pricingModel) {
        this.pricingModel = pricingModel;
    }

    public CompletableFuture<Boolean> submit(MarketTick tick) {
        ExecutorService executor = executors.computeIfAbsent(tick.symbol(), key ->
                Executors.newSingleThreadExecutor(Thread.ofVirtual().name("pricing-" + key + "-", 0).factory()));

        return CompletableFuture.supplyAsync(() -> process(tick), executor);
    }

    private boolean process(MarketTick tick) {
        long start = System.nanoTime();
        Set<String> ids = processedEventIds.computeIfAbsent(tick.symbol(), key -> ConcurrentHashMap.newKeySet());
        if (!ids.add(tick.eventId())) {
            metrics.recordRejected();
            return false;
        }

        OrderBook book = books.computeIfAbsent(tick.symbol(), key -> new OrderBook());
        if (!book.apply(tick)) {
            metrics.recordRejected();
            return false;
        }

        double price = pricingModel.calculate(book.bestBid(), book.bestAsk());
        PriceSnapshot snapshot = new PriceSnapshot(
                tick.symbol(), book.lastSequence(), book.bestBid(), book.bestAsk(), price, Instant.now());
        snapshots.computeIfAbsent(tick.symbol(), key -> new AtomicReference<>()).set(snapshot);
        metrics.recordProcessed(System.nanoTime() - start);
        return true;
    }

    public PriceSnapshot latest(String symbol) {
        AtomicReference<PriceSnapshot> reference = snapshots.get(symbol);
        return reference == null ? null : reference.get();
    }

    public EngineMetrics metrics() {
        return metrics;
    }

    @Override
    public void close() {
        executors.values().forEach(ExecutorService::shutdown);
    }
}
