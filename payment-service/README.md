# Payment Service - Microservices E-Commerce System

A Spring Boot microservice for handling payment processing in a distributed e-commerce system. This service integrates with the Order Service to process payments, track payment status, and prevent duplicate charges.

## 🎓 University Project
**Course**: Cloud Computing (CTSE)  
**Assignment**: Microservices-Based E-Commerce System  
**Service**: Payment Service  
**Port**: 8082

---

## 🚀 Technology Stack

- **Java**: 17
- **Spring Boot**: 3.2.3
- **Spring Web**: RESTful API
- **Spring Data JPA**: Database persistence
- **H2 Database**: In-memory database (development)
- **Lombok**: Boilerplate code reduction
- **Springdoc OpenAPI**: API documentation (Swagger UI)
- **Jakarta Validation**: Request validation
- **Maven**: Dependency management

---

## ✨ Features

### Core Features
- ✅ Payment processing with simulated payment gateway
- ✅ Order-payment linking (one payment per order)
- ✅ Duplicate payment prevention (idempotency)
- ✅ Payment status tracking (PENDING → SUCCESS/FAILED)
- ✅ Multiple payment methods support (CREDIT_CARD, DEBIT_CARD, PAYPAL, etc.)
- ✅ Comprehensive error handling with specific exceptions
- ✅ RESTful API with proper HTTP status codes

### Technical Features
- ✅ Layered architecture (Entity → Repository → Service → Controller)
- ✅ H2 in-memory database for development
- ✅ Swagger UI for interactive API documentation
- ✅ Request validation with Jakarta Bean Validation
- ✅ Transaction management with @Transactional
- ✅ Automatic timestamp generation with @PrePersist
- ✅ Configurable payment gateway simulation

### Microservices Integration
- ✅ Designed to be called by Order Service
- ✅ Payment status endpoints for order fulfillment workflow
- ✅ Detailed integration comments in code
- ✅ Future support for service discovery and circuit breakers

---

## 📁 Project Structure

```
payment-service/
├── src/
│   └── main/
│       ├── java/com/ecommerce/paymentservice/
│       │   ├── PaymentServiceApplication.java       # Main class
│       │   ├── controller/
│       │   │   └── PaymentController.java           # REST API endpoints
│       │   ├── dto/
│       │   │   ├── PaymentRequest.java              # Payment request DTO
│       │   │   └── PaymentResponse.java             # Payment response DTO
│       │   ├── entity/
│       │   │   └── Payment.java                     # Payment entity
│       │   ├── exception/
│       │   │   ├── DuplicatePaymentException.java   # Duplicate payment error
│       │   │   ├── GlobalExceptionHandler.java      # Centralized error handling
│       │   │   ├── PaymentNotFoundException.java    # Payment not found error
│       │   │   └── PaymentProcessingException.java  # Processing error
│       │   ├── repository/
│       │   │   └── PaymentRepository.java           # JPA repository
│       │   └── service/
│       │       └── PaymentService.java              # Business logic
│       └── resources/
│           └── application.properties                # Configuration
├── pom.xml                                           # Maven dependencies
└── README.md                                         # This file
```

---

## 📋 Prerequisites

- **JDK 17** or higher
- **Maven 3.6+** (or use Maven Wrapper)
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code
- **Postman** (optional, for API testing)

---

## 🏃 Running the Application

### Method 1: Using Maven
```bash
cd payment-service
mvn clean install
mvn spring-boot:run
```

### Method 2: Using Maven Wrapper (Windows)
```bash
cd payment-service
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

### Method 3: Using Maven Wrapper (Linux/Mac)
```bash
cd payment-service
./mvnw clean install
./mvnw spring-boot:run
```

### Method 4: Running JAR directly
```bash
cd payment-service
mvn clean package
java -jar target/payment-service-0.0.1-SNAPSHOT.jar
```

The application will start on **http://localhost:8082**

---

## 📚 API Documentation

Once the application is running:

- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **API Docs (JSON)**: http://localhost:8082/v3/api-docs
- **H2 Console**: http://localhost:8082/h2-console

### H2 Database Connection
- **JDBC URL**: `jdbc:h2:mem:paymentdb`
- **Username**: `sa`
- **Password**: (leave blank)

---

## 🔌 API Endpoints

### 1. Health Check
- **Endpoint**: `GET /payments/health`
- **Description**: Check if the service is running
- **Response**: `200 OK`
  ```json
  {
    "status": "Payment Service is running!",
    "timestamp": "2024-01-15T10:30:00"
  }
  ```

### 2. Process Payment
- **Endpoint**: `POST /payments/process`
- **Description**: Process a new payment for an order
- **Request Body**:
  ```json
  {
    "orderId": 123,
    "amount": 299.99,
    "method": "CREDIT_CARD"
  }
  ```
- **Success Response**: `200 OK`
  ```json
  {
    "id": 1,
    "orderId": 123,
    "amount": 299.99,
    "method": "CREDIT_CARD",
    "status": "SUCCESS",
    "createdAt": "2024-01-15T10:30:00",
    "message": "Payment processed successfully"
  }
  ```
- **Possible Errors**:
  - `409 Conflict`: Payment already exists for this order
  - `500 Internal Server Error`: Payment processing failed
  - `400 Bad Request`: Validation errors

### 3. Get Payment by ID
- **Endpoint**: `GET /payments/{id}`
- **Description**: Retrieve payment details by payment ID
- **Path Variable**: `id` (Long)
- **Success Response**: `200 OK`
  ```json
  {
    "id": 1,
    "orderId": 123,
    "amount": 299.99,
    "method": "CREDIT_CARD",
    "status": "SUCCESS",
    "createdAt": "2024-01-15T10:30:00",
    "message": null
  }
  ```
- **Error Response**: `404 Not Found` - Payment not found

### 4. Get Payment by Order ID
- **Endpoint**: `GET /payments/order/{orderId}`
- **Description**: Retrieve payment details by order ID (critical for order fulfillment)
- **Path Variable**: `orderId` (Long)
- **Success Response**: `200 OK`
  ```json
  {
    "id": 1,
    "orderId": 123,
    "amount": 299.99,
    "method": "CREDIT_CARD",
    "status": "SUCCESS",
    "createdAt": "2024-01-15T10:30:00",
    "message": null
  }
  ```
- **Error Response**: `404 Not Found` - Payment not found for this order

---

## 📮 Postman Examples

### Example 1: Process a New Payment (Success)
```http
POST http://localhost:8082/payments/process
Content-Type: application/json

{
  "orderId": 101,
  "amount": 149.99,
  "method": "CREDIT_CARD"
}
```

**Expected Response**: `200 OK`
```json
{
  "id": 1,
  "orderId": 101,
  "amount": 149.99,
  "method": "CREDIT_CARD",
  "status": "SUCCESS",
  "createdAt": "2024-01-15T10:30:00",
  "message": "Payment processed successfully"
}
```

---

### Example 2: Process a Payment (Simulated Failure)
```http
POST http://localhost:8082/payments/process
Content-Type: application/json

{
  "orderId": 102,
  "amount": 599.99,
  "method": "DEBIT_CARD"
}
```

**Possible Response**: `500 Internal Server Error` (20% chance)
```json
{
  "status": 500,
  "message": "Payment processing failed: Simulated payment gateway failure",
  "timestamp": "2024-01-15T10:31:00"
}
```

**Note**: The payment gateway simulation has a 20% failure rate for testing purposes.

---

### Example 3: Duplicate Payment Prevention
```http
POST http://localhost:8082/payments/process
Content-Type: application/json

{
  "orderId": 101,
  "amount": 149.99,
  "method": "CREDIT_CARD"
}
```

**Expected Response**: `409 Conflict`
```json
{
  "status": 409,
  "message": "Payment already exists for order ID: 101",
  "timestamp": "2024-01-15T10:32:00"
}
```

---

### Example 4: Get Payment by ID
```http
GET http://localhost:8082/payments/1
```

**Expected Response**: `200 OK`
```json
{
  "id": 1,
  "orderId": 101,
  "amount": 149.99,
  "method": "CREDIT_CARD",
  "status": "SUCCESS",
  "createdAt": "2024-01-15T10:30:00",
  "message": null
}
```

---

### Example 5: Get Payment by Order ID
```http
GET http://localhost:8082/payments/order/101
```

**Expected Response**: `200 OK`
```json
{
  "id": 1,
  "orderId": 101,
  "amount": 149.99,
  "method": "CREDIT_CARD",
  "status": "SUCCESS",
  "createdAt": "2024-01-15T10:30:00",
  "message": null
}
```

---

### Example 6: Validation Error (Missing Required Fields)
```http
POST http://localhost:8082/payments/process
Content-Type: application/json

{
  "orderId": null,
  "amount": -10.00,
  "method": ""
}
```

**Expected Response**: `400 Bad Request`
```json
{
  "status": 400,
  "message": "orderId: must not be null, amount: must be greater than or equal to 0.01, method: must not be blank",
  "timestamp": "2024-01-15T10:33:00"
}
```

---

### Example 7: Payment Not Found
```http
GET http://localhost:8082/payments/999
```

**Expected Response**: `404 Not Found`
```json
{
  "status": 404,
  "message": "Payment not found with id: 999",
  "timestamp": "2024-01-15T10:34:00"
}
```

---

## 💳 Payment Gateway Simulation

The service includes a configurable payment gateway simulator for development and testing:

### Configuration (application.properties)
```properties
# Payment Gateway Simulation
payment.gateway.simulation.delay=1000
payment.gateway.simulation.failure-rate=0.2
```

### Behavior
- **Delay**: Simulates processing time (default: 1000ms)
- **Failure Rate**: Random failure probability (default: 0.2 = 20%)
- **Status Flow**: PENDING → SUCCESS or FAILED

### Payment Processing Workflow
1. **Validation**: Check if payment already exists for order
2. **Create Pending**: Save payment with PENDING status
3. **Gateway Call**: Simulate payment gateway processing
4. **Update Status**: Change to SUCCESS or FAILED
5. **Return Response**: Send result to Order Service

---

## 🔗 Microservices Integration

### Order Service Integration Pattern
```
Order Service                    Payment Service
     |                                   |
     |  1. Create Order                  |
     |---------------------------------->|
     |                                   |
     |  2. POST /payments/process        |
     |---------------------------------->|
     |                                   |
     |  3. Return Payment Status         |
     |<----------------------------------|
     |                                   |
     |  4. Update Order Status           |
     |   (based on payment status)       |
     |                                   |
     |  5. GET /payments/order/{orderId} |
     |---------------------------------->|
     |                                   |
     |  6. Return Payment Details        |
     |<----------------------------------|
```

### Key Integration Points
- **Order Service calls** `/payments/process` after order creation
- **Payment Service** processes payment and returns status
- **Order Service** can query payment status via `/payments/order/{orderId}`
- **Duplicate prevention** ensures no double-charging
- **Status tracking** enables order fulfillment decisions

---

## 🧪 Testing Scenarios

### Scenario 1: Successful Payment
1. POST `/payments/process` with valid data
2. Verify response has `status: SUCCESS`
3. GET `/payments/{id}` to confirm persistence
4. GET `/payments/order/{orderId}` to verify order-payment link

### Scenario 2: Duplicate Prevention
1. POST `/payments/process` with orderId=100
2. POST `/payments/process` again with orderId=100
3. Verify second request returns `409 Conflict`

### Scenario 3: Failed Payment
1. POST `/payments/process` multiple times
2. Eventually get `500 Internal Server Error` (20% chance)
3. Verify payment has `status: FAILED` in database

### Scenario 4: Validation Errors
1. POST `/payments/process` with negative amount
2. POST `/payments/process` with blank method
3. POST `/payments/process` with null orderId
4. Verify all return `400 Bad Request`

### Scenario 5: Payment Not Found
1. GET `/payments/999` (non-existent ID)
2. GET `/payments/order/999` (non-existent order ID)
3. Verify both return `404 Not Found`

---

## 🗃️ Database Schema

### PAYMENTS Table
| Column     | Type           | Constraints         |
|-----------|----------------|---------------------|
| id        | BIGINT         | Primary Key, Auto   |
| order_id  | BIGINT         | Not Null            |
| amount    | DOUBLE         | Not Null            |
| method    | VARCHAR(50)    | Not Null            |
| status    | VARCHAR(20)    | Not Null            |
| created_at| TIMESTAMP      | Not Null            |

### Sample Data
```sql
-- View all payments
SELECT * FROM PAYMENTS;

-- View payments by status
SELECT * FROM PAYMENTS WHERE status = 'SUCCESS';

-- View payment for specific order
SELECT * FROM PAYMENTS WHERE order_id = 101;
```

---

## ⚙️ Configuration Reference

### application.properties
```properties
# Server Configuration
server.port=8082
spring.application.name=payment-service

# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:paymentdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Payment Gateway Simulation
payment.gateway.simulation.delay=1000
payment.gateway.simulation.failure-rate=0.2

# Microservices Integration
order.service.url=http://localhost:8083

# Swagger/OpenAPI
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

---

## 📝 Development Notes

### Payment Methods Supported
- CREDIT_CARD
- DEBIT_CARD
- PAYPAL
- BANK_TRANSFER
- CASH_ON_DELIVERY

### Status Values
- **PENDING**: Payment is being processed
- **SUCCESS**: Payment completed successfully
- **FAILED**: Payment failed due to gateway or technical error

### Error Handling
- All exceptions return standardized ErrorResponse format
- HTTP status codes follow REST best practices
- Detailed error messages for debugging

---

## 🚀 Future Enhancements

- [ ] Integrate real payment gateway (Stripe, PayPal, etc.)
- [ ] Add payment refund functionality
- [ ] Implement payment history and reporting
- [ ] Add service discovery (Eureka)
- [ ] Add circuit breaker (Resilience4j)
- [ ] Add distributed tracing (Zipkin)
- [ ] Add API Gateway integration
- [ ] Add authentication/authorization
- [ ] Add payment webhook support
- [ ] Add payment receipt generation
- [ ] Migrate to production database (PostgreSQL, MySQL)

---

## 👨‍💻 Author

**CTSE Cloud Computing Assignment**  
University Microservices Project

---

## 📄 License

This is a university assignment project for educational purposes.

---

## 🔗 Related Services

- **User Service**: http://localhost:8081 (Authentication & User Management)
- **Order Service**: http://localhost:8083 (Order Management) - *To be implemented*
- **Payment Service**: http://localhost:8082 (This service)

---

## 📞 Support

For issues or questions:
1. Check Swagger UI documentation
2. Review H2 console for database state
3. Check application logs for errors
4. Test with Postman examples above

---

**Happy Coding! 🎉**
