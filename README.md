# 🚦 TrafficProcessor

**TrafficProcessor** is a toy **Spring Boot** project demonstrating a **multimodule, layered (hexagonal) architecture** for modern Java backend development.

![Layered architecture](images/LayeredArchitecture.png)

It showcases clean modular design, domain-driven structure, and strong build-time governance using Maven Enforcer Plugin and google-java-format for consistent code style.

## Prerequisites

* Java 25 (`sdk install java 25-librca`)
* Java 25 native kit for native image (`sdk install java 25.r25-nik`)
* Maven
* Docker

## ✨ Key Features

* 🧩 **Multimodule design** with clear separation of concerns
* 🧱 **Hexagonal architecture** for flexibility and testability
* 🛠️ **Maven Enforcer Plugin** for dependency and build consistency
* 🧹 **Code formatting** with [google-java-format](https://github.com/google/google-java-format)
* 🗄️ **Flyway** for database migrations
* 💾 **Custom DynamoDB session configuration**
* 🔍 **Static code analysis**, **dependency vulnerability checks**, and **automated dependency updates**
* 📊 **Observability**, **benchmarking**, and **code coverage** integration
* 🌍 **Internationalization (i18n)**
* ⚡ **REST Endpoint** with **HATEOAS** support
* 🔗 **gRPC Endpoint** for fast data exchange
* 🐳 **OCI image builds** with **AOT** and **native image** support for optimized deployment

## Running the application

* Run project with maven: `./mvnw clean install -Dmaven.test.skip && ./mvnw spring-boot:run -Prun-app -pl app`
* Create executable JAR: `./mvnw clean install -Dmaven.test.skip`

### 🔐 Test Login Credentials

For testing secured endpoints or accessing integrated services (e.g., via Keycloak-protected APIs):

```
Username: demo  
Password: demo
```

## 🚀 Available Services

After starting the **TrafficProcessor** application, the following services are available locally:

| Service                              | Description                                                       | URL                                            |
| ------------------------------------ | ----------------------------------------------------------------- | ---------------------------------------------- |
| **REST endpoint with Swagger UI**    | REST API documentation and testing interface                      | [http://localhost:8080](http://localhost:8080) |
| **gRPC Endpoint**                    | Exposes gRPC services for inter-module and external communication | [http://localhost:8081](http://localhost:8081) |
| **Grafana**                          | Observability dashboard for metrics visualization                 | [http://localhost:3000](http://localhost:3000) |
| **Keycloak**                         | Authentication and authorization server                           | [http://localhost:7080](http://localhost:7080) |
| **Kafka UI**                         | Interface for inspecting and managing Kafka topics and events     | [http://localhost:8083](http://localhost:8083) |


### 🏗️ Project Structure

The project follows a **multimodule layout** designed for scalability, separation of concerns, and reusability:

```
trafficprocessor/
├── core/          # Core business logic (domain model, entities, and use cases)
├── adapter/       # Persistence, Presentation and DevOps layers (DB, Kafka, Security, Observability ...)
├── app/           # Main entry point (Spring Boot application)
```

🧭 **Core** Domain model and logic — independent of frameworks or external systems.
🔌 **Adapter** Handles persistence, messaging and exposes external endpoints (REST/gRPC)
🚀 **App** bootstraps the runtime environment and ties all modules together.
