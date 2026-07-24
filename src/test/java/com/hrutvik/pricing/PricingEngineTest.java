package com.hrutvik.pricing;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class PricingEngineTest {
    @Test
    void processesLatestOrderedTick() {
        try (PricingEngine engine = new PricingEngine(new MidPriceModel())) {
            assertTrue(engine.submit(tick("1", 1, 99.0, 101.0)).join());
            PriceSnapshot snapshot = engine.latest("TEST");
            assertEquals(100.0, snapshot.theoreticalPrice());
            assertEquals(1, snapshot.sequence());
        }
    }

    @Test
    void rejectsDuplicateAndStaleTicks() {
        try (PricingEngine engine = new PricingEngine(new MidPriceModel())) {
            assertTrue(engine.submit(tick("1", 2, 100.0, 102.0)).join());
            assertFalse(engine.submit(tick("1", 3, 101.0, 103.0)).join());
            assertFalse(engine.submit(tick("2", 1, 98.0, 100.0)).join());
            assertEquals(1, engine.metrics().processed());
            assertEquals(2, engine.metrics().rejected());
        }
    }

    private MarketTick tick(String id, long sequence, double bid, double ask) {
        return new MarketTick(id, "TEST", sequence, bid, ask, Instant.now());
    }
}
