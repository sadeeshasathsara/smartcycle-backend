# SmartCycle Backend Application

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen)
![Java](https://img.shields.io/badge/Java-21-orange)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue)
![Maven](https://img.shields.io/badge/Maven-3.6+-red)

<img width="300" height="300" alt="spring-boot-icon" src="https://github.com/user-attachments/assets/c0371dd2-d845-422f-b301-e9b71ff2f34c" />


A comprehensive waste management and recycling collection system built with Spring Boot. SmartCycle enables residents to schedule waste pickups, track collection requests, manage payments, and earn recycling rebates while providing administrative tools for collection personnel management and route optimization.

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Security](#security)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

### For Residents
- 🏠 **User Registration & Authentication** - Secure JWT-based authentication system
- 📅 **Pickup Request Scheduling** - Schedule waste collection pickups with flexible time slots
- 📊 **Collection History** - View past and current pickup requests
- 🔔 **Real-time Status Tracking** - Track active collection requests in real-time
- ❌ **Request Cancellation** - Cancel scheduled pickups before collection
- 💳 **Payment Management** - Handle payments for waste collection services
- ♻️ **Recycling Rebates** - Earn rebates for recycling activities

### For Administration
- 👥 **Personnel Management** - Manage drivers and collection staff
- 🚚 **Vehicle & Resource Management** - Track and allocate collection vehicles
- 🗺️ **Route Optimization** - Plan and optimize collection routes
- 📈 **Reporting & Analytics** - Generate reports on collection activities
- ⚠️ **Hazardous Waste Handling** - Special handling for hazardous materials
- 📋 **Schedule Management** - Manage collection schedules and assignments

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.5.6
- **Language**: Java 21
- **Build Tool**: Maven
- **Database**: MySQL 8.0+
- **Security**: Spring Security with JWT
- **ORM**: Spring Data JPA / Hibernate
- **Testing**: JUnit 5, Mockito
- **Additional Libraries**:
  - Lombok (Code generation)
  - JJWT (JWT implementation)
  - H2 Database (Testing)

## 🏗️ Architecture

The application follows a layered architecture pattern:

```
┌─────────────────────────────────────┐
│         Controllers Layer           │  ← REST API Endpoints
├─────────────────────────────────────┤
│          Services Layer             │  ← Business Logic
├─────────────────────────────────────┤
│        Repositories Layer           │  ← Data Access
├─────────────────────────────────────┤
│          Models Layer               │  ← Domain Entities
└─────────────────────────────────────┘
```

### Key Components:

- **Controllers**: Handle HTTP requests and responses
- **Services**: Implement business logic
- **Repositories**: Manage database operations
- **DTOs**: Data Transfer Objects for API communication
- **Models**: JPA entities representing database tables
- **Security**: JWT authentication and authorization filters
- **Exception Handlers**: Global exception handling

## 📦 Prerequisites

Before running this application, ensure you have:

- **Java Development Kit (JDK) 21** or higher
- **Maven 3.6+** (or use included Maven Wrapper)
- **MySQL 8.0+** installed and running
- **Git** (for cloning the repository)

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/Eric-Devon/smartcycle-backend.git
cd smartcycle-backend
```

### 2. Set Up MySQL Database

Create a new MySQL database:

```sql
CREATE DATABASE smartcycle;
```

### 3. Configure Application Properties

Edit `src/main/resources/application.properties`:

```properties
spring.application.name=SmartCycleApplication

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/smartcycle
spring.datasource.username=root
spring.datasource.password=YourPassword

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# JWT Configuration
jwt.secret.key=YourSecretKeyHere
jwt.expiration.ms=86400000
```

> ⚠️ **Security Warning**: Never commit sensitive credentials to version control. Use environment variables or external configuration in production.

## ⚙️ Configuration

### Environment Variables (Recommended for Production)

Set the following environment variables:

```bash
export DB_URL=jdbc:mysql://localhost:3306/smartcycle
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret_key
export JWT_EXPIRATION=86400000
```

Update `application.properties` to use environment variables:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
jwt.secret.key=${JWT_SECRET}
jwt.expiration.ms=${JWT_EXPIRATION}
```

### Application Profiles

Create profile-specific properties files:

- `application-dev.properties` - Development environment
- `application-prod.properties` - Production environment
- `application-test.properties` - Testing environment

Run with specific profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 🏃 Running the Application

### Using Maven Wrapper (Recommended)

**Windows (PowerShell):**
```powershell
.\mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

### Using Maven

```bash
mvn spring-boot:run
```

### Building a JAR

```bash
mvn clean package
java -jar target/SmartCycleApplication-0.0.1-SNAPSHOT.jar
```

### Default Access

The application will start on: `http://localhost:8080`

## 📚 API Documentation

### Authentication Endpoints

#### Register New User
```http
POST /api/auth/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "securePassword123",
  "phoneNumber": "1234567890",
  "address": "123 Main St"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Account successfully created",
  "data": {
    "userId": 1,
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Logged in successfully",
  "data": {
    "userId": 1,
    "email": "john.doe@example.com",
    "firstName": "John",
    "role": "RESIDENT"
  }
}
```

*JWT token is set in HTTP-only cookie*

### Collection Endpoints

All collection endpoints require authentication (JWT token in cookie).

#### Request Pickup
```http
POST /api/collections/request-pickup
Authorization: Bearer <token>
Content-Type: application/json

{
  "wasteType": "RECYCLABLE",
  "quantity": 2,
  "preferredDate": "2025-11-15",
  "preferredTimeSlot": "MORNING",
  "specialInstructions": "Please collect from side entrance"
}
```

#### Get My Collection Requests
```http
GET /api/collections/my-requests
Authorization: Bearer <token>
```

#### Get Active Request Status
```http
GET /api/collections/status/active
Authorization: Bearer <token>
```

#### Cancel Request
```http
PATCH /api/collections/{requestId}/cancel
Authorization: Bearer <token>
```

#### Complete Pickup (Admin/Driver)
```http
PATCH /api/collections/{requestId}/complete
Authorization: Bearer <token>
Content-Type: application/json

{
  "actualWeight": 15.5,
  "completionNotes": "Collection completed successfully"
}
```

### Admin Endpoints

#### Manage Collection Personnel
```http
GET /api/admin/personnel
POST /api/admin/personnel
PUT /api/admin/personnel/{id}
DELETE /api/admin/personnel/{id}
```

#### Manage Vehicles
```http
GET /api/admin/vehicles
POST /api/admin/vehicles
PUT /api/admin/vehicles/{id}
```

### Payment Endpoints

```http
GET /api/payments/user/{userId}
POST /api/payments/process
GET /api/payments/{paymentId}
```

### Schedule & Route Endpoints

```http
GET /api/schedules
POST /api/schedules/create
GET /api/schedules/{scheduleId}
```

## 🗄️ Database Schema

### Core Entities

- **User** - Base user information
- **Resident** - Resident-specific details
- **Admin** - Administrator details
- **Driver** - Driver information
- **CollectionPersonnel** - Collection staff details
- **CollectionRequest** - Waste collection requests
- **CollectionSchedule** - Scheduled collections
- **Payment** - Payment records
- **RecyclingRebate** - Rebate information
- **Vehicle** - Collection vehicles
- **WasteBin** - Waste bin tracking
- **Route** - Collection routes
- **Notification** - User notifications
- **Report** - System reports
- **HazardousWasteRequest** - Special waste handling

### Enumerations

- **WasteType**: GENERAL, RECYCLABLE, ORGANIC, HAZARDOUS
- **Status**: PENDING, SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
- **ScheduleStatus**: ACTIVE, INACTIVE, SUSPENDED
- **PaymentStatus**: PENDING, COMPLETED, FAILED, REFUNDED

## 🔐 Security

### JWT Authentication

The application uses JSON Web Tokens (JWT) for stateless authentication:

1. **Registration/Login**: User provides credentials
2. **Token Generation**: Server creates JWT with user details
3. **Token Storage**: JWT stored in HTTP-only cookie
4. **Request Authorization**: JWT validated on each protected endpoint
5. **Token Expiration**: Default 24 hours (configurable)

### Security Features

- ✅ Password encryption using BCrypt
- ✅ HTTP-only cookies for token storage
- ✅ CORS configuration
- ✅ Role-based access control (RBAC)
- ✅ Global exception handling
- ✅ SQL injection prevention (JPA)
- ✅ XSS protection

### Protected Endpoints

Most endpoints require authentication. Public endpoints:
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /` (Home page)

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=PaymentServiceTest
```

### Test Coverage

The project includes:
- **Unit Tests**: Testing individual components
- **Integration Tests**: Testing component interactions
- **Service Tests**: Business logic validation

### Example Test Files

- `PaymentServiceTest.java` - Payment service tests
- `SmartCycleApplicationTests.java` - Application context tests

## 📁 Project Structure

```
smartcycle-backend/
├── src/
│   ├── main/
│   │   ├── java/com/smartcycle/smartcycleapplication/
│   │   │   ├── SmartCycleApplication.java          # Main application
│   │   │   ├── config/
│   │   │   │   ├── ApplicationConfig.java          # App configuration
│   │   │   │   ├── SecurityConfig.java             # Security config
│   │   │   │   ├── JwtAuthFilter.java              # JWT filter
│   │   │   │   ├── WebConfig.java                  # CORS config
│   │   │   │   └── GlobalExceptionHandler.java     # Exception handling
│   │   │   ├── controllers/
│   │   │   │   ├── AuthController.java             # Authentication
│   │   │   │   ├── CollectionController.java       # Collections
│   │   │   │   ├── AdminController.java            # Admin ops
│   │   │   │   ├── PaymentController.java          # Payments
│   │   │   │   ├── ScheduleController.java         # Schedules
│   │   │   │   └── ResourceController.java         # Resources
│   │   │   ├── services/                           # Business logic
│   │   │   ├── repositories/                       # Data access
│   │   │   ├── models/                             # JPA entities
│   │   │   ├── dtos/                               # Data transfer objects
│   │   │   ├── mappers/                            # Entity-DTO mappers
│   │   │   └── exceptions/                         # Custom exceptions
│   │   └── resources/
│   │       ├── application.properties              # Configuration
│   │       └── static/
│   │           └── index.html                      # Home page
│   └── test/
│       └── java/                                   # Test classes
├── target/                                         # Build output
├── mvnw                                            # Maven wrapper (Unix)
├── mvnw.cmd                                        # Maven wrapper (Windows)
├── pom.xml                                         # Maven configuration
└── README.md                                       # This file
```

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add some amazing feature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open a Pull Request**

### Coding Standards

- Follow Java naming conventions
- Write unit tests for new features
- Document public APIs with JavaDoc
- Keep methods focused and concise
- Use meaningful variable names

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Team

**Project Lead**: [Sadeesha Sathsara](https://github.com/sadeeshasathsara)

### Contributors

- **Sadeesha Sathsara** - [@sadeeshasathsara](https://github.com/sadeeshasathsara) - Project Lead & Full Stack Developer
- **Eric Devon** - [@Eric-Devon](https://github.com/Eric-Devon) - Full Stack Developer
- **Ehara Perera** - [@EHARAPERERA](https://github.com/EHARAPERERA) - Full Stack Developer
- **Vageesha Udawatta** - [@vageeshau](https://github.com/vageeshau) - Full Stack Developer

## 📧 Contact

For questions or support, please open an issue on the GitHub repository.

## 🔄 Version History

- **v0.0.1-SNAPSHOT** - Initial development version
  - User authentication and authorization
  - Collection request management
  - Payment processing
  - Admin panel functionality
  - Schedule and route management

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- All contributors and supporters of this project

---

**Made with ❤️ for a cleaner, smarter future**
