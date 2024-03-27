# vng

## Development

### Front end development

You can start the back end and keycloak using docker.

First login to git.phinion.com if needed. You need an access token with `read_registry` permissions. You only need to do this once.

```shell
docker login git.phinion.com
```

Then start the backend and keycloak:

```shell
./deploy.backend.dev.sh
```

Create a user with the username and password 'admin' in keycloak:

```
./addUsers.sh
```

Start the front end in a dev server as follows:

```shell
cd frontend
yarn && yarn start
```

You shouldn't need any settings in the .env file other than `DIWI_DB_USERNAME` and `DIWI_DB_PASSWORD`. See `.env.backend.dev.example`.

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
- Set the path of the application to / by clicking the edit button and changing `/vng` to `/`.
- Click the play button in the servers view to start the server.

## Setup the DB with the test data

Note: This is still in development and subject to change.

- Make sure the empty database exists / create it.
- Start the backend (all the tables will be created through migration scripts).
- Run the following command to import the test sets:

```shell
psql diwi < backend/src/main/resources/db/sql/vng_projects_testdata.sql
psql diwi < backend/src/main/resources/db/sql/vng_woningbloks_testdata.sql
```

Or in docker:

```shell
docker compose exec -T database psql --user diwi diwi < backend/src/main/resources/db/sql/vng_projects_testdata.sql
docker compose exec -T database psql --user diwi diwi < backend/src/main/resources/db/sql/vng_woningbloks_testdata.sql
```

## Deploy on production

- Copy `.env.production.example` to `.env`
- Set a secure password for the database in the .env file
- Configure keycloak with a new client and enter the parameters in the .env file
- Call `./deploy.sh`

## Development

### Calling the backend from the front end

To make sure we don't get redirect responses when we do `fetch` requests we need to use the wrapper `diwiFetch` from `src/utils/request.ts`.

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

## Glossary

As the project is meant for Dutch municipalities there are lots of Dutch terms.

| Dutch       | English        |
| ----------- | -------------- |
| Peildatum   | Reference date |
| Gemeente    | Municipality   |
| Buurt       | Neighbourhood  |
| Eigenaar    | Owner          |
| Beleidsdoel | Policy goal    |
| Perceel     | Plot           |
