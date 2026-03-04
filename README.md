# Trishul-Flux

**Trishul-Flux** is a high-performance, predictive resilience engine designed for modern Cloud-Native Java applications. It bridges the gap between reactive infrastructure scaling and application-level stability.

## The Core Thesis
Traditional cloud scaling (like Kubernetes HPA) is reactive—it responds only after a system is under stress. For Java applications, the "JVM Warm-up Gap" means that by the time a new pod is ready, the system may have already crashed. 

**Trishul-Flux** introduces a pre-emptive feedback loop using Small Language Models (SLMs) to predict surges and prepare infrastructure in advance.

## The Three Prongs of Resilience
Inspired by the Trishul, this system operates on three distinct prongs of control:

1. **Static Flux (Rate Limiting):** Managing the immediate "flow" of requests using Java 21 Virtual Threads and Redis-backed token buckets to ensure the system doesn't drown during spikes.
2. **Predictive Flux (AI Telemetry):** Using lightweight SLMs (Small Language Models) to analyze telemetry patterns and see where the "flow" is heading before the traffic arrives.
3. **Elastic Flux (Infrastructure):** Coordinating the proactive movement of Kubernetes pods on Oracle Cloud Infrastructure (OCI) to match anticipated demand.
