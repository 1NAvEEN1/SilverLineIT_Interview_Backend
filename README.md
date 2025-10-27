# Sample Spring Boot Backend

A simple Spring Boot 3 (Java 17) backend with JWT authentication, MySQL persistence (Spring Data JPA), file uploads, and course/content management.

- Build tool: Maven (wrapper included)
- DB: MySQL
- Auth: Spring Security + JWT (jjwt)
- API Docs: See API_DOCUMENTATION.md


## Prerequisites

- Java 17 (JDK)
- MySQL 8.x running locally
- Git (optional)
- Windows terminal: cmd.exe (commands below use cmd syntax)


## Quick start (Windows)

1) Configure database credentials in `src/main/resources/application.properties` (defaults shown):

```
spring.datasource.url=jdbc:mysql://localhost:3306/userdb?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=1234
```

2) Build and run with Maven wrapper:

```bat
mvnw.cmd clean package
mvnw.cmd spring-boot:run
```

App starts on http://localhost:8080

Alternatively run the jar:

```bat
java -jar target\Sample-SpringBoot-0.0.1-SNAPSHOT.jar
```


## Configuration

Main properties file: `src/main/resources/application.properties`

Key properties you may want to change:
- Server: `server.port=8080`
- DB: `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`
- JPA: `spring.jpa.hibernate.ddl-auto=update` (use `validate`/`none` in production)
- JWT: `jwt.secret`, `jwt.expiration`, `jwt.refresh.expiration`
- File uploads: `spring.servlet.multipart.max-file-size`, `file.upload-dir` (default: `uploads` at project root)

You can also switch to YAML by renaming `src/main/resources/application.yml.example` to `application.yml` and removing `application.properties`.

Environment variable overrides (example, current session only):

```bat
set SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/userdb?createDatabaseIfNotExist=true
set SPRING_DATASOURCE_USERNAME=root
set SPRING_DATASOURCE_PASSWORD=1234
set JWT_SECRET=change_me_long_random_string
mvnw.cmd spring-boot:run
```


## Database migration (optional)

A SQL script is provided to add soft-delete support for course content: `database-migration-soft-delete.sql`.

Run it after the schema exists:

```bat
mysql -u root -p < database-migration-soft-delete.sql
```


## Running tests

```bat
mvnw.cmd test
```

There are two helper scripts you can use to quickly hit key endpoints once the app is running:
- `test-api.bat`
- `test-jwt-api.bat`


## Authentication flow (JWT)

Basic steps:
1) Register a user (instructor or normal user)
2) Login to receive an access token and refresh token
3) Send `Authorization: Bearer <access_token>` header on protected endpoints
4) Use the refresh token to obtain a new access token when it expires

See `API_DOCUMENTATION.md` for endpoint details and request/response formats.


## File uploads

- Max upload size defaults to 10MB
- Files are stored under the `uploads` directory (created automatically)
- Course content APIs exclude soft-deleted items by default


## Common issues and troubleshooting

- MySQL connection failure
  - Ensure MySQL is running and accessible at `localhost:3306`
  - Verify DB user and password in `application.properties`
  - Create the schema or let Hibernate create it if `createDatabaseIfNotExist=true` is set

- Port already in use
  - Change `server.port` in `application.properties` or free the port 8080

- JWT errors
  - Ensure `jwt.secret` is a sufficiently long, random string (keep it secret; do not commit secrets to VCS)

- File upload errors
  - Verify `spring.servlet.multipart.*` and `file.max-size` are large enough
  - Confirm the `uploads` directory is writable


## Project layout (high level)

```
src/
  main/
    java/org/example/samplespringboot/
      controller/        # REST controllers
      service/           # Services (business logic)
      repository/        # Spring Data JPA repositories
      security/          # Security + JWT
      dto/               # Request/response DTOs
      entity/            # JPA entities
    resources/
      application.properties
API_DOCUMENTATION.md
database-migration-soft-delete.sql
mvnw.cmd / mvnw
pom.xml
uploads/                 # created at runtime
```


## Next steps

- Read `API_DOCUMENTATION.md` and try endpoints with Postman/Insomnia
- Create an instructor user and a course; then upload files to the course
- Update `jwt.secret` and database credentials for your environment

If you run into issues, open an issue with your logs and steps to reproduce.
