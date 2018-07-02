package com.n26.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionDto {

    private double amount;

    private long timestamp;

    @JsonCreator
    public TransactionDto(@JsonProperty("amount") double amount,
                          @JsonProperty("timestamp") Long timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
