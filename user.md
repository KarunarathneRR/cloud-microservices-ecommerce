# User Service

## Overview

The user service manages registration, login, profile retrieval, profile updates, account deactivation, and admin user management.

- Service name: `user-service`
- Port: `8081`
- Base path: `/users`
- Database: H2 in-memory database `jdbc:h2:mem:userdb`
- Swagger UI: `http://localhost:8081/swagger-ui.html`
- H2 console: `http://localhost:8081/h2-console`

## Main Responsibilities

- Register new users with validation and password hashing
- Authenticate users and return JWT tokens
- Return user profile details without exposing passwords
- Allow users to update or deactivate their own accounts
- Allow admins to view and manage users
- Provide an internal endpoint for payment-service user validation

## Roles

- `CUSTOMER`: default standard user role
- `ADMIN`: administrative access to user and payment management views

## Security

- JWT is generated on login
- Protected endpoints use `Authorization: Bearer <token>`
- Non-admin users can only update or deactivate their own accounts
- Admin users can manage other users
- Internal endpoint `/users/internal/{id}` is intended for inter-service use

## API Endpoints

### Public Endpoints

#### `POST /users/register`

Creates a new user account.

Request body:

```json
{
	"name": "John Doe",
	"email": "john@example.com",
	"password": "secret123",
	"role": "USER"
}
```

Validation rules:

- `name` is required
- `email` is required and must be valid
- `password` is required and must be at least 6 characters
- `role` is optional and should be `CUSTOMER` or `ADMIN`

Behavior:

- Email must be unique
- Password is hashed before storage
- `USER` is normalized to `CUSTOMER`
- Any non-admin custom role value is treated as `CUSTOMER`

Success response:

```json
{
	"id": 1,
	"name": "John Doe",
	"email": "john@example.com",
	"role": "CUSTOMER",
	"active": true
}
```

#### `POST /users/login`

Authenticates a user and returns a JWT token.

Request body:

```json
{
	"email": "john@example.com",
	"password": "secret123"
}
```

Success response:

```json
{
	"token": "<jwt-token>",
	"userId": 1,
	"email": "john@example.com",
	"role": "CUSTOMER",
	"message": "Login successful"
}
```

Failure cases:

- Invalid email or password
- Deactivated account cannot log in

#### `GET /users/health`

Health check endpoint.

Response:

```text
User Service is running!
```

### Protected and Management Endpoints

#### `GET /users/{id}`

Returns one user profile by ID.

#### `GET /users`

Returns all users. Supports optional filtering by role.

Examples:

- `GET /users`
- `GET /users?role=ADMIN`

Used by the frontend admin customer management page.

#### `PUT /users/{id}`

Updates user profile fields.

Allowed updates:

- `name`
- `email`
- `password` if provided and non-blank

Not intended to change:

- user ID
- role

Authorization rules:

- Admin can update any user
- Non-admin can only update their own profile

#### `POST /users/{id}/deactivate`

Deactivates a user account.

Authorization rules:

- Admin can deactivate any user
- Non-admin can only deactivate their own profile

#### `GET /users/internal/{id}`

Internal endpoint for other services. Payment-service uses this to confirm that a user exists before creating a payment.

#### `GET /users/email/{email}`

Returns one user by email.

## Data Returned by the Service

User response structure:

```json
{
	"id": 1,
	"name": "John Doe",
	"email": "john@example.com",
	"role": "CUSTOMER",
	"active": true
}
```

## Frontend Pages Using User Service

- Register page: creates new users
- Login page: authenticates and stores JWT in local storage
- Profile page: loads the current user profile
- Navbar: uses auth information and resolves the signed-in user name
- Manage Customers page: admin-only list of registered users with search and filtering

Frontend routes connected to this service:

- `/register`
- `/login`
- `/profile`
- `/customers`

## Business Rules

- Duplicate email registration is blocked
- Passwords are stored encoded, not plain text
- Inactive users cannot log in
- Role normalization converts `USER` to `CUSTOMER`
- Non-admin users are restricted to their own account actions

## Error Cases

Common error scenarios include:

- duplicate email
- invalid credentials
- user not found
- forbidden operation on another user's account
- validation errors on request payloads

## Notes

- JWT secret is shared with the payment service so both services can validate tokens
- Data is stored in memory, so restarting the service clears user records
