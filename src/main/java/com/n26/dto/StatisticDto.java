package com.n26.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class StatisticDto {
    @JsonIgnore
    private final long utcSec;
    private final double sum;
    private final double avg;
    private final double max;
    private final double min;
    private final long count;

    public StatisticDto() {
        this(Long.MIN_VALUE, 0d, Double.MIN_VALUE, Double.MAX_VALUE, 0);
    }

    public StatisticDto(long utcSec, Double sum, Double max, Double min, long count) {
        this.sum = sum;
        this.avg = sum / count;
        this.max = max;
        this.min = min;
        this.count = count;
        this.utcSec = utcSec;
    }

    public Double getSum() {
        return sum;
    }

    public Double getAvg() {
        return avg;
    }

    public Double getMax() {
        return max;
    }

    public Double getMin() {
        return min;
    }

    public long getCount() {
        return count;
    }

    public long getUtcSec() {
        return utcSec;
    }

    public StatisticDto merge(StatisticDto updated) {
        return new StatisticDto(utcSec, sum + updated.sum, Math.max(max, updated.max), Math.min(min, updated.min), count + updated.count);

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StatisticDto{");
        sb.append("utcSec=").append(utcSec);
        sb.append(", sum=").append(sum);
        sb.append(", avg=").append(avg);
        sb.append(", max=").append(max);
        sb.append(", min=").append(min);
        sb.append(", count=").append(count);
        sb.append('}');
        return sb.toString();
    }
}
