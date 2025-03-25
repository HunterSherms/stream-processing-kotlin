# Stream Processing in Kotlin with Kafka Streams and RocksDB

A collection of simple, incremental examples demonstrating how to use **Kafka Streams** with **Kotlin** and **Spring Boot**. 
These examples highlight how to:

1. Read and transform data in a Kafka Streams application.
2. Store and serve data in different ways (in-memory vs. RocksDB on disk).

Each example builds on the previous one, showcasing a step-by-step evolution of your streaming application.

---

## Table of Contents
- [Overview](#overview)
- [Example 1: Kafka Streams - Sanitizing User Data](#example-1-kafka-streams---sanitizing-user-data)
- [Example 2: In-Memory Cache with HTTP Endpoint](#example-2-in-memory-cache-with-http-endpoint)
- [Example 3: RocksDB Storage for Persistent Caching](#example-3-rocksdb-storage-for-persistent-caching)

---

## Overview

**Kafka Streams** allows you to process real-time data at scale. By combining **Kotlin** and **Spring Boot**, 
you can build concise, robust applications that can transform, filter, and enrich data streams.

This project provides three examples:
1. **Sanitizing User Data** – removing personally identifiable information (PII) before forwarding records to another Kafka topic.
2. **Serving Data In-Memory** – reading sanitized data into an in-memory cache and exposing it via a simple HTTP endpoint.
3. **Persisting Data on Disk** – swapping out the in-memory cache for **RocksDB**, ensuring the data is durable even if the application restarts or fails, and can scale beyond the memory available to most servers.

---

## Example 1: Kafka Streams - Sanitizing User Data

[**Example 1**](https://github.com/HunterSherms/stream-processing-kotlin/tree/main/src/main/kotlin/com/huntersherms/streamprocessingkotlin/example1)

### What does it do?
- Reads user objects (with possible PII) from an **input Kafka topic**.
- Removes sensitive information by removing the sensitive properties (e.g., names, addresses, emails).
- Writes the sanitized data to a **new Kafka topic** for downstream usage.

### Why is this useful?
- **Compliance**: Many regulations (GDPR, CCPA, etc.) require organizations to protect personal data.
- **Security**: Removing or masking PII reduces the risk of data leaks and misuse.
- **Data Hygiene**: Ensures only necessary attributes are forwarded downstream, keeping data pipelines clean and efficient, and ensuring access is limited to required information.

---

## Example 2: In-Memory Cache with HTTP Endpoint

[**Example 2**](https://github.com/HunterSherms/stream-processing-kotlin/tree/main/src/main/kotlin/com/huntersherms/streamprocessingkotlin/example2)

### What does it do?
- Reads the sanitized user objects from the **new Kafka topic** created in Example 1.
- Stores the objects in an **in-memory cache** for fast lookups.
- Exposes **RESTful HTTP endpoints** so you can query the current state of user data.

### Why is this useful?
- **Low Latency**: In-memory lookups are fast, allowing near real-time data retrieval.
- **Easy Access**: An HTTP endpoint makes the data readily available to other services, dashboards, or front-end applications.

### When is this useful?

1. When your data fits easily into the memory available on your server(s).
2. When you don't need strong consistency guarantees on write.

---

## Example 3: RocksDB Storage for Persistent Caching

[**Example 3**](https://github.com/HunterSherms/stream-processing-kotlin/tree/main/src/main/kotlin/com/huntersherms/streamprocessingkotlin/example3)

### What does it do?
- Builds on Example 2, but **replaces the in-memory store** with an on-disk **RocksDB** instance.
- Maintains local state even after application restarts or after system failures.
- Continues to expose the data via a **RESTful HTTP endpoint**.

### Why is this useful?
- **Persistence**: RocksDB ensures state is not lost if the application crashes or needs to be redeployed.
- **Scalability**: Disk-based storage allows you to handle data volumes that surpass available memory.
- **Reliability**: Essential for mission-critical applications requiring high availability and fault tolerance.

---

## Getting Started

1. **Clone the project**:
   ```bash
   git clone https://github.com/HunterSherms/stream-processing-kotlin.git
   cd stream-processing-kotlin
   mvn clean install
