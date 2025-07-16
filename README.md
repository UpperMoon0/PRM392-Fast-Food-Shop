# PRM392-Fast-Food-Shop

This project is a fast food shop application with a frontend Android application and a Node.js backend.

## Backend Setup

1.  Navigate to the `be` directory:
    ```bash
    cd be
    ```
2.  Install the dependencies:
    ```bash
    npm install
    ```
3.  Create a `.env` file from the template:
    ```bash
    copy .env.template .env
    ```
4.  Update the `.env` file with your Stripe secret key.
5.  Run the backend server using the provided script:
    ```bash
    run.bat
    ```
    Alternatively, you can run the server directly:
    ```bash
    node server.js
    ```

## Frontend Setup

1.  Open the `fe` directory in Android Studio.
2.  If you have any frontend-specific environment variables, create a `.env` file in the `fe` directory. A `.env.template` is provided for reference.
3.  To build the application, you can use the provided script. Navigate to the `fe` directory:
    ```bash
    cd fe
    ```
4.  Run the build script:
    ```bash
    run.bat
    ```
    This will generate a debug APK.
5.  You can also build and run the application directly from Android Studio.