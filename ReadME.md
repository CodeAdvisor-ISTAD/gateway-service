### Gateway Server README 📋

---

![CodeAdvisors Logo](http://167.172.78.79:8090/api/v1/files/preview?fileName=b5d01918-2824-48d7-83e0-fb557ce6bd73_2024-12-21T18-28-24.856529397.jpg)

## Handle By ***Yith Sopheakta***

## Overview 🌐
The **Gateway Server** is the entry point to the **CodeAdvisors** platform. It serves as a central API gateway that routes requests to various services while ensuring secure and efficient communication using OAuth2 and Spring Cloud Gateway.

---

## Features ✨

- **Dynamic Routing**: Routes requests to Identity Service and front-end applications.
- **OAuth2 Authentication**: Integrates with the Identity Service for secure token relay.
- **Custom Matchers**: Configurable permit and security matchers for client request filtering.
- **Health & Management Endpoints**: Includes readiness, liveness probes, and actuator endpoints for monitoring.
- **Integration with Next.js Frontend**: Supports seamless routing to the main Next.js frontend.

---

## Technologies Used ⚙️

- **Spring Cloud Gateway**: For routing and filtering HTTP requests.
- **OAuth2 Security**: For client authentication and authorization.
- **Actuator**: To provide operational insights and monitoring.
- **Next.js Frontend**: Routes requests to the main client-side application.

---

## Prerequisites 📦

1. **JDK 21**: Required for building and running the server.
2. **Identity Service**: Ensure the Identity Service is running at `http://127.0.0.1:9090`.
3. **Frontend Service**: Ensure the main Next.js frontend application is running at `http://127.0.0.1:3000`.

---

## Configuration 🔧

The following configurations are important for setting up the Gateway Server:

### Security Matchers
- **Client Security Matchers**:
  ```yaml
  /login/**, /oauth2/**, /api/v1/auth/user/**, /home/**, /logout/**, /identity/**, /en/**, /auth/**, /business/**, /profile/**
  ```
- **Client Permit Matchers**:
  ```yaml
  /login/**, /oauth2/**, /api/v1/auth/user/**, /home/**, /en/**, /auth/**, /identity/**, /contactSupport/**, /error/**, /actuator/**, /info/**, /health/**, /prometheus/**, /business/**, /profile/**
  ```

### Gateway Routes
- **Identity Service**:
    - URI: `http://127.0.0.1:9090`
    - Path: `/identity/**`
- **CodeAdvisors UI**:
    - URI: `http://127.0.0.1:3000`
    - Path: `/**`

---

## Running the Gateway Server 🚀

1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/gateway-server.git
   cd gateway-server
   ```

2. Build the project:
   ```bash
   ./gradlew build
   ```

3. Start the Gateway Server:
   ```bash
   ./gradlew bootRun
   ```

4. Access the Gateway Server at `http://localhost:8168`.

---

## Testing the Gateway 🧪

1. **Identity Service**:
    - Test routes prefixed with `/identity/**` to ensure proper routing.
2. **CodeAdvisors UI**:
    - Test the default route `/**` for routing to the primary Next.js application.
3. **Health Monitoring**:
    - Access Actuator endpoints like `/actuator/health` or `/actuator/gateway` for operational insights.

---

## License 📜
This project is licensed under the MIT License. See the LICENSE file for more details.

---

Built with ❤️ by the CodeAdvisors Team.