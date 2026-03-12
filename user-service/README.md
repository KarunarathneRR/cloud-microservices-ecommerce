# User Service Microservice

A comprehensive Spring Boot microservice for user management and authentication in an e-commerce system. This service handles user registration, JWT-based authentication, and profile management.

## 📋 Table of Contents
- [Technology Stack](#technology-stack)
- [Features](#features)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Database Access](#database-access)
- [API Endpoints](#api-endpoints)
- [Postman Examples](#postman-examples)
- [Testing](#testing)
- [Architecture](#architecture)

## 🛠 Technology Stack

- **Java 17** - Programming language
- **Spring Boot 3.2.3** - Application framework
- **Spring Web** - RESTful web services
- **Spring Data JPA** - Data persistence
- **Spring Security** - Password encryption (BCrypt)
- **H2 Database** - In-memory database for development
- **JJWT** - JWT token generation and validation
- **Springdoc OpenAPI** - API documentation (Swagger UI)
- **Lombok** - Reduce boilerplate code
- **Maven** - Build and dependency management

## ✨ Features

- ✅ User registration with email uniqueness validation
- ✅ Secure password hashing using BCrypt
- ✅ JWT-based authentication
- ✅ User profile retrieval by ID and email
- ✅ Comprehensive exception handling
- ✅ Input validation
- ✅ OpenAPI/Swagger documentation
- ✅ Logging for debugging and monitoring
- ✅ H2 console for database inspection
- ✅ Clean architecture with layered design

## 📁 Project Structure

```
user-service/
├── pom.xml                                 # Maven dependencies and build configuration
├── README.md                               # Project documentation
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── ecommerce/
        │           └── userservice/
        │               ├── UserServiceApplication.java     # Main application entry point
        │               ├── entity/
        │               │   └── User.java                   # User entity (JPA)
        │               ├── repository/
        │               │   └── UserRepository.java         # Data access layer
        │               ├── service/
        │               │   └── UserService.java            # Business logic layer
        │               ├── controller/
        │               │   └── UserController.java         # REST API endpoints
        │               ├── dto/
        │               │   ├── RegisterRequest.java        # Registration request DTO
        │               │   ├── LoginRequest.java           # Login request DTO
        │               │   ├── LoginResponse.java          # Login response DTO
        │               │   └── UserResponse.java           # User profile response DTO
        │               ├── security/
        │               │   ├── JwtUtil.java                # JWT utility class
        │               │   └── SecurityConfig.java         # Spring Security configuration
        │               └── exception/
        │                   ├── UserNotFoundException.java           # Custom exception
        │                   ├── DuplicateEmailException.java        # Custom exception
        │                   ├── InvalidCredentialsException.java    # Custom exception
        │                   └── GlobalExceptionHandler.java         # Exception handler
        └── resources/
            └── application.properties          # Application configuration
```

## ⚙️ Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- IDE (IntelliJ IDEA, Eclipse, or VS Code recommended)

## 🚀 Running the Application

### Method 1: Using Maven

```bash
# Navigate to project directory
cd user-service

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### Method 2: Using Java

```bash
# Build the JAR file
mvn clean package

# Run the JAR
java -jar target/user-service-1.0.0.jar
```

### Method 3: Using IDE

1. Open the project in your IDE
2. Run the `UserServiceApplication.java` main class

The application will start on **http://localhost:8081**

## 📚 API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/v3/api-docs

The Swagger UI provides an interactive interface to test all API endpoints.

## 🗄️ Database Access

The service uses an H2 in-memory database. Access the H2 console:

- **URL**: http://localhost:8081/h2-console
- **JDBC URL**: `jdbc:h2:mem:userdb`
- **Username**: `sa`
- **Password**: (leave blank)

**Note**: Data is stored in memory and will be lost when the application stops.

## 🌐 API Endpoints

### 1. Health Check
```
GET /users/health
```
Check if the service is running.

**Response**: `200 OK`
```
User Service is running!
```

### 2. Register User
```
POST /users/register
```
Register a new user account.

**Request Body**:
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "role": "USER"
}
```

**Response**: `201 CREATED`
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "role": "CUSTOMER"
}
```

**Validation Rules**:
- `name`: Required, cannot be blank
- `email`: Required, must be valid email format, must be unique
- `password`: Required, minimum 6 characters
- `role`: Optional, defaults to "USER"

**Error Responses**:
- `400 BAD REQUEST`: Validation failure
- `409 CONFLICT`: Email already registered

### 3. User Login
```
POST /users/login
```
Authenticate user and receive JWT token.

**Request Body**:
```json
{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

**Response**: `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImpvaG4uZG9lQGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiIsInN1YiI6ImpvaG4uZG9lQGV4YW1wbGUuY29tIiwiaWF0IjoxNzA5OTAwMDAwfQ.signature",
  "userId": 1,
  "email": "john.doe@example.com",
  "role": "USER",
  "message": "Login successful"
}
```

**Error Responses**:
- `400 BAD REQUEST`: Validation failure
- `401 UNAUTHORIZED`: Invalid email or password

### 4. Get User by ID
```
GET /users/{id}
```
Retrieve user profile by ID.

**Path Parameter**:
- `id`: User ID (Long)

**Response**: `200 OK`
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "role": "USER"
}
```

**Error Responses**:
- `404 NOT FOUND`: User not found

### 5. Get User by Email
```
GET /users/email/{email}
```
Retrieve user profile by email. Used by other microservices for validation.

**Path Parameter**:
- `email`: User email address (String)

**Response**: `200 OK`
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "role": "USER"
}
```

**Error Responses**:
- `404 NOT FOUND`: User not found

## 📮 Postman Examples

### Example 1: Register a New User

```
POST http://localhost:8081/users/register
Content-Type: application/json

{
  "name": "Alice Smith",
  "email": "alice.smith@example.com",
  "password": "alice123",
  "role": "CUSTOMER"
}
```

### Example 2: Login

```
POST http://localhost:8081/users/login
Content-Type: application/json

{
  "email": "alice.smith@example.com",
  "password": "alice123"
}
```

### Example 3: Get User by ID

```
GET http://localhost:8081/users/1
```

### Example 4: Get User by Email

```
GET http://localhost:8081/users/email/alice.smith@example.com
```

### Example 5: Register User with Validation Error

```
POST http://localhost:8081/users/register
Content-Type: application/json

{
  "name": "",
  "email": "invalid-email",
  "password": "123"
}
```

**Response**: `400 BAD REQUEST`
```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "name": "Name is required",
    "email": "Email must be valid",
    "password": "Password must be at least 6 characters long"
  },
  "timestamp": "2026-03-08T10:30:00"
}
```

### Example 6: Duplicate Email Registration

```
POST http://localhost:8081/users/register
Content-Type: application/json

{
  "name": "Bob Johnson",
  "email": "alice.smith@example.com",
  "password": "bob123"
}
```

**Response**: `409 CONFLICT`
```json
{
  "status": 409,
  "message": "Email already registered: alice.smith@example.com",
  "timestamp": "2026-03-08T10:31:00"
}
```

### Example 7: Invalid Login Credentials

```
POST http://localhost:8081/users/login
Content-Type: application/json

{
  "email": "alice.smith@example.com",
  "password": "wrongpassword"
}
```

**Response**: `401 UNAUTHORIZED`
```json
{
  "status": 401,
  "message": "Invalid email or password",
  "timestamp": "2026-03-08T10:32:00"
}
```

## 🧪 Testing

### Manual Testing Steps

1. **Start the application** using one of the methods above
2. **Access Swagger UI** at http://localhost:8081/swagger-ui.html
3. **Test each endpoint** using the interactive interface:
   - Register a new user
   - Login with the registered user
   - Copy the JWT token from the login response
   - Get user by ID
   - Get user by email
4. **Verify data in H2 Console**:
   - Access http://localhost:8081/h2-console
   - Connect using the credentials above
   - Run SQL: `SELECT * FROM USERS;`
   - Verify password is hashed (BCrypt)

### Testing Scenarios

✅ **Happy Path**:
- Register → Login → Get User Profile

✅ **Validation Testing**:
- Register with invalid email
- Register with short password
- Login with missing fields

✅ **Business Logic Testing**:
- Register duplicate email (should fail)
- Login with wrong password (should fail)
- Get non-existent user (should fail)

## 🏗️ Architecture

### Layered Architecture

1. **Controller Layer** (`UserController`)
   - Handles HTTP requests/responses
   - Input validation
   - API documentation

2. **Service Layer** (`UserService`)
   - Business logic
   - Transaction management
   - Exception handling

3. **Repository Layer** (`UserRepository`)
   - Data access
   - Database operations

4. **Entity Layer** (`User`)
   - Domain model
   - Database mapping

5. **DTO Layer** (Request/Response objects)
   - Data transfer between layers
   - Excludes sensitive data

6. **Security Layer** (`JwtUtil`, `SecurityConfig`)
   - Authentication
   - Password hashing
   - JWT token management

7. **Exception Layer** (Custom exceptions & handler)
   - Centralized error handling
   - Consistent error responses

### Key Design Decisions

✅ **BCrypt password hashing** - Industry standard for password security  
✅ **JWT tokens without expiration** - Simplified for assignment/demo  
✅ **H2 in-memory database** - Easy setup, no external dependencies  
✅ **Lombok** - Reduces boilerplate code  
✅ **Bean validation** - Input validation at DTO level  
✅ **Global exception handler** - Consistent error responses  
✅ **OpenAPI documentation** - Self-documenting API  

## 🔐 Security Notes

- Passwords are hashed using BCrypt before storage
- JWT tokens are signed with a secret key
- Passwords are never returned in API responses
- **For production**: Change the JWT secret key in `application.properties`
- **For production**: Implement token expiration and refresh mechanism
- **For production**: Add HTTPS/TLS encryption
- **For production**: Implement rate limiting

## 🤝 Integration with Other Microservices

Other microservices can use the following endpoint to validate users:

```
GET /users/email/{email}
```

This endpoint returns user information if the email exists, allowing other services to verify user existence before processing orders, payments, etc.

## 📝 Assignment Notes

This microservice demonstrates:
- ✅ Clean architecture and separation of concerns
- ✅ RESTful API design principles
- ✅ Spring Boot best practices
- ✅ Comprehensive error handling
- ✅ Security implementation (password hashing, JWT)
- ✅ API documentation
- ✅ Input validation
- ✅ Logging
- ✅ Database integration

Perfect for a university cloud computing assignment showcasing microservices architecture!

## 📧 Support

For questions or issues, please refer to the Swagger UI documentation or the code comments for detailed explanations.

---

**Built with ❤️ for Cloud Computing Assignment**
