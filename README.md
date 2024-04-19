# Challenge Project

## Overview

This project uses a Cassandra database with a 2-node cluster for efficient scalability. The backend, powered by Scala and Akka HTTP, ensures high performance. React and Material UI are employed for a sleek and intuitive frontend interface.

### Key Features

- **Secure Authentication**: Features robust login and signup processes using JWT tokens, providing a secure and reliable authentication system.
- **Advanced Data Grid**: Offers a sophisticated data grid with extensive column filtering capabilities. Backend logic handles row filtering and pagination, ensuring a smooth and efficient user experience.
- **Dynamic Time Series Visualization**: Allows users to visualize time series data through a variety of chart types. Includes features for selecting specific data ranges, enabling detailed analysis of data trends and patterns.
- **Automated Data Management**: Leverages Apache Spark for the automation of database creation and data loading.

## How to Reproduce

### Prerequisites

Before you start, ensure you have Docker installed on your local machine. This project utilizes Docker Compose for simplifying the setup and deployment process.

### Running the Application

1. Clone this repository to your local machine.
2. Navigate to the main folder of the project.

#### Running the Application with Docker-compose

3. Run the following command:

```bash
docker-compose up
```

This command starts all the necessary services and applications in containers. The process may take some time during the first run as images are being pulled and containers are being set up.

4. Once the application is successfully started, you should see a log message stating:

```bash
Application is running go to http://localhost:3000/
```

indicating that the backend is up and running, and the application can be accessed through your web browser.

### Troubleshooting

If you encounter any errors during startup, it's recommended to stop all running containers and rerun the Docker Compose command. This ensures that any temporary issues are resolved by reinitializing the containers.

```bash
docker-compose down
docker-compose up
```

### Running the Application with kubernates
