# Orbital: Smart Building Management Backend Service

A high-performance, real-time backend system for managing and controlling smart building devices, built with modern Kotlin and Ktor. It bridges professional home automation expertise with scalable software engineering.

## 📋 Table of Contents
- [Overview](#overview)
- [Motivation & Expertise](#motivation--expertise)
- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Project Architecture](#-project-architecture)
- [Getting Started](#-getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation & Running](#installation--running)
- [API Documentation](#-api-documentation)
- [Roadmap & Future Enhancements](#-roadmap--future-enhancements)
- [Contributing](#-contributing)
- [License](#-license)

## Overview

**Orbital** is a backend service designed to manage, monitor, and control smart devices within a commercial or residential building. It provides a structured REST API for device management and leverages WebSockets for real-time state updates and command execution, mimicking the core requirements of professional home automation and AV control systems.

This project is a portfolio piece that demonstrates the application of modern backend software engineering principles—such as Clean Architecture, domain-driven design, and real-time communication—to a domain traditionally handled by proprietary systems (e.g., Crestron, Control4).

## Motivation & Expertise

This project bridges a six-year career in the **home automation and AV integration industry** with professional software development. The domain model is directly informed by hands-on experience with systems like Crestron, Control4, Lutron, and KNX, where managing device states, zones, and real-time feedback is critical.

The goal is to reimagine the control layer of such systems using scalable, standards-based technologies (Kotlin, WebSockets, REST) rather than vendor-specific tools, focusing on:
*   **Maintainability:** Creating a codebase that is easier to understand and modify than traditional automation programming.
*   **Interoperability:** Providing a modern API that can be integrated with various frontends and third-party services.
*   **Cost-Effectiveness:** Demonstrating how software engineering practices can streamline the delivery and support of complex systems.

## ✨ Features

*   **Device Management:** Full CRUD operations for smart devices (Lighting, HVAC, AV equipment) with detailed metadata and categorization.
*   **Real-Time Control:** Bi-directional WebSocket connections for instant device state updates and command broadcasting.
*   **Zoned Control:** Logical grouping of devices into zones (e.g., "Conference Room", "Lobby") for bulk operations.
*   **Clean Architecture:** Separation of concerns with distinct Domain, Data, and Presentation layers for testability and maintainability.
*   **Thread-Safe Repository:** An in-memory data layer using `ConcurrentHashMap` for safe concurrent access.

## 🛠 Technology Stack

| Layer | Technology |
| :--- | :--- |
| **Language** | ![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white) |
| **Framework** | ![Ktor](https://img.shields.io/badge/Ktor-000000?style=for-the-badge&logo=ktor&logoColor=white) |
| **Build Tool** | ![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white) |
| **Serialization** | `kotlinx.serialization` (JSON) |
| **Concurrency** | `kotlinx.coroutines`, `ConcurrentHashMap` |

## 🏗 Project Architecture

src/
├── domain/                           # Core business logic
│   ├── model/                        # SmartDevice, Zone, DeviceCommand, etc.
│   └── repository/                   # DeviceRepository interface
├── data/                             # Data access layer
│   ├── repository/                   # InMemoryDeviceRepository
│   └── mock/                         # MockDataProvider
└── application/ & routes/            # Ktor setup & HTTP/WebSocket routes


*   **Domain Layer:** Contains pure Kotlin data classes (`SmartDevice`, `DeviceCommand`) and business interfaces. It has no external dependencies.
*   **Data Layer:** Implements the repository interface, currently with an in-memory store. This is where future database integration (e.g., PostgreSQL) will reside.
*   **Presentation Layer:** Ktor-specific code defines REST endpoints (`GET /api/devices`) and WebSocket routes (`/ws/devices`) that delegate logic to the domain.

## 🚀 Getting Started

### Prerequisites
*   **JDK 17** or higher
*   **Gradle 7.6** or higher (Wrapper included)

### Installation & Running

1.  **Clone the repository**
    ```bash
    git clone https://github.com/Jbass-101/Orbital-Backend.git
    cd Orbital-Backend
    ```

2.  **Build the project**
    ```bash
    ./gradlew build
    ```

3.  **Run the server**
    ```bash
    ./gradlew run
    ```
    The server will start on `http://localhost:8080`. You should see log output confirming the startup.

## 📚 API Documentation

### REST API (HTTP)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/devices` | Fetch a list of all smart devices. |
| `GET` | `/api/devices/{id}` | Fetch details of a specific device by its ID. |
| `POST` | `/api/devices/{id}/command` | Send a command (e.g., `TurnOn`, `SetClimateMode`) to a device. |

### Real-Time API (WebSocket)
*   **Endpoint:** `ws://localhost:8080/ws/devices`
*   **Purpose:** Connect to receive live state updates for all devices and broadcast commands.
*   **Protocol:** Messages are serialized as JSON. Connect and send a `Subscribe` command to begin receiving updates.

*Example WebSocket Command (JSON):*
```json
{
  "type": "Subscribe",
  "deviceIds": ["device-123", "device-456"]
}

```
## 🗺 Roadmap & Future Enhancements

This project is under active development as part of a career transition to software engineering. High-priority next steps include:

* **Persistent Storage:** Replace the in-memory repository with a PostgreSQL database using JetBrains Exposed.
* **Authentication & Authorization:** Implement JWT-based auth to secure API endpoints and device control.
* **Comprehensive Testing:** Add unit tests for the domain layer and integration tests for Ktor routes.
* **Containerization:** Create a Dockerfile for easy deployment.
* **Live Deployment:** Host a public demo instance on a platform like Railway or Render.

## 🤝 Contributing

As a portfolio project, this repository is primarily for personal development. However, suggestions, feedback, and ideas are always welcome. Please feel free to open an issue to discuss potential improvements.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.