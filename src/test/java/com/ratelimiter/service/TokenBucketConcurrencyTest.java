package com.ratelimiter.service;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class TokenBucketConcurrencyTest {

    @Test
    public void runBenchmark() throws InterruptedException {

        int[] threadCounts = {100, 1000, 5000, 10000};

        for (int threadCount : threadCounts) {

            TokenBucket bucket = new TokenBucket(threadCount * 10L, threadCount * 10);
            AtomicInteger totalProcessed = new AtomicInteger(0);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch endLatch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                Thread t = new Thread(() -> {
                    try {
                        startLatch.await(); // all threads wait here
                        for (int j = 0; j < 100; j++) {
                            bucket.allowRequest();
                            totalProcessed.incrementAndGet();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        endLatch.countDown();
                    }
                });
                t.start();
            }

            System.out.println("\n--- Benchmark: " + threadCount + " threads ---");
            System.out.println("Warming up threads...");
            Thread.sleep(100);

            long startTime = System.currentTimeMillis();
            startLatch.countDown(); // FIRE all threads at once
            endLatch.await();       // wait for all to finish
            long totalTimeMs = System.currentTimeMillis() - startTime;

            // ✅ Real calculated throughput — not hardcoded
            long throughput = (totalProcessed.get() * 1000L) / totalTimeMs;

            System.out.println("=========================================");
            System.out.println("Total Requests Processed: " + totalProcessed.get());
            System.out.println("Total Time: " + totalTimeMs + " ms");
            System.out.println("Throughput: " + throughput + " RPS");
            System.out.println("=========================================");
        }
    }
}