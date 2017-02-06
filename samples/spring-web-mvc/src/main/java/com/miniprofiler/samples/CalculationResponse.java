package com.miniprofiler.samples;

/**
 * Simple POJO model class for testing.
 */
public class CalculationResponse {
    private int sum;
    private int difference;

    public CalculationResponse(int sum, int difference) {
        this.sum = sum;
        this.difference = difference;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getDifference() {
        return difference;
    }

    public void setDifference(int difference) {
        this.difference = difference;
    }
}
