# Library Management System

## Overview

This is a Library Management System built using Java 17+, Spring Boot, and Gradle. The project uses PostgreSQL as the database and Docker for containerization. The application provides a REST API documented with OpenAPI and an interactive Swagger-UI page.

## Entity Relationships
- **Author**
    - `id` (Primary Key)
    - `name`: Name of the author
    - `dateOfBirth`: Date of birth of the author

- **Book**
    - `id` (Primary Key)
    - `title`: Title of the book
    - `genre`: Genre of the book
    - `price`: Price of the book
    - `authorId` (Foreign Key): References `Author.id`

- **Member**
    - `id` (Primary Key)
    - `username`: Unique username of the member
    - `email`: Email address of the member
    - `address`: Address of the member
    - `phoneNumber`: Phone number of the member

- **Loan**
    - `id` (Primary Key)
    - `memberId` (Foreign Key): References `Member.id`
    - `bookId` (Foreign Key): References `Book.id`
    - `lendDate`: Date when the book was lent out
    - `returnDate`: Date when the book is expected to be returned

## Prerequisites

- Java 17 or later
- Gradle
- Docker
- PostgreSQL

## Building and Running the Project

### Step 1: Clone the Repository

```sh
git clone https://github.com/yourusername/library-management-system.git
cd library-management-system
```
Step 2: Create a .env File
```sh
POSTGRES_DB=librarydb
POSTGRES_USER=user
POSTGRES_PASSWORD=password
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/librarydb
SPRING_DATASOURCE_USERNAME=user
SPRING_DATASOURCE_PASSWORD=password
```
Step 3: Build the Project
```sh
gradle build
```

Step 4: Run the Project with Docker Compose
```sh
docker-compose up --build
```

Step 5: Access the Application 

Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI Documentation: http://localhost:8080/v3/api-docs

