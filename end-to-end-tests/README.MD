# Project Title

## Steps to Run Tests

0. **Setup Database:** Import the `base.sql` database located in the `test-data` folder. Run the following commands from the project root directory.

    ```bash
    dropdb diwi
    createdb diwi
    psql diwi < end-to-end-tests/test-data/base.sql
    ```

1. **Deploy Backend:** Run the `deploy.backend.dev.sh` script from the project root directory.

    ```bash
    ./deploy.backend.dev.sh
    ```

2. **Start Frontend:** Navigate to the `frontend` directory, install dependencies and start the server.

    ```bash
    cd frontend
    yarn && yarn start
    ```

3. **Run End-to-End Tests:** Navigate to the `end-to-end-tests` directory and run the tests.

    ```bash
    cd ../end-to-end-tests
    yarn && yarn run test
    ```
