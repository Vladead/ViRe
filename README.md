# ViRe Backend <!-- Project Title -->

<p align="center">
  A fast, private & secure VPN service API.
</p>

[![License](https://img.shields.io/github/license/Vladead/ViRe)](LICENSE)

---

## Table of Contents
1. [Purpose](#purpose)
2. [Getting Started](#getting-started)
3. [Tech Stack](#tech-stack)
4. [API Docs](#api-docs)
5. [Contributing](#contributing)
6. [License](#license)

---

## Purpose
ViRe Backend is the service core of the **ViRe VPN** project.

* User and subscription management
* JWT-based authentication
* REST endpoints for web clients

---

## Getting Started

### Prerequisites
- **Java 24+**
- **Docker & Docker Compose**

### Local Setup
```bash
# 1 - clone the repo
git clone git@github.com:Vladead/ViRe.git

# 2 - create env file and use .env.example as reference
# commented variables have default values and you can use them as is
cd ViRe
cp .env.example .env

# 3 - spin up containers
# this will use default profile and start both db and backend containers 
docker compose up -d

# you can use dev profile to start only db container and test app via editor
# docker compose --profile dev up -d
```

---

## Tech Stack

| Layer     | Stack/Libraries                    |
|-----------|------------------------------------|
| Language  | Java 24 (Temurin)                  |
| Framework | Spring Boot 3.5, Spring Security 6 |
| DB        | PostgreSQL 17, Liquibase           |
| Auth      | JWT (jjwt 0.12.6)                  |
| DevOps    | Docker, Docker Compose             |

---

## API Docs

Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Authorization

**If you don’t have a user in the database:**

1. Send a `POST` request to `/api/auth/register` with `username`, `email`, and `password` - you will receive a `token`.
2. In Swagger UI, click **"Authorize"** at the top of the page (or the lock icon next to an endpoint), paste the token,
   and press **Authorize**.
3. Protected endpoints are now accessible.

**If you already have a user in the database:**

1. Send a `POST` request to `/api/auth/login` with `email` and `password` - you will receive a `token`.
2. In Swagger UI, click **"Authorize"** at the top of the page (or the lock icon next to an endpoint), paste the token,
   and press **Authorize**.
3. Protected endpoints are now accessible.

---

## Contributing

At the moment I'm not accepting external contributions.
Feel free to open issues for bugs and feature requests.

---

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.