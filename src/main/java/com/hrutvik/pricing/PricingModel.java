package com.hrutvik.pricing;

@FunctionalInterface
public interface PricingModel {
    double calculate(double bestBid, double bestAsk);
}
