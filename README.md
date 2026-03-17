# Cricket Auction Management System

A comprehensive web-based cricket auction management system built with Spring Boot, HTML/CSS/JavaScript, and MySQL. This system allows teams to register, manage players, conduct live auctions by category, and track budgets and player acquisitions.

## Features

### 🏆 Core Features
- **Team Management**: Register teams with captains, total budgets, and optional player retentions
- **Player Registration**: Register players with roles (All-rounder Bowling/Batting, Batsman, Bowler, Wicket Keeper)
- **Live Auction System**: Conduct auctions organized by player categories
- **Budget Tracking**: Real-time budget tracking with remaining budget calculations
- **Player Lifecycle Management**: Players move between REGISTERED → SOLD/UNSOLD → RECYCLED states
- **Player Release**: Release players back to the auction pool with refunds
- **Auction Statistics**: Real-time tracking of registered, sold, and unsold players

### 🎯 Auction Workflow
1. Initialize auction and select first category
2. Randomly select a player from current category
3. Teams place bids for the player
4. Player is either:
   - **SOLD**: Assigned to team and deducted from budget
   - **UNSOLD**: Returned to registration pool for next cycle
5. Process continues through all categories
6. Unsold players are recycled back to REGISTERED state
7. Cycle continues until all players are sold

## Technology Stack

- **Backend**: Spring Boot 4.0.1
- **ORM**: Spring Data JPA with Hibernate
- **Database**: MySQL 8.0+
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla)
- **Build Tool**: Maven 3.8+
- **Java Version**: JDK 17+
- **Additional Libraries**: 
  - Lombok (for annotation processing)
  - Apache POI (for Excel support - future enhancement)

## Project Structure

```
SPL-2/
├── src/
│   ├── main/
│   │   ├── java/com/example/spl2/
│   │   │   ├── entity/           # JPA Entities
│   │   │   │   ├── Player.java
│   │   │   │   ├── Team.java
│   │   │   │   ├── Bid.java
│   │   │   │   └── AuctionState.java
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   ├── PlayerDTO.java
│   │   │   │   └── TeamDTO.java
│   │   │   ├── repository/       # Spring Data JPA Repositories
│   │   │   │   ├── PlayerRepository.java
│   │   │   │   ├── TeamRepository.java
│   │   │   │   ├── BidRepository.java
│   │   │   │   └── AuctionStateRepository.java
│   │   │   ├── service/          # Business Logic
│   │   │   │   ├── PlayerService.java
│   │   │   │   ├── TeamService.java
│   │   │   │   └── AuctionService.java
│   │   │   ├── controller/       # REST API Controllers
│   │   │   │   ├── PlayerController.java
│   │   │   │   ├── TeamController.java
│   │   │   │   ├── AuctionController.java
│   │   │   │   └── HomeController.java
│   │   │   ├── exception/        # Custom Exceptions
│   │   │   │   ├── PlayerNotFoundException.java
│   │   │   │   ├── TeamNotFoundException.java
│   │   │   │   ├── TeamAlreadyExistsException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── Spl2Application.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       │   ├── index.html
│   │       │   ├── add-team.html
│   │       │   ├── team-management.html
│   │       │   ├── auction.html
│   │       │   ├── player-registration.html
│   │       │   ├── css/
│   │       │   │   └── style.css
│   │       │   └── js/
│   │       │       └── script.js
│   │       └── templates/
│   └── test/
│       └── java/com/example/spl2/
│           └── Spl2ApplicationTests.java
├── pom.xml
└── HELP.md
```

## Installation & Setup

### Prerequisites
- JDK 17 or higher
- Maven 3.8+
- MySQL 8.0+

### Step 1: Clone or Extract Project
```bash
cd C:\Users\soumyadeep DEY\IdeaProjects\SPL-2
```

### Step 2: Configure Database

1. **Create MySQL Database**:
```sql
CREATE DATABASE spl_auction_db;
USE spl_auction_db;
```

2. **Update Database Credentials** in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/spl_auction_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

### Step 3: Build Project
```bash
.\mvnw clean package -DskipTests
```

### Step 4: Run Application

**Option A: Using Maven**
```bash
.\mvnw spring-boot:run
```

**Option B: Using JAR**
```bash
java -jar target/SPL-2-0.0.1-SNAPSHOT.jar
```

### Step 5: Access Application
- **Home**: http://localhost:8080/index.html
- **Add Team**: http://localhost:8080/add-team.html
- **Manage Teams**: http://localhost:8080/team-management.html
- **Auction**: http://localhost:8080/auction.html
- **Register Player**: http://localhost:8080/player-registration.html

## API Endpoints

### Teams API
- `POST /api/teams` - Create new team
- `GET /api/teams` - Get all teams
- `GET /api/teams/{id}` - Get team by ID
- `PUT /api/teams/{id}` - Update team
- `DELETE /api/teams/{id}` - Delete team
- `POST /api/teams/{teamId}/release-player/{playerId}?releasePrice={price}` - Release player

### Players API
- `POST /api/players` - Create new player
- `GET /api/players` - Get all players
- `GET /api/players?status=REGISTERED` - Get players by status
- `GET /api/players?status=REGISTERED&role=All-rounder%20Bowling` - Filter by status and role
- `GET /api/players?teamId={id}` - Get players by team
- `GET /api/players/{id}` - Get player by ID
- `PUT /api/players/{id}` - Update player
- `DELETE /api/players/{id}` - Delete player

### Auction API
- `POST /api/auction/initialize` - Initialize auction
- `GET /api/auction/next-player` - Get next player for auction
- `POST /api/auction/sell-player/{playerId}?teamId={teamId}&soldPrice={price}` - Sell player
- `POST /api/auction/unsold-player/{playerId}` - Mark player as unsold
- `POST /api/auction/move-to-next-category` - Move to next category
- `GET /api/auction/status` - Get auction status
- `GET /api/auction/categories` - Get all auction categories

## Usage Guide

### 1. Register Teams
1. Click "Add Team" in navigation
2. Fill in team details:
   - Team Name (required, unique)
   - Captain Name (required)
   - Total Budget (required, positive number)
   - Player Retention 1 & 2 (optional with amounts)
3. Click "Create Team"
4. Teams are immediately added to the system

### 2. Register Players
1. Click "Register Player" in navigation
2. Fill in player details:
   - Player Name
   - Age (18-50)
   - Role (select from dropdown)
   - Base Price
3. Click "Register Player"
4. Players appear in "Registered Players" table
5. Filter by role or status using dropdowns

### 3. Manage Teams
1. Click "Manage Teams" in navigation
2. View all teams with their budgets
3. Click "View Details" on any team to see:
   - Team composition
   - Players acquired
   - Spent amount
   - Remaining budget
4. Release players from team (will refund to team budget)

### 4. Conduct Auction
1. Click "Auction" in navigation
2. Click "Initialize Auction" to start
3. Click "Next Player" to display a random player
4. Teams place bids by:
   - Selecting team from dropdown
   - Entering bid amount
   - Clicking "Place Bid"
5. Alternatives:
   - "Mark Unsold" - Player not sold, goes to unsold pool
   - "Next Player" - Move to next player
6. Monitor auction statistics in real-time

## Database Schema

### Players Table
```sql
CREATE TABLE players (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    player_name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    role VARCHAR(50) NOT NULL,
    base_price DOUBLE NOT NULL,
    status VARCHAR(20) NOT NULL,
    team_id BIGINT,
    sold_price DOUBLE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (team_id) REFERENCES teams(id)
);
```

### Teams Table
```sql
CREATE TABLE teams (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    team_name VARCHAR(100) UNIQUE NOT NULL,
    captain VARCHAR(100) NOT NULL,
    total_budget DOUBLE NOT NULL,
    remaining_budget DOUBLE NOT NULL,
    player_retention1 VARCHAR(100),
    player_retention2 VARCHAR(100),
    player_retention1_money DOUBLE,
    player_retention2_money DOUBLE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Bids Table
```sql
CREATE TABLE bids (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    player_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    bid_amount DOUBLE NOT NULL,
    is_winning BOOLEAN,
    bid_time TIMESTAMP,
    created_at TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES players(id),
    FOREIGN KEY (team_id) REFERENCES teams(id)
);
```

### AuctionState Table
```sql
CREATE TABLE auction_state (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    current_category VARCHAR(50) NOT NULL,
    category_index INT NOT NULL,
    current_player_id BIGINT,
    auction_active BOOLEAN,
    category_start_time TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (current_player_id) REFERENCES players(id)
);
```

## Player Roles

The system supports the following player roles:
1. **All-rounder Bowling** - Players good at batting and bowling
2. **All-rounder Batting** - Players good at batting and bowling
3. **Only Batsman** - Pure batsmen
4. **Only Bowler** - Pure bowlers
5. **Wicket Keeper** - Wicket keepers

## Auction Categories

The auction proceeds through these categories in order:
1. All-rounder Bowling
2. All-rounder Batting
3. Only Batsman
4. Only Bowler
5. Wicket Keeper

After completing all categories, unsold players cycle back to REGISTERED status.

## Budget Constraints

- **Mandatory**: No team can have negative remaining budget
- **Retention Deduction**: Retention amounts are automatically deducted from total budget
- **Spent Tracking**: System tracks total spent on player acquisitions

## Error Handling

The application includes comprehensive error handling:
- **404 Not Found**: Team or player not found
- **409 Conflict**: Team already exists with same name
- **400 Bad Request**: Invalid input data or insufficient budget
- **500 Internal Server Error**: Unexpected server errors

All errors return JSON with:
```json
{
  "timestamp": "2026-01-01T00:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error description"
}
```

## Troubleshooting

### Database Connection Issues
- Verify MySQL is running
- Check credentials in `application.properties`
- Ensure database `spl_auction_db` exists

### Port Already in Use
```bash
# Change port in application.properties
server.port=8081
```

### Compilation Issues
```bash
# Clean and rebuild
.\mvnw clean compile
```

### Lombok Not Working
- Ensure annotation processor paths are configured in pom.xml
- Rebuild project: `.\mvnw clean compile`

## Future Enhancements

1. **Excel Import**: Import player data from Excel sheets
2. **CSV Export**: Export auction results to CSV
3. **Real-time Notifications**: WebSocket support for live bidding
4. **Admin Panel**: Dashboard with analytics
5. **Authentication**: User login and permissions
6. **Auction History**: Track previous auctions
7. **Mobile Responsive**: Improved mobile UI
8. **Multi-language Support**: i18n support

## Contributing

For bug reports and feature requests, please contact the development team.

## License

This project is proprietary and confidential.

## Support

For issues or questions, please refer to the documentation or contact support.

---

**Version**: 1.0.0  
**Last Updated**: January 1, 2026  
**Build**: SPL-2-0.0.1-SNAPSHOT

