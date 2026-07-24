# Real-Time Market Data & Pricing Engine

A Java 21 project that simulates exchange market-data events, maintains per-symbol order books, calculates real-time theoretical prices, and reports latency and throughput metrics.

## Why this project

The project demonstrates engineering patterns used in real-time pricing systems:

- ordered event processing per instrument
- idempotency and stale-sequence rejection
- in-memory order-book maintenance
- atomic price snapshots
- low-latency asynchronous processing
- replay-friendly event design
- latency and throughput measurements

## Architecture

```text
MarketTickSimulator
        |
        v
PricingEngine.submit(tick)
        |
        +--> per-symbol single-thread executor
                  |
                  v
              OrderBook
                  |
                  v
           PricingCalculator
                  |
                  v
          Atomic PriceSnapshot
```

Each symbol is processed by one dedicated executor. This preserves ordering without a global lock while allowing different symbols to be processed concurrently.

## Tech stack

- Java 21
- Maven
- JUnit 5
- GitHub Actions
- Docker

## Run locally

```bash
mvn clean test
mvn exec:java
```

Example output:

```text
AAPL price=189.95 bid=189.90 ask=190.00 sequence=1000
MSFT price=421.15 bid=421.10 ask=421.20 sequence=1000
processed=2000 rejected=0 averageLatencyMicros=...
```

## Run with Docker

```bash
docker build -t real-time-pricing-engine .
docker run --rm real-time-pricing-engine
```

## Pricing model

The initial model uses the mid-price:

```text
theoreticalPrice = (bestBid + bestAsk) / 2
```

The pricing interface is intentionally replaceable so richer models can be introduced later.

## Reliability behaviour

A tick is rejected when:

- its event ID has already been processed for that symbol
- its sequence number is lower than or equal to the latest accepted sequence
- bid or ask values are invalid
- the best bid is greater than the best ask

## Roadmap

- Kafka-backed market-data ingestion
- event persistence and deterministic replay
- Redis-backed latest-price distribution
- PostgreSQL historical snapshots
- percentile latency metrics
- load and fault-injection tests

## Author

Hrutvik Nagrale — [GitHub](https://github.com/ihrutvik) | [LinkedIn](https://www.linkedin.com/in/hrutvik-nagrale/)
