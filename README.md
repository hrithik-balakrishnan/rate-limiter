# High-Throughput Distributed Rate Limiter

A production-grade distributed rate limiter built from scratch — benchmarked across three concurrency implementations, with multi-tenant isolation and a chaos-tested core engine.

---

## 🏗 Architecture
- **Phase 1 (Complete):** Local In-Memory Core Engine.
- **Phase 2 (In Progress):** Async HTTP Gateway + Audit Pipeline.
- **Phase 3 (Upcoming):** Redis Cluster + Lua Scripts + Circuit Breaker.

## 🚀 Phase 1 Performance Results
We pushed the JVM to its limits to ensure correctness under load.

| Implementation | Threads | Throughput |
| :--- | :--- | :--- |
| `synchronized` (Intrinsic Locks) | 10,000 | 1.6M RPS (Unstable/Caches) |
| Lock-Free (CAS Engine) | 5,000 | 414,250 RPS (Stable) |
| **Asymmetric Chaos Test** | **10,000** | **4,108 RPS** |

### Chaos Test: Asymmetric Saturation
Simulated a "noisy neighbor" scenario: one tenant generated 80% of total traffic, causing deliberate cache-line contention. The architecture successfully isolated the abuser, keeping normal traffic flows completely unaffected.

![Asymmetric Chaos Test](./docs/chaos-test.png)

---

## 🛠 Tech Stack
- **Language:** Java 21
- **Framework:** Spring Boot
- **Concurrency:** `AtomicLong`, `ConcurrentHashMap`
- **Testing:** JUnit 5, Maven

## 📈 Phase 2 Roadmap
- [ ] HTTP Filter layer (intercepting requests at the Spring Filter chain).
- [ ] Async Audit Pipeline (`ThreadPoolTaskExecutor` decoupled from hot path).
- [ ] PostgreSQL Event Logging for observability.

## 💡 Interview Reference
| Question | Answer |
| :--- | :--- |
| Why CAS over `synchronized`? | Avoids OS-level thread context switching; nanosecond latency. |
| Why did `synchronized` show 1.6M RPS? | Primitive field caching; JVM optimized away memory reads. |
| How does multi-tenancy work? | Isolated `TokenBucket` instances mapped via `ConcurrentHashMap`. |

---

## Author
**Hrithik B**
[LinkedIn](https://www.linkedin.com/in/hrithik-b-a45865319)