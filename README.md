# vng

## Setup development environment

### Backend development

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

Essentially you need to run the following commands to import the test sets at the moment:

```shell
psql diwi < backend/src/main/resources/db/sql/diwi_baseline_20240117_114546.sql
psql diwi < backend/src/main/resources/db/sql/vng_projects_testdata.sql
```

Or in docker:

```shell
docker compose exec -T database psql --user diwi diwi < backend/src/main/resources/db/sql/diwi_baseline_20240117_114546.sql
docker compose exec -T database psql --user diwi diwi < backend/src/main/resources/db/sql/vng_projects_testdata.sql
```

## Deploy on production

TBD

## Development

### HTTP API guidelines

We use the following query parameters for paginated queries:

- pageNumber (1 based)
- pageSize (must be larger than 0)

We use the following query parameters for sorting

- sortColumn
- sortDirection (ASC or DESC)

We use the following for filterings:

- filterColumn
- filterValue
- filterCondition (This can be a list e.g: `filterValue[]=a&filterValue[]=b)

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
