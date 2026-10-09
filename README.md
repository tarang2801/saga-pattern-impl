# Saga Pattern Implementation

A sample Java-based microservices implementation demonstrating the Saga pattern with Spring Boot, Kafka, and PostgreSQL.

This repository contains three independent services that coordinate through asynchronous events to simulate a distributed order workflow:

- Order service creates orders and initiates the saga
- Inventory service validates product availability and compensates on failures
- Payment service processes payment and triggers rollback when needed

## Architecture

The flow is:

1. Client sends a new order to the Order Service
2. Order Service persists the order and publishes an `order-created` event
3. Inventory Service consumes the event and reserves stock
4. If inventory is available, it publishes `inventory-reserved`
5. Payment Service consumes the reservation and attempts payment
6. On success, it publishes `payment-processed`
7. On failure, it publishes `payment-failed`
8. Inventory and Order services react to compensation events to rollback and mark the order as cancelled

## Services

### 1) saga-order-service
- Exposes REST API for order creation
- Persists orders in PostgreSQL
- Publishes order events to Kafka
- Consumes payment events to update order status

Endpoints:
- `POST /orders`

### 2) saga-inventory-service
- Consumes `order-created` events
- Checks inventory quantity
- Reserves stock and emits `inventory-reserved`
- Emits `quantity-not-available` if stock is insufficient
- Consumes `payment-failed` and releases inventory

### 3) saga-payment-service
- Consumes `inventory-reserved` events
- Randomly simulates success or failure of payment
- Saves payment records in PostgreSQL
- Emits `payment-processed` or `payment-failed`

## Technology Stack

- Java 17
- Spring Boot 3.x / Spring Boot 4.1.1 in pom files
- Apache Kafka
- PostgreSQL
- JPA / Hibernate
- Maven

## Repository Structure

```text
.
├── saga-order-service
│   ├── src/main/java
│   ├── src/main/resources
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
├── saga-inventory-service
│   ├── src/main/java
│   ├── src/main/resources
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
├── saga-payment-service
│   ├── src/main/java
│   ├── src/main/resources
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
└── .idea
```

## Prerequisites

Before running the services, make sure you have:

- Java 17+
- Maven
- Kafka running locally on `localhost:9092`
- PostgreSQL running on `localhost:5432`
- Database named `saga_db` with user `postgres` and password `password`

## Run the Project

Open three terminals and run each service from its directory:

```bash
cd saga-order-service
./mvnw spring-boot:run
```

```bash
cd saga-inventory-service
./mvnw spring-boot:run
```

```bash
cd saga-payment-service
./mvnw spring-boot:run
```

## Example Request

Create an order using the Order Service:

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 101,
    "productName": "Laptop",
    "quantity": 1
  }'
```

Expected response:

```text
Order created and saga started
```

## Notes

This is a lightweight educational implementation of the Saga pattern. It demonstrates the core idea of distributed transaction coordination using events and compensation logic, rather than a production-grade orchestration framework.

## Future Improvements

- Add Docker Compose for Kafka and PostgreSQL
- Add API validation and error handling
- Add centralized configuration
- Add distributed tracing and monitoring
- Use Event Sourcing or a workflow engine for more robust orchestration

## License

This project does not currently include a license file. If you want to publish or distribute it, consider adding an appropriate license such as MIT or Apache 2.0.
