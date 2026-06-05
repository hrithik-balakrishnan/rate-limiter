package com.ratelimiter.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TokenBucketPrecisionTest {

    @Test
    @DisplayName("Verify monotonic fractional math precision and saturation limits")
    void testFractionalMathAndSaturation() throws InterruptedException {
        // Bucket with max 3 tokens, refilling at 10 tokens/sec
        TokenBucket bucket = new TokenBucket(3, 10.0);

        assertTrue(bucket.allowRequest(), "1st should pass");
        assertTrue(bucket.allowRequest(), "2nd should pass");
        assertTrue(bucket.allowRequest(), "3rd should pass");
        assertFalse(bucket.allowRequest(), "4th should fail (Empty)");

        // Wait 100ms (earns exactly 1 token)
        Thread.sleep(100);
        assertTrue(bucket.allowRequest(), "Should allow 1 request after 100ms");
        assertFalse(bucket.allowRequest(), "Should fail immediately after");

        // Wait 500ms (earns 5 tokens, but should cap at max capacity of 3)
        Thread.sleep(500);
        int successfulPicks = 0;
        while (bucket.allowRequest()) {
            successfulPicks++;
        }

        assertEquals(3, successfulPicks, "CRITICAL: Did not clamp at max capacity!");
    }
}