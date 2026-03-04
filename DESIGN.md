# System Design & Research Objectives

## Research Focus
This project serves as a technical case study on the following:
1. **Virtual Thread Performance:** Benchmarking Java 21 thread-per-request models against traditional thread pools in high-concurrency rate-limiting scenarios.
2. **Predictive Accuracy:** Comparing SLM-based scaling triggers against standard K8s Horizontal Pod Autoscaler (HPA) latency.
3. **ARM Optimization:** Evaluating the cost-to-performance ratio of running AI-driven infrastructure on OCI Ampere A1 shapes.

## Implementation Phases
- [ ] **Phase 1:** Core Rate Limiter engine with Redis and Virtual Threads.
- [ ] **Phase 2:** Telemetry exporter and Prometheus integration.
- [ ] **Phase 3:** SLM integration for pattern recognition.
- [ ] **Phase 4:** OCI/OKE scaling automation and benchmarking.
