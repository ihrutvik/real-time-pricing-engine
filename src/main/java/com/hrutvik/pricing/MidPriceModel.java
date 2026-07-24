package com.hrutvik.pricing;

public final class MidPriceModel implements PricingModel {
    @Override
    public double calculate(double bestBid, double bestAsk) {
        return (bestBid + bestAsk) / 2.0;
    }
}
