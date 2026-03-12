# Payment Service

## Overview

The payment service processes payments, stores payment records, validates users through user-service, and exposes payment history endpoints for customers and admins.

- Service name: `payment-service`
- Port: `8083`
- Base path: `/payments`
- Database: H2 in-memory database `jdbc:h2:mem:paymentdb`
- Depends on user-service: `http://localhost:8081`
- Swagger UI: `http://localhost:8083/swagger-ui.html`
- H2 console: `http://localhost:8083/h2-console`

## Main Responsibilities

- Accept payment requests from authenticated users
- Validate the payer against user-service before processing
- Generate and persist payment records
- Return user payment history
- Allow admin users to view all payments
- Support order-based and payment ID lookups

## Payment Flow

When a payment is created, the service does the following:

1. Receives `orderId`, `userId`, `amount`, and `paymentMethod`
2. Validates that the user exists by calling user-service
3. Creates a payment record with `PENDING` status
4. Simulates gateway processing
5. Marks the payment as `COMPLETED`
6. Saves the record and returns the response

## Security and Authorization

- JWT is required for protected endpoints
- Admin users are not allowed to make payments
- Non-admin users can only create payments for their own account
- Non-admin users can only view their own payment history
- Only admin users can view the full payment list

## API Endpoints

### `POST /payments`

Processes a new payment.

Request body:

```json
{
	"orderId": 101,
	"userId": 1,
	"amount": 250.00,
	"paymentMethod": "CREDIT_CARD"
}
```

Validation rules:

- `orderId` is required
- `userId` is required
- `amount` is required and must be greater than `0.01`
- `paymentMethod` is required

Supported payment method examples:

- `CREDIT_CARD`
- `DEBIT_CARD`
- `PAYPAL`
- `BANK_TRANSFER`

Authorization rules:

- Admin users are blocked from this endpoint
- Logged-in users must submit their own `userId`

Success response:

```json
{
	"id": 1,
	"orderId": 101,
	"userId": 1,
	"amount": 250.00,
	"status": "COMPLETED",
	"paymentMethod": "CREDIT_CARD",
	"transactionId": "550e8400-e29b-41d4-a716-446655440000",
	"createdAt": "2026-03-13T10:15:30",
	"message": "Payment processed successfully"
}
```

### `GET /payments/{id}`

Returns a single payment by payment ID.

### `GET /payments/order/{orderId}`

Returns all payments associated with one order.

### `GET /payments/user/{userId}`

Returns payment history for one user.

Authorization rules:

- Admin can view any user's payment history
- Non-admin can only view their own history

### `GET /payments`

Returns every payment record.

Authorization rule:

- Only admin users can access this endpoint

### `GET /payments/health`

Health check endpoint.

Response:

```text
Payment Service is running!
```

## Data Returned by the Service

Payment response structure:

```json
{
	"id": 1,
	"orderId": 101,
	"userId": 1,
	"amount": 250.00,
	"status": "COMPLETED",
	"paymentMethod": "CREDIT_CARD",
	"transactionId": "uuid-value",
	"createdAt": "2026-03-13T10:15:30",
	"message": "Payment processed successfully"
}
```

Known statuses in the system:

- `PENDING`
- `COMPLETED`
- `FAILED`
- `REFUNDED`

Current processing logic sets new successful payments to `COMPLETED`.

## Integration With User Service

Before a payment is saved, payment-service calls user-service to verify the given `userId`.

Integration behavior:

- valid user: payment continues
- missing user: request fails
- user-service unavailable: payment request fails

This means payment-service depends on user-service being up and reachable on port `8081`.

## Frontend Pages Using Payment Service

- Payment page: customer payment form with method selection
- Payment history page: list of payments with totals and status badges
- Admin payment history view: full payment list via `/payments`
- Home page: links users to payment features

Frontend routes connected to this service:

- `/payment`
- `/payments`

## Business Rules

- Payments cannot be created for another user's account
- Admin users cannot create payments
- Payment history is user-scoped unless the requester is admin
- Each payment gets a generated transaction ID
- The gateway is simulated in the current implementation

## Error Cases

Common error scenarios include:

- invalid payment request data
- user not found in user-service
- payment not found
- forbidden access to another user's history
- admin attempting to create a payment
- user-service unavailable

## Notes

- Data is stored in memory, so restarting the service clears payment records
- JWT secret matches user-service so token validation works across both services
