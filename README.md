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

### Setup the DB

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
- Only `DIWI_DB_USERNAME` and `DIWI_DB_PASSWORD` are needed in `.env`. See `.env.backend.dev.example`.

### Recreating the database

First drop the database. For this you need to stop the backend and close any existing connections to the database.

```shell
dropdb diwi
```

Then you can execute the steps in [the setup chapter](#setup-the-db).

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

### Mailhog settings (with keycloak)

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

### Install Eclipse for java EE

- Download the installer (https://www.eclipse.org/downloads/)
- Extract the installer
- Run `eclipse-inst`
- Choose 'Eclipse IDE for Enterprise Java and Web Developers'
- Wait
- Done

### Install Project Lombok

- If you've already run maven, the lombok installer is in the repo. otherwise you can download lombok.jar from https://projectlombok.org/download.
- Run the installer from the maven repo.

```

java -jar ~/.m2/repository/org/projectlombok/lombok/1.18.30/lombok-1.18.30.jar

```

- -or- from the downloaded file

```

java -jar ~/Downloads/lombok.jar

```

- In the installer choose the eclipse folder. Which normally is something like: `~/eclipse/jee-2023-12/eclipse/`
- Click install
- Close the installer

### Setup the eclipse workspace

- Open eclipse and choose a workspace directory (e.g. the root folder of the repo. **Not** the `backend` directory.)
- Import the project

  - Open the import File → Import
  - Select Maven → Existing Maven Projects
  - Click Browse and select the backend directory
  - Click Finish

### Setup the development server

- In the servers view(the servers view is located by default next to the console at the bottom of the window), select 'Click this link to create a new one'
- Select tomcat v10.1 (Or look at the docker compose file for the current version)
- In the next window select download and install. Create a new dir somewhere outside the project directory. Eclipse will download the server in the background so it will take some time for the 'next' button to be activated.
- In the next window, move the backend to the configured side.
- Double click the server in the servers view. This opens the run configuration. (You can also go here from the dropdown next to the play button in the toolbar.)
- Click the modules tab at the bottom of the run configuration.
- Set the path of the application to / by clicking the edit button and changing `/diwi` to `/`.
- Click the play button in the servers view to start the server.

### HTTP API guidelines

We use the following query parameters for paginated queries:

- pageNumber (1 based)
- pageSize (must be larger than 0)

We use the following query parameters for sorting

- sortColumn
- sortDirection (ASC or DESC)

We use the following for filterings:

- filterColumn
- filterValue (This can be a list: filterValue=a&filterValue=b&filterValue=c)
- filterCondition

Also see FilterPaginationSorting.java

### How to query the tables for a specific 'peildatum'/reference date

For querying for the current date you can just check if the end date of the table is `NULL`. e.g:

```sql
SELECT
    *
FROM
    diwi_testset_simplified.gemeente_state gs
WHERE
    gs.change_end_date IS NULL
```

For a specific date the query is more complex. There can not be any overlapping state/changelog entries:

```sql
SELECT
    *
FROM
    diwi_testset_simplified.gemeente_state gs
WHERE
    gs.change_start_date <= :reference_date AND (gs.change_end_date IS NULL OR gs.change_end_date > :reference_date)
```

## Generating API Types

```shell
./update-types.sh
```

- Updates `openapi.json` and `frontend/src/types/schema.d.ts`.

---

## Deployment on Production

- Copy `.env.production.example` to `.env`
- Set a secure password for the database in the .env file
- Configure keycloak with a new client and enter the parameters in the .env file
- Enter the parameters for the email server in the .env file
- Call `./deploy.sh`

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
