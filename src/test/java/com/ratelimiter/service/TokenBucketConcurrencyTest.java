package com.ratelimiter.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

class TokenBucketConcurrencyTest {

    @Test
    @DisplayName("Benchmark: Synchronized Intrinsic Lock Contention")
    void benchmarkSynchronizedBucket() throws InterruptedException {
        // Setup: 100 concurrent threads, each firing 1,000 requests = 100,000 total requests
        int numThreads = 100;
        int requestsPerThread = 1000;
        int totalRequests = numThreads * requestsPerThread;

        // Bucket allowing 100,000 tokens per second (virtually unlimited for this test)
        // We are benchmarking the SPEED of the lock, not the token math.
        TokenBucket bucket = new TokenBucket(totalRequests, totalRequests);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // The "Starting Pistol"
        CountDownLatch readyLatch = new CountDownLatch(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numThreads);

        AtomicInteger successfulRequests = new AtomicInteger(0);

        System.out.println("--- Starting Week 2 Concurrency Benchmark ---");
        System.out.println("Warming up " + numThreads + " threads...");

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    readyLatch.countDown(); // Tell main thread "I am ready at the starting line"
                    startLatch.await();     // Wait for the pistol to fire

                    // Fire 1,000 requests as fast as possible
                    for (int j = 0; j < requestsPerThread; j++) {
                        if (bucket.allowRequest()) {
                            successfulRequests.incrementAndGet();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();  // Tell main thread "I finished my lap"
                }
            });
        }

        // Wait for all threads to reach the starting line
        readyLatch.await();

        System.out.println("All threads ready. Firing the starting pistol!");
        long startTime = System.nanoTime();

        // FIRE! (Releases all 100 threads simultaneously)
        startLatch.countDown();

        // Wait for all threads to cross the finish line
        doneLatch.await();
        long endTime = System.nanoTime();

        executor.shutdown();

        // Calculate benchmark metrics
        long durationMillis = (endTime - startTime) / 1_000_000;
        long requestsPerSecond = (totalRequests * 1000L) / Math.max(durationMillis, 1);

        System.out.println("==========================================");
        System.out.println("Total Requests Processed: " + successfulRequests.get());
        System.out.println("Total Time: " + durationMillis + " ms");
        System.out.println("Throughput: " + requestsPerSecond + " RPS");
        System.out.println("==========================================\n");
    }
}