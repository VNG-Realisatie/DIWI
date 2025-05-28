# diwi-source-export

## Overview

**DIWI** is a platform designed for Dutch municipalities, providing tools for data management and analysis. This document describes how to set up a development environment and deploy an instance of DIWI on a virtual machine (VM).

---

## Architecture

- **Backend:** Java, connects to a PostgreSQL database.
- **Frontend:** React (TypeScript) with Vitejs.
- **Authentication:** Keycloak (Dockerized).
- **Email Testing:** Mailhog (Dockerized).
- **Database:** PostgreSQL.
- **Containerization:** Docker & Docker Compose for local development and deployment.

---

## Tools Used

| Tool           | Purpose                       |
| -------------- | ----------------------------- |
| Docker         | Containerization              |
| Docker Compose | Multi-container orchestration |
| PostgreSQL     | Database                      |
| Keycloak       | Authentication                |
| Mailhog        | Email testing (local)         |
| Node.js/NPM    | Frontend development          |
| Java (Maven)   | Backend development           |
| GDAL           | GIS tools                     |

---

## Getting Started: Development Environment

### Prerequisites

- **PostgreSQL:** `sudo apt install postgresql`
- **GDAL:** `sudo apt install gdal-bin`
- **Docker & Docker Compose:** [Install Docker](https://docs.docker.com/get-docker/)
- **Node.js & NPM:** [Install Node.js](https://nodejs.org/)
- **Java 17+ & Maven:** [Install Java](https://adoptium.net/) and [Maven](https://maven.apache.org/)
- **Eclipse IDE for Java EE:** [Download Eclipse](https://www.eclipse.org/downloads/)

### Database Setup

```shell
createuser diwi
psql -c "ALTER USER \"diwi\" WITH PASSWORD 'diwi'"
createdb diwi -O diwi
```

### Start Backend & Keycloak

```shell
./deploy.backend.dev.sh
```

- This starts the backend and a local Keycloak instance.
- All tables are created via migration scripts.

### Add Admin User

```shell
./addUsers.sh
```

- Adds an admin user and assigns the DIWI admin role.

### Start Frontend

```shell
cd frontend
npm install
npm start
```

- Only `DIWI_DB_USERNAME` and `DIWI_DB_PASSWORD` are needed in `.env`. See `.env.backend.dev.example`.

### Mailhog (Email Testing)

- Mailhog runs by default at [http://localhost:8025](http://localhost:8025).
- Configure Keycloak email settings as:
  - From: `mailhog@phinion.com`
  - Host: `localhost`
  - Port: `1025`

---

## Development Scripts

| Script                   | Description                                       |
| ------------------------ | ------------------------------------------------- |
| `addUsers.sh`            | Adds admin user to Keycloak and assigns DIWI role |
| `compose.dev.sh`         | Helper for running Docker Compose files           |
| `deploy.backend.dev.sh`  | Runs backend with external DB and local Keycloak  |
| `deploy.keycloak.dev.sh` | Starts Keycloak in Docker                         |
| `deployNoPull.sh`        | Deploys production without pulling new changes    |
| `deploy.sh`              | Deploys production after pulling latest changes   |
| `kcadm.sh`               | Runs Keycloak CLI in container                    |
| `mergeBackToDevelop.sh`  | Creates merge request from release to develop     |
| `update-types.sh`        | Updates OpenAPI JSON and TypeScript types         |
| `version.sh`             | Creates env vars with version info                |

---

## Backend Development

### Test Database Setup

```shell
dropdb diwi_test # Optional when database already exists
createuser diwi
psql -c "ALTER USER \"diwi\" WITH PASSWORD 'diwi'"
createdb diwi_test -O diwi
psql -d diwi_test -c 'ALTER SCHEMA "public" OWNER TO "diwi"'
```

### Eclipse & Lombok

- Install Eclipse IDE for Java EE.
- Install Project Lombok:
  ```shell
  java -jar ~/.m2/repository/org/projectlombok/lombok/1.18.30/lombok-1.18.30.jar
  ```
- Import backend as Maven project.

### Running the Development Server

- Configure Tomcat v10.1 in Eclipse.
- Set application path to `/`.
- Start the server from Eclipse.

---

## Generating API Types

```shell
./update-types.sh
```

- Updates `openapi.json` and `frontend/src/types/schema.d.ts`.

---

## Deployment on Production

1. Copy `.env.production.example` to `.env`.
2. Set secure DB password and configure Keycloak/email in `.env`.
3. Run:
   ```shell
   ./deploy.sh
   ```

---

## Querying Data

- **Current records:** `WHERE change_end_date IS NULL`
- **Records at reference date:**
  ```sql
  WHERE change_start_date <= :reference_date AND (change_end_date IS NULL OR change_end_date > :reference_date)
  ```

---

## Glossary

| Dutch       | English        |
| ----------- | -------------- |
| Peildatum   | Reference date |
| Buurt       | Neighborhood   |
| Wijk        | District       |
| Gemeente    | Municipality   |
| Eigenaar    | Owner          |
| Beleidsdoel | Policy goal    |
| Perceel     | Plot           |

---
