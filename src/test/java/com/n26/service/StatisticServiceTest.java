package com.n26.service;

import com.n26.dto.StatisticDto;
import com.n26.dto.TransactionDto;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

public class StatisticServiceTest {



    @Test
    public void testAddAnOldTransaction() throws Exception {
        final StatisticService statisticService = new StatisticService(10);
        Assert.assertFalse(statisticService.addTransaction(new TransactionDto(12.3, Instant.now().minusSeconds(11).toEpochMilli())));
    }

    @Test
    public void testAddAValidTransaction() throws Exception {
        final StatisticService statisticService = new StatisticService(10);
        Assert.assertTrue(statisticService.addTransaction(new TransactionDto(12.3, Instant.now().minusSeconds(10).toEpochMilli())));
    }

    @Test
    public void testStatisticAreProperlyAccumulatedOnTheSameSec() throws Exception {
        final long toEpochMilli = Instant.now().minusSeconds(5).toEpochMilli();
        final StatisticService statisticService = new StatisticService(10);
        statisticService.addTransaction(new TransactionDto(2, toEpochMilli));
        statisticService.addTransaction(new TransactionDto(5, toEpochMilli));
        final StatisticDto statistic = statisticService.getStatistic();
        Assert.assertEquals(statistic.getAvg(), 3.5);
        Assert.assertEquals(statistic.getCount(), 2);
        Assert.assertEquals(statistic.getMax(), 5d);
        Assert.assertEquals(statistic.getMin(), 2d);
    }

    @Test
    public void testStatisticAreProperlyAccumulatedOnDifferentSec() throws Exception {
        final StatisticService statisticService = new StatisticService(10);
        Instant now = Instant.now();
        long toEpochMilli = now.minusSeconds(5).toEpochMilli();
        statisticService.addTransaction(new TransactionDto(2, toEpochMilli));
        toEpochMilli = now.minusSeconds(4).toEpochMilli();
        statisticService.addTransaction(new TransactionDto(5, toEpochMilli));
        final StatisticDto statistic = statisticService.getStatistic();
        Assert.assertEquals(statistic.getAvg(), 3.5);
        Assert.assertEquals(statistic.getCount(), 2);
        Assert.assertEquals(statistic.getMax(), 5d);
        Assert.assertEquals(statistic.getMin(), 2d);
    }

    @Test
    public void multiThreadTestForAddTransaction() throws InterruptedException {

        final StatisticService statisticService = new StatisticService(11);
        Instant now = Instant.now();
        final ExecutorService executorService = Executors.newFixedThreadPool(13);
        for (int i=0; i< 10000; i++) {
            executorService.submit(() -> {
                //9000 mill to have all the transaction inside the retention period
                long toEpochMilli = now.minusMillis(ThreadLocalRandom.current().nextInt(0, 9000)).toEpochMilli();
                statisticService.addTransaction(new TransactionDto(1d, toEpochMilli));
            });
        }
        executorService.awaitTermination(1, TimeUnit.SECONDS);

        final StatisticDto statistic = statisticService.getStatistic();
        Assert.assertEquals(statistic.getAvg(), 1d);
        Assert.assertEquals(statistic.getCount(), 10000);
        Assert.assertEquals(statistic.getMax(), 1d);
        Assert.assertEquals(statistic.getMin(), 1d);
        Assert.assertEquals(statistic.getSum(), 10000d);


    }
}