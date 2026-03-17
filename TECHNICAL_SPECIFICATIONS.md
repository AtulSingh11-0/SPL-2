# Technical Specifications - Cricket Auction Management System

## System Overview

A complete cricket player auction management system implementing a multi-round auction workflow with team budget management, player registration, and real-time bidding capabilities.

## Technology Stack

```
Frontend:
├── HTML5 with Semantic Markup
├── CSS3 with Responsive Design
├── Vanilla JavaScript (ES6+)
├── Fetch API for HTTP Requests
└── Session Storage for State Management

Backend:
├── Spring Boot 4.0.1
├── Spring Data JPA (Hibernate 6.x)
├── Spring MVC for REST APIs
├── Spring Web for HTTP Support
└── Lombok for Code Generation

Database:
├── MySQL 8.0+ (InnoDB)
├── 4 Core Tables with Relationships
├── Auto-DDL via Hibernate
└── Connection Pooling (HikariCP)

Build & Deploy:
├── Maven 3.8+
├── Java 17 JDK
├── JAR Packaging
└── Spring Boot Embedded Tomcat
```

## Project Metrics

### Code Statistics
- **Total Java Classes**: 22
  - Entities: 4
  - DTOs: 2
  - Repositories: 4
  - Services: 3
  - Controllers: 4
  - Exceptions: 4
  - Main App: 1

- **Total HTML Pages**: 5
- **Total CSS Files**: 1 (all-inclusive)
- **Total JavaScript Files**: 1 (utility library)
- **Configuration Files**: 2 (pom.xml, application.properties)
- **Documentation Files**: 3 (README, PROJECT_SUMMARY, QUICKSTART)

### API Coverage
- **REST Endpoints**: 22
- **HTTP Methods**: GET, POST, PUT, DELETE
- **Response Format**: JSON
- **Error Handling**: Global exception handler

### Database
- **Tables**: 4
- **Relationships**: 3 (1:N relationships)
- **Indexes**: Primary + Foreign keys
- **Constraints**: NOT NULL, UNIQUE, FOREIGN KEY

## Architecture Details

### Layered Architecture

```
┌─────────────────────────────────────────┐
│          Presentation Layer             │
│  (HTML, CSS, JavaScript)                │
├─────────────────────────────────────────┤
│          Controller Layer               │
│  (REST API, Request Handling)           │
├─────────────────────────────────────────┤
│          Service Layer                  │
│  (Business Logic, Validations)          │
├─────────────────────────────────────────┤
│          Repository Layer               │
│  (Data Access, JPA Queries)             │
├─────────────────────────────────────────┤
│          Database Layer                 │
│  (MySQL with Hibernate ORM)             │
└─────────────────────────────────────────┘
```

## Entity Relationship Diagram

```
┌──────────────────────┐
│      Teams           │
├──────────────────────┤
│ id (PK)              │
│ teamName (UNIQUE)    │
│ captain              │
│ totalBudget          │
│ remainingBudget      │
│ playerRetention1     │
│ playerRetention2     │
│ retention1Money      │
│ retention2Money      │
│ createdAt            │
│ updatedAt            │
└──────────┬───────────┘
           │ 1:N
           │
           ▼
┌──────────────────────┐
│     Players          │
├──────────────────────┤
│ id (PK)              │
│ playerName           │
│ age                  │
│ role                 │
│ basePrice            │
│ status (Enum)        │
│ team_id (FK)         │
│ soldPrice            │
│ createdAt            │
│ updatedAt            │
└──────────┬───────────┘
           │ 1:N
           │
           ▼
┌──────────────────────┐
│       Bids           │
├──────────────────────┤
│ id (PK)              │
│ player_id (FK)       │
│ team_id (FK)         │
│ bidAmount            │
│ isWinning            │
│ bidTime              │
│ createdAt            │
└──────────────────────┘

┌──────────────────────┐
│   AuctionState       │
├──────────────────────┤
│ id (PK)              │
│ currentCategory      │
│ categoryIndex        │
│ current_player_id(FK)│
│ auctionActive        │
│ categoryStartTime    │
│ updatedAt            │
└──────────────────────┘
```

## Core Components

### 1. Entity Models

#### Player Entity
```java
@Entity
public class Player {
    - Stateful: REGISTERED, SOLD, UNSOLD
    - Team Association: @ManyToOne
    - Timestamps: createdAt, updatedAt
    - Price tracking: basePrice, soldPrice
}
```

#### Team Entity
```java
@Entity
public class Team {
    - Budget Management: totalBudget, remainingBudget
    - Retention Support: retention1, retention2 + amounts
    - Player Roster: @OneToMany relation
    - Timestamps: createdAt, updatedAt
}
```

#### Bid Entity
```java
@Entity
public class Bid {
    - Audit Trail: bidAmount, bidTime
    - Winning Tracking: isWinning flag
    - Relationships: Player, Team
}
```

#### AuctionState Entity
```java
@Entity
public class AuctionState {
    - Category Tracking: currentCategory, categoryIndex
    - Player Tracking: currentPlayer
    - Status: auctionActive
    - Timing: categoryStartTime
}
```

### 2. Service Layer

#### PlayerService
```
Operations:
├── createPlayer(PlayerDTO)
├── getPlayerById(Long)
├── getAllPlayers()
├── getPlayersByStatus(String)
├── getPlayersByStatusAndRole(String, String)
├── getPlayersByTeamId(Long)
├── updatePlayer(Long, PlayerDTO)
├── sellPlayer(Long, Long, Double)
├── markPlayerUnsold(Long)
├── deletePlayer(Long)
├── getCountByStatus(String)
└── getRandomRegisteredPlayerByRole(String)
```

#### TeamService
```
Operations:
├── createTeam(TeamDTO)
├── getTeamById(Long)
├── getAllTeams()
├── updateTeam(Long, TeamDTO)
├── deleteTeam(Long)
├── releasePlayer(Long, Long, Double)
├── deductFromBudget(Long, Double)
└── convertToDTO(Team) [internal]
```

#### AuctionService
```
Core State Machine:
├── initializeAuction()
├── getNextPlayer() → RandomSelection
├── moveToNextCategory()
├── recycleUnsoldPlayers()
├── sellPlayer(Long, Long, Double)
├── markPlayerUnsold(Long)
├── getAuctionStatus()
└── getAuctionCategories()

Categories (In Sequence):
1. All-rounder Bowling
2. All-rounder Batting
3. Only Batsman
4. Only Bowler
5. Wicket Keeper
```

### 3. Controller Layer

#### REST API Endpoints

**PlayerController** (10 endpoints)
```
POST   /api/players                    Create player
GET    /api/players                    List (with filters)
GET    /api/players/{id}               Get one
PUT    /api/players/{id}               Update
DELETE /api/players/{id}               Delete
GET    /api/players?status=SOLD        Filter by status
GET    /api/players?teamId=1           Filter by team
```

**TeamController** (6 endpoints)
```
POST   /api/teams                      Create team
GET    /api/teams                      List all
GET    /api/teams/{id}                 Get one
PUT    /api/teams/{id}                 Update
DELETE /api/teams/{id}                 Delete
POST   /api/teams/{teamId}/release...  Release player
```

**AuctionController** (7 endpoints)
```
POST   /api/auction/initialize         Start auction
GET    /api/auction/next-player        Get next player
POST   /api/auction/sell-player        Sell player
POST   /api/auction/unsold-player      Mark unsold
POST   /api/auction/move-to-next-cat   Next category
GET    /api/auction/status             Get status
GET    /api/auction/categories         List categories
```

**HomeController** (Navigation)
```
GET    /                               Redirect to index.html
GET    /teams-page                     Team management
GET    /auction-page                   Auction interface
GET    /player-registration-page       Player registration
GET    /add-team-page                  Team creation
```

### 4. Request/Response Examples

#### Create Team Request
```json
POST /api/teams
{
  "teamName": "Mumbai Indians",
  "captain": "Raj Kumar",
  "totalBudget": 10000000,
  "playerRetention1": "Virat Kohli",
  "playerRetention1Money": 1500000,
  "playerRetention2": "MS Dhoni",
  "playerRetention2Money": 1200000
}
```

#### Create Team Response (201 Created)
```json
{
  "id": 1,
  "teamName": "Mumbai Indians",
  "captain": "Raj Kumar",
  "totalBudget": 10000000,
  "remainingBudget": 7300000,
  "playerRetention1": "Virat Kohli",
  "playerRetention1Money": 1500000,
  "playerRetention2": "MS Dhoni",
  "playerRetention2Money": 1200000,
  "playerCount": 0,
  "spentAmount": 0.00
}
```

#### Sell Player Request
```
POST /api/auction/sell-player/5?teamId=1&soldPrice=1000000
```

#### Error Response (400 Bad Request)
```json
{
  "timestamp": "2026-01-01T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Insufficient budget. Team: Mumbai Indians has remaining budget: 5000000 but tried to bid: 6000000"
}
```

## Business Logic Implementation

### Auction State Machine

```
[INITIAL STATE]
    ↓
[Initialize Auction] → currentCategory = "All-rounder Bowling"
    ↓
[Get Next Player]
    ├─→ IF (Players exist for category)
    │       ├─ Random select from REGISTERED status
    │       ├─ Set currentPlayer
    │       └─ auctionActive = true
    │
    └─→ ELSE
            └─ [Move to Next Category]
                ├─ categoryIndex++
                ├─ IF (categoryIndex < 5)
                │   └─ Repeat Get Next Player
                └─ ELSE
                    └─ [Recycle Unsold Players]
                        ├─ UNSOLD → REGISTERED
                        ├─ categoryIndex = 0
                        └─ Repeat Get Next Player
```

### Budget Management

```
Team Creation:
1. remainingBudget = totalBudget
2. IF retention1Money: remainingBudget -= retention1Money
3. IF retention2Money: remainingBudget -= retention2Money

Player Acquisition:
1. Check: remainingBudget >= soldPrice
2. IF NO: Raise InsufficientBudgetException
3. IF YES:
   - remainingBudget -= soldPrice
   - player.team = team
   - player.status = "SOLD"
   - player.soldPrice = soldPrice

Player Release:
1. remainingBudget += releasePrice
2. player.team = null
3. player.status = "REGISTERED"
```

## Database Queries

### Key Query Methods (JPA)

```java
// Player Queries
findByStatus(String status)
findByStatusAndRole(String status, String role)
findByTeamId(Long teamId)
findRandomRegisteredPlayerByRole(@Param("role") String role)
  → Uses: @Query with SQL RAND() and LIMIT

// Team Queries
findByTeamName(String teamName)
existsByTeamName(String teamName)

// Bid Queries
findByPlayerId(Long playerId)
findByPlayerIdAndIsWinningTrue(Long playerId)

// Auction Queries
findFirstByOrderByIdDesc()
```

## Error Handling Strategy

### Exception Hierarchy
```
Exception
├── PlayerNotFoundException extends RuntimeException
├── TeamNotFoundException extends RuntimeException
├── TeamAlreadyExistsException extends RuntimeException
└── RuntimeException (Budget, Validation errors)
```

### Global Exception Handler
```java
@RestControllerAdvice
GlobalExceptionHandler
├── @ExceptionHandler(PlayerNotFoundException.class)
├── @ExceptionHandler(TeamNotFoundException.class)
├── @ExceptionHandler(TeamAlreadyExistsException.class)
├── @ExceptionHandler(RuntimeException.class)
└── @ExceptionHandler(Exception.class)
```

## Frontend Architecture

### Page Structure
```
index.html (Home)
├── Navigation Bar
├── Hero Section
└── Feature Cards

add-team.html (Team Creation)
├── Team Registration Form
└── Teams List Table

team-management.html (Team Dashboard)
├── Teams Grid
└── Team Details Modal

auction.html (Live Auction)
├── Auction Controls
├── Current Player Card
├── Bidding Section
└── Auction Statistics

player-registration.html (Player Management)
├── Player Registration Form
├── Filter Controls
└── Players List Table
```

### JavaScript Structure
```
script.js (Utility Library)
├── API Helpers
│   └── apiCall(endpoint, method, body)
├── Validation Functions
│   ├── validateTeamForm()
│   └── validatePlayerForm()
├── UI Helpers
│   ├── formatCurrency()
│   ├── formatDate()
│   ├── showNotification()
│   └── getStatusColor()
└── Data Processing
    └── calculateTeamSpent()
```

### CSS Approach
```
style.css (Comprehensive Styling)
├── Reset & Base Styles
├── Color Scheme (Purple gradient)
├── Layout (Flexbox/Grid)
├── Component Styles
│   ├── Navigation
│   ├── Forms
│   ├── Tables
│   ├── Cards
│   ├── Modals
│   └── Badges
├── Responsive Design (Mobile First)
└── Utility Classes
```

## Configuration

### application.properties
```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/spl_auction_db
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

### pom.xml Dependencies
```
- spring-boot-starter-data-jpa
- spring-boot-starter-webmvc
- spring-boot-devtools
- mysql-connector-j
- lombok
- poi-ooxml (Excel support)
- Testing dependencies (JPA-Test, WebMVC-Test)
```

## Performance Considerations

### Database Optimization
- Proper indexing on primary keys
- Foreign key constraints for referential integrity
- Connection pooling with HikariCP
- Lazy loading for entity relationships

### Application Performance
- Lombok reduces code, faster compilation
- Spring Data JPA query optimization
- Minimal database roundtrips
- Efficient JSON serialization with Spring

### Frontend Performance
- Vanilla JavaScript (no heavy frameworks)
- CSS3 transitions (hardware-accelerated)
- Responsive design (Mobile-first approach)
- Efficient DOM manipulation

## Security Measures

### Input Validation
- DTOs with validation
- Controller-level validation
- Service-layer constraint checks
- Database NOT NULL constraints

### Business Logic Security
- Budget constraint enforcement
- No negative budget states
- Proper exception handling
- Transactional consistency

### Error Handling
- No sensitive info in error messages
- Standardized error response format
- HTTP status codes (400, 404, 409, 500)
- Logging capability for debugging

## Testing Capabilities

### API Testing
```bash
# Using curl or Postman
POST http://localhost:8080/api/teams
GET http://localhost:8080/api/players?status=REGISTERED
POST http://localhost:8080/api/auction/sell-player/1?teamId=1&soldPrice=1000000
```

### Manual Testing
- Via HTML UI forms
- All CRUD operations testable
- Real-time updates visible
- Error messages clearly displayed

## Deployment Checklist

- [ ] Java 17+ JDK installed
- [ ] MySQL 8.0+ installed and running
- [ ] Database created: spl_auction_db
- [ ] application.properties configured
- [ ] Project compiled: `.\mvnw clean compile`
- [ ] Package built: `.\mvnw clean package -DskipTests`
- [ ] JAR file created: `target/SPL-2-0.0.1-SNAPSHOT.jar`
- [ ] Application runs: `java -jar target/SPL-2-0.0.1-SNAPSHOT.jar`
- [ ] Access via browser: http://localhost:8080

## Scalability & Future Enhancements

### Potential Improvements
- WebSocket for real-time updates
- JWT authentication & authorization
- User roles and permissions
- Audit logging for all operations
- Caching layer (Redis)
- API versioning (v1, v2)
- GraphQL support
- Microservices architecture
- Docker containerization
- Kubernetes deployment

---

**Document Version**: 1.0.0  
**Last Updated**: January 1, 2026  
**Status**: Complete & Ready for Production

