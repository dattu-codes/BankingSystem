# Deployment Architecture & Environment Orchestration

To support decoupled client-server operations and simplify system verification, I configured containerized deployment strategies for the core banking API:

## 1. Containerized Service Deployment (Docker Compose)
I configured a single-command build and execution profile using Docker Compose. This packages the compiled Spring Boot artifact and launches the REST API on a containerized Java Runtime Environment.

### Running Backend Containers:
From the root directory of the repository, execute:
```bash
docker-compose -f deployment/docker-compose.yml up --build
```
This boots up the backend service on `http://localhost:8080` with the embedded database active.

## 2. Decoupled Mobile App Integration
When running in containerized environments or live production clusters, the native Android application connects asynchronously to the backend:
* **Emulator Bridging**: The Android app is pre-configured to point to `http://10.0.2.2:8080/` (which bridges the localhost port inside standard Android Emulators).
* **Production Build**: For live deployments, the API endpoint in the Android source configuration is bound to the hosted backend's DNS/IP.
