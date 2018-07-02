package com.n26.service;

import com.n26.dto.StatisticDto;
import com.n26.dto.TransactionDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceArray;

@Service
public class StatisticService {

    private AtomicReferenceArray<StatisticDto> transactions;

    private final int retention;

    public StatisticService(@Value("${com.n26,statistic.retention:60}") int retention) {
        this.transactions = new AtomicReferenceArray<>(retention);
        this.retention = retention;
        for (int i = 0; i < retention; i++) {
            transactions.set(i, new StatisticDto());
        }
    }


    public boolean addTransaction(TransactionDto transactionDto) {
        long transactionSeconds = TimeUnit.MILLISECONDS.toSeconds(transactionDto.getTimestamp());
        long difference = Instant.now().minusSeconds(transactionSeconds).getEpochSecond();
        if (difference  > retention) {
            return false;
        }

        int index = (int) transactionSeconds % retention;

        final Double amount = transactionDto.getAmount();
        transactions.accumulateAndGet(index, new StatisticDto(transactionSeconds, amount, amount, amount, 1), this::merge);

        return true;
    }


    private StatisticDto merge(StatisticDto current, StatisticDto updated) {
        if (current.getUtcSec() != updated.getUtcSec()) {
            return updated;
        } else {
            return current.merge(updated);
        }
    }

    public StatisticDto getStatistic() {
        StatisticDto statisticDto = new StatisticDto();
        final long epochSecond = Instant.now().minusSeconds(retention).getEpochSecond();
        for(int i =0; i<retention; i++){
            final StatisticDto updated = transactions.get(i);
            if(updated.getUtcSec() >= epochSecond) {
                statisticDto= statisticDto.merge(updated);
            }
        }
        return statisticDto;

    }

}
