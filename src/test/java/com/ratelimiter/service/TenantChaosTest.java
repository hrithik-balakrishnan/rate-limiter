package com.ratelimiter.service;

import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class TenantChaosTest {

    @Test
    public void runAsymmetricContentionTest() throws InterruptedException {
        int totalThreads = 10000;
        // 100 tokens max, refilling 10 per second
        TenantRegistry registry = new TenantRegistry(100, 10);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(totalThreads);

        AtomicInteger abuserRequests = new AtomicInteger(0);
        AtomicInteger normalRequests = new AtomicInteger(0);

        for (int i = 0; i < totalThreads; i++) {
            // 80% chance to act as the abuser
            boolean isAbuser = ThreadLocalRandom.current().nextDouble() < 0.8;
            String tenantId = isAbuser ? "ABUSER_999" : "NORMAL_" + ThreadLocalRandom.current().nextInt(100);

            Thread t = new Thread(() -> {
                try {
                    startLatch.await(); // All threads wait here

                    TokenBucket bucket = registry.getBucketForTenant(tenantId);
                    bucket.allowRequest();

                    if (isAbuser) abuserRequests.incrementAndGet();
                    else normalRequests.incrementAndGet();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
            t.start();
        }

        System.out.println("Warming up chaos threads...");
        Thread.sleep(500);

        long startTime = System.currentTimeMillis();
        startLatch.countDown(); // FIRE ALL THREADS
        endLatch.await();       // Wait for completion
        long totalTimeMs = System.currentTimeMillis() - startTime;

        long throughput = (totalThreads * 1000L) / totalTimeMs;

        System.out.println("=========================================");
        System.out.println("=== ASYMMETRIC SATURATION TEST (WEEK 5) ===");
        System.out.println("Total Time: " + totalTimeMs + " ms");
        System.out.println("Abuser Requests Fired: " + abuserRequests.get());
        System.out.println("Normal Requests Fired: " + normalRequests.get());
        System.out.println("Throughput: " + throughput + " RPS");
        System.out.println("=========================================");
    }
}