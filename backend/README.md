# QuantCrux Backend

A monolithic Spring Boot application providing comprehensive quantitative finance platform capabilities.

## Features

- **Authentication & Authorization**: JWT-based security with role-based access control
- **Strategy Management**: Create and manage quantitative trading strategies
- **Backtesting**: Historical strategy performance testing with Monte Carlo simulation
- **Product Creation**: Design structured financial products (digital options, barrier options, etc.)
- **Trade Booking**: Book and manage trades with real-time P&L tracking
- **Market Data**: Mock market data service with realistic price movements
- **Portfolio Management**: Real-time portfolio tracking and analytics
- **Risk Analytics**: VaR, Sharpe ratio, drawdown calculations
- **Pricing Engine**: Monte Carlo-based pricing with Greeks calculation
- **Lifecycle Management**: Trade event processing and barrier monitoring
- **Reporting**: Generate comprehensive reports

## Tech Stack

- Java 17
- Spring Boot 3.2.0
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- Apache Commons Math
- Swagger/OpenAPI

## Setup Instructions

### Prerequisites

1. Java 17 or higher
2. Maven 3.6+
3. PostgreSQL 12+

### Database Setup

1. Install PostgreSQL and create a database:
```sql
CREATE DATABASE quantcrux;
```

2. Update `application.yml` with your database credentials:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/quantcrux
    username: your_username
    password: your_password
```

### Running the Application

1. Clone the repository and navigate to the backend directory
2. Build the application:
```bash
mvn clean package
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on port 8081.

### API Documentation

Once the application is running, you can access:
- Swagger UI: http://localhost:8081/swagger-ui.html
- Health Check: http://localhost:8081/actuator/health

### Demo Users

The application automatically creates demo users on startup:

| Username    | Password | Role              |
|-------------|----------|-------------------|
| client1     | password | CLIENT            |
| pm1         | password | PORTFOLIO_MANAGER |
| researcher1 | password | RESEARCHER        |

### Testing the API

1. Login to get a JWT token:
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "pm1", "password": "password"}'
```

2. Use the token in subsequent requests:
```bash
curl -X GET http://localhost:8081/api/strategies \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Architecture

The application follows a layered architecture:

- **Controllers**: REST API endpoints
- **Services**: Business logic implementation
- **Repositories**: Data access layer
- **Models**: JPA entities
- **DTOs**: Data transfer objects
- **Security**: JWT authentication and authorization

## Key Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Strategies
- `GET /api/strategies` - Get user strategies
- `POST /api/strategies` - Create new strategy

### Products
- `GET /api/products` - Get all products
- `POST /api/products` - Create new product

### Trading
- `POST /api/trades/book` - Book a trade
- `GET /api/trades` - Get user trades

### Market Data
- `GET /api/market-data/{symbol}` - Get market data

### Analytics
- `GET /api/analytics/risk-metrics` - Get risk metrics

### Pricing
- `POST /api/pricing/monte-carlo` - Monte Carlo pricing

## Development

### Adding New Features

1. Create the model/entity in `com.quantcrux.model`
2. Create the repository in `com.quantcrux.repository`
3. Implement business logic in `com.quantcrux.service`
4. Create DTOs in `com.quantcrux.dto`
5. Add REST endpoints in `com.quantcrux.controller`

### Security

The application uses Spring Security with JWT tokens. Access control is role-based:
- `CLIENT`: Can view portfolios and book trades
- `PORTFOLIO_MANAGER`: Can create products and manage portfolios
- `RESEARCHER`: Can create strategies and run backtests

### Database Schema

The application uses JPA with automatic schema generation. Key entities:
- `User`: System users with roles
- `Strategy`: Trading strategies with indicators and rules
- `Product`: Structured financial products
- `Trade`: Individual trades and positions

## Production Considerations

1. **Security**: Change JWT secret and use environment variables
2. **Database**: Use connection pooling and proper indexing
3. **Monitoring**: Add application monitoring and logging
4. **Performance**: Implement caching for frequently accessed data
5. **Backup**: Set up regular database backups