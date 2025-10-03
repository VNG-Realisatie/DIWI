| Eigenaar | Ingevuld door |
| --- | --- |
| Fysieke Leefomgeving | Jeroen Ruig |
<hr/>

# VNG - DIWI

**DIWI** is a platform designed for Dutch municipalities, providing tools for data management and analysis. This document describes how to set up a development environment and deploy an instance of DIWI on a virtual machine (VM).

- [VNG - DIWI](#vng---diwi)
  - [Architecture](#architecture)
  - [Tools Used](#tools-used)
  - [Getting Started: Development Environment](#getting-started-development-environment)
    - [Prerequisites](#prerequisites)
      - [Optional](#optional)
    - [Git Hooks Setup](#git-hooks-setup)
    - [Setup the DB](#setup-the-db)
    - [Start Backend \& Keycloak](#start-backend--keycloak)
      - [Creating an admin user](#creating-an-admin-user)
    - [Recreating the database](#recreating-the-database)
    - [Resetting the keycloak database](#resetting-the-keycloak-database)
    - [Start Frontend](#start-frontend)
    - [Mailhog settings](#mailhog-settings)
    - [Generate types from backend api](#generate-types-from-backend-api)
    - [Setup Backend development](#setup-backend-development)
      - [Setup test DB](#setup-test-db)
    - [Back end development](#back-end-development)
    - [Back end development with eclipse](#back-end-development-with-eclipse)
      - [Install Eclipse for java EE](#install-eclipse-for-java-ee)
      - [Install Project Lombok](#install-project-lombok)
      - [Setup the eclipse workspace](#setup-the-eclipse-workspace)
      - [Setup the development server](#setup-the-development-server)
    - [HTTP API guidelines](#http-api-guidelines)
    - [How to query the tables for a specific 'peildatum'/reference date](#how-to-query-the-tables-for-a-specific-peildatumreference-date)
  - [Generating API Types](#generating-api-types)
  - [Deployment on Production](#deployment-on-production)
  - [Scripts](#scripts)
  - [Glossary](#glossary)

## Architecture

- **Backend:** Java, connects to a PostgreSQL database.
- **Frontend:** React (TypeScript) with Vitejs.
- **Authentication:** Keycloak (Dockerized).
- **Email Testing:** Mailhog (Dockerized).
- **Database:** PostgreSQL.
- **Containerization:** Docker & Docker Compose for local development and deployment.

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

## Getting Started: Development Environment

### Prerequisites

- **PostgreSQL:** `sudo apt install postgresql`
- **GDAL:** `sudo apt install gdal-bin`
- **Docker & Docker Compose:** [Install Docker](https://docs.docker.com/get-docker/)
- **Node.js & NPM:** [Install Node.js](https://nodejs.org/)
- **Java 17+ & Maven:** [Install Java](https://adoptium.net/) and [Maven](https://maven.apache.org/)

#### Optional

- **Eclipse IDE for Java EE:** [Download Eclipse](https://www.eclipse.org/downloads/)
- [pre-commit](#git-hooks-setup) for git hooks

### Git Hooks Setup

Before starting development, you must set up the Git pre-commit hook. This is recommended to maintain consistent code formatting across the project and only needs to be done once per clone of the repository.

We use the [pre-commit](https://pre-commit.com/index.html) framework for the hooks.

You can install it using [pipx](https://pipx.pypa.io/stable/)

```shell
pipx install pre-commit
pre-commit-install
```

[uv](https://docs.astral.sh/uv/)

```shell
uvx pre-commit isntall
```

### Setup the DB

Set up the database and the user:

```shell
createuser diwi
psql -c "ALTER USER \"diwi\" WITH PASSWORD 'diwi'"
createdb diwi -O diwi
```

### Start Backend & Keycloak

You can start the back end and keycloak using docker.

```shell
./deploy.backend.dev.sh
```

- This starts the backend and a local Keycloak instance.
- All tables are created via migration scripts.
- (Optional) You can change the config in `.env`. See `.env.backend.dev.example`.

You can run only the supporting services if needed. e.g. if you want run the backend in [eclipse](#back-end-development)

```shell
./deploy.keycloak.dev.sh
```

#### Creating an admin user

After starting keycloak for the first time you need to create an admin user once. The username is admin and the password is admin.

```shell
./addUsers.sh
```

### Recreating the database

In case you want to start with a clean slate.

First drop the database. For this you need to stop the backend and close any existing connections to the database.

```shell
dropdb diwi
```

Then you can execute the steps in [the setup chapter](#setup-the-db).

### Resetting the keycloak database

First remove the data/keycloak directory. You might want to clear the main data base as well. See [Recreating the database](#recreating-the-database). As the keycloak user id's are stored in the main database too.

```shell
sudo rm -r data/keycloak
```

Then add the user(s) again using the addUsers.sh script. See [Creating an admin user](#creating-an-admin-user)

### Start Frontend

```shell
cd frontend
yarn && yarn start
```

### Mailhog settings

You only need this for keycloak and when developing in eclipse. The back-end is automatically configured correctly when running in docker.

- Mailhog runs by default at [http://localhost:8025](http://localhost:8025).
- Configure Keycloak email settings as:
  - From: `mailhog@phinion.com`
  - Host: `localhost`
  - Port: `1025`
  - We use mailhog for testing when developing. By default you can access it on http://localhost:8025.

### Generate types from backend api

You can generate typescript types matching the API with the following script:

```bash
./update-types.sh
```

This will update [openapi.json](./api/openapi.json) and [schema.d.ts](./frontend/src/types/schema.d.ts).

You can use the types in typescript as follows:

```Typescript
import { components } from "../types/schema";

// Directly using it is a bit cumbersome so it is a good idea to use type aliases
export type CategoryType = components["schemas"]["SelectDisabledModel"];
```

You can use Typescript utility types if you want to derive another type from an API type. e.g. if want to remove the id:

```Typescript
export type Project = components["schemas"]["ProjectSnapshotModel"];
export type ProjectWithoutId Omit<Project, "projectId">
```

If some values values seem optional in the openapi definition, but they are not, You need to add the `@JsonProperty(required = true)` annotation in the java model.

### Setup Backend development

#### Setup test DB

The tests expect a db called diwi_test owned by the diwi user

```shell
dropdb diwi_test # Optional when database already exists
createuser diwi
psql -c "ALTER USER \"diwi\" WITH PASSWORD 'diwi'"
createdb diwi_test -O diwi
psql -d diwi_test -c 'ALTER SCHEMA "public" OWNER TO "diwi"'
```

### Back end development

The back-end is written in Java EE that runs in a tomcat container. You can run the back-end and the supporting service using a docker compose file. See [Start the back-end development environment in docker](#start-the-back-end-development-environment-in-docker).

When that is running you can use any editor to edit the back-end and docker compose watch mode will update the running code automatically.

### Back end development with eclipse

If you want to develop using eclipse you can use the following steps to set it up.

#### Install Eclipse for java EE

- Download the installer (https://www.eclipse.org/downloads/)
- Extract the installer
- Run `eclipse-inst`
- Choose 'Eclipse IDE for Enterprise Java and Web Developers'
- Wait
- Done

#### Install Project Lombok

- If you've already run maven, the lombok installer is in the repo. otherwise you can download lombok.jar from https://projectlombok.org/download.
- Run the installer from the maven repo.

```shell
java -jar ~/.m2/repository/org/projectlombok/lombok/1.18.30/lombok-1.18.30.jar
```

- -or- from the downloaded file

```shell
java -jar ~/Downloads/lombok.jar
```

- In the installer choose the eclipse folder. Which normally is something like: `~/eclipse/jee-2023-12/eclipse/`
- Click install
- Close the installer

#### Setup the eclipse workspace

- Open eclipse and choose a workspace directory (e.g. the root folder of the repo. **Not** the `backend` directory.)
- Import the project

  - Open the import File → Import
  - Select Maven → Existing Maven Projects
  - Click Browse and select the backend directory
  - Click Finish

#### Setup the development server

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

Deploying on production requires

- Setting up a separate keycloak server that you can link to for authentication.
- A reverse proxy for https termination.

Do the following steps

- Copy [`.env.production.example`](./.env.production.example) to `.env`
- Set a secure password for the database in the .env file
- Configure keycloak with a new client and enter the parameters in the .env file
- Enter the parameters for the email server in the .env file
- Call `./deploy.sh`

## Scripts

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
