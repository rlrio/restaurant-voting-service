# Project Setup and Deployment Instructions

## 1. Prerequisites
Before starting, ensure you have the following installed on your machine:
- **Java 17** (or the required version for your project).
- **Maven** (for manual project builds).
- **Docker** (for containerization).

---

## 2. Build the Project

### Build the JAR File
- Clone the project repository:
     ```
     git clone <repository-url> 
     cd <project-directory>
     ```

- Build the project using Maven:
   ```
   ./mvnw clean package
   ```
   Once the build completes, you should find the JAR file in the target directory:
   ```
   target/restaurant-voting.jar
   ```

### Build the Docker Image
   ```
   docker build -t restaurant-voting .
   ```
   Verify the image is created successfully:
   ```
   docker images
   ```

## 3. Run the Application
   ```
   docker run -p 9080:9080 restaurant-voting
   ```
   Once the container starts, the application will be accessible at:
   ```
   http://localhost:9080
   ```

## 4. Access Swagger
- The Swagger UI will be available at:
   `http://localhost:9080/swagger-ui/index.html`
  
   Use Swagger to explore and test the API endpoints interactively.

## 5. Access H2 Console

- If H2 console is enabled in your configuration, it will be accessible at:
  `http://localhost:9080/h2-console`
- Use the following credentials:
  - JDBC URL: jdbc:h2:mem:testdb
  - Username: sa
  - Password: (leave blank)

## 6. Use the `ui-test` Profile
- When you start the application with the ui-test profile, the following test data will be created automatically:
  1. Test Users:
     - Admin:
     
        Username: testAdmin
     
        Password: testAdmin
     - Regular User:
       
       Username: test 
     
       Password: test
  2. Test Restaurant:
     
     A test restaurant with a predefined test menu.

- Run with ui-test Profile
  
  To start the application with this profile, pass the environment variable `SPRING_PROFILES_ACTIVE=ui-test`:
   ```
  docker run -p 9080:9080 \
  -e SPRING_PROFILES_ACTIVE=ui-test \
  restaurant-voting
   ```