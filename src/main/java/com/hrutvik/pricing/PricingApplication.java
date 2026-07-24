package com.hrutvik.pricing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class PricingApplication {
    public static void main(String[] args) {
        MarketTickSimulator simulator = new MarketTickSimulator();

        try (PricingEngine engine = new PricingEngine(new MidPriceModel())) {
            List<CompletableFuture<Boolean>> futures = new ArrayList<>();
            for (long sequence = 1; sequence <= 1_000; sequence++) {
                futures.add(engine.submit(simulator.next("AAPL", sequence, 190.0)));
                futures.add(engine.submit(simulator.next("MSFT", sequence, 421.0)));
            }
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

            print(engine.latest("AAPL"));
            print(engine.latest("MSFT"));
            System.out.printf("processed=%d rejected=%d averageLatencyMicros=%.2f%n",
                    engine.metrics().processed(),
                    engine.metrics().rejected(),
                    engine.metrics().averageLatencyMicros());
        }
    }

    private static void print(PriceSnapshot snapshot) {
        System.out.printf("%s price=%.2f bid=%.2f ask=%.2f sequence=%d%n",
                snapshot.symbol(), snapshot.theoreticalPrice(), snapshot.bestBid(), snapshot.bestAsk(), snapshot.sequence());
    }
}
