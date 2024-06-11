# Project Title

## Steps to Run Tests

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
