# Quick Start Guide - Cricket Auction Management System

## ⚡ 5-Minute Setup

### 1. Prerequisites Check
```bash
# Verify Java 17+
java -version

# Verify Maven
mvn -version

# Verify MySQL is running
# Windows: Services → MySQL80 should be running
```

### 2. Database Setup (1 minute)

**Open MySQL Command Line or MySQL Workbench:**

```sql
-- Create Database
CREATE DATABASE spl_auction_db;
USE spl_auction_db;

-- Tables will be auto-created by Hibernate on first run
```

### 3. Update Configuration (1 minute)

**File**: `src/main/resources/application.properties`

Update these lines with your MySQL credentials:
```properties
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### 4. Build Project (2 minutes)

```bash
cd "C:\Users\soumyadeep DEY\IdeaProjects\SPL-2"
.\mvnw clean package -DskipTests
```

### 5. Run Application (1 minute)

```bash
java -jar target/SPL-2-0.0.1-SNAPSHOT.jar
```

Or using Maven:
```bash
.\mvnw spring-boot:run
```

### 6. Access Application

Open browser and go to:
```
http://localhost:8080/index.html
```

---

## 🎯 Quick Usage (Step by Step)

### Step 1: Create Teams (2 minutes)
1. Click "Add Team" in navigation
2. Enter:
   - Team Name: "Mumbai Indians"
   - Captain: "John Doe"
   - Total Budget: "10000000" (10 million)
3. Click "Create Team"
4. Repeat for 4-5 teams

### Step 2: Register Players (3 minutes)
1. Click "Register Player"
2. Add multiple players:
   - Example 1:
     - Name: "Virat Kohli"
     - Age: 25
     - Role: "Only Batsman"
     - Base Price: "1000000"
   - Example 2:
     - Name: "Jasprit Bumrah"
     - Age: 24
     - Role: "Only Bowler"
     - Base Price: "800000"
3. Create 10-15 players across different roles

### Step 3: Start Auction (5 minutes)
1. Click "Auction" in navigation
2. Click "Initialize Auction"
3. Click "Next Player" to start
4. For each player:
   - Select a team from dropdown
   - Enter bid amount (higher than base price)
   - Click "Place Bid"
5. Continue until all players are sold/unsold

### Step 4: Monitor Progress
1. Click "Manage Teams"
2. Click "View Details" on any team
3. See team roster and remaining budget
4. Option to release players

---

## 📱 Features Demo

### Feature 1: Team Management
```
Add Team → Create Multiple Teams → View Details → Release Players
```

### Feature 2: Player Registration
```
Register Player → View All Players → Filter by Status/Role
```

### Feature 3: Live Auction
```
Initialize → Next Player → Place Bid → Sold/Unsold → Next → Complete
```

### Feature 4: Budget Tracking
```
Total Budget → Deduct for Acquisitions → Show Remaining → Refund on Release
```

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| "Database Connection Error" | Check MySQL is running, verify credentials in application.properties |
| "Port 8080 already in use" | Change port: `server.port=8081` in application.properties |
| "BUILD FAILURE" | Run `.\mvnw clean compile` to rebuild |
| "No teams appear" | Ensure database tables were created (check with `SHOW TABLES;` in MySQL) |
| "Cannot place bid" | Verify team has sufficient budget remaining |

---

## 📊 Sample Data for Testing

### Teams
```
Team 1: Mumbai Indians, Captain: Raj, Budget: 10M
Team 2: Delhi Capitals, Captain: Priya, Budget: 9M
Team 3: Bangalore United, Captain: Arjun, Budget: 8M
Team 4: Hyderabad Heroes, Captain: Neha, Budget: 7M
```

### Players
```
Batsmen:
- Name: Virat Kohli, Age: 25, Role: Only Batsman, Base: 1M
- Name: Sachin Sharma, Age: 22, Role: Only Batsman, Base: 800K

Bowlers:
- Name: Jasprit Bumrah, Age: 24, Role: Only Bowler, Base: 800K
- Name: Yuzvendra Chahal, Age: 26, Role: Only Bowler, Base: 600K

All-rounders:
- Name: Hardik Pandya, Age: 28, Role: All-rounder Batting, Base: 900K
- Name: Ravindra Jadeja, Age: 30, Role: All-rounder Bowling, Base: 850K

Wicket Keepers:
- Name: MS Dhoni, Age: 34, Role: Wicket Keeper, Base: 750K
```

---

## ✅ Verification Checklist

- [ ] Java 17+ installed
- [ ] Maven installed
- [ ] MySQL running
- [ ] Database created: `spl_auction_db`
- [ ] Credentials updated in application.properties
- [ ] Project compiled successfully
- [ ] Application running on http://localhost:8080
- [ ] Can access home page
- [ ] Can create teams
- [ ] Can register players
- [ ] Can start auction
- [ ] Can place bids

---

## 🎓 Key Concepts

### Player Status Flow
```
REGISTERED (waiting for auction)
    ↓
(Selected in Auction)
    ↓
    ├─→ SOLD (team bought) → Moved to Team
    └─→ UNSOLD (not bought) → Recycled back to REGISTERED
```

### Budget Calculation
```
Remaining Budget = Total Budget - Retention1 - Retention2 - (Player Costs)
```

### Auction Categories (In Order)
1. All-rounder Bowling
2. All-rounder Batting
3. Only Batsman
4. Only Bowler
5. Wicket Keeper

---

## 📞 Common Questions

**Q: Can I auction players multiple times?**  
A: Yes! Unsold players are recycled back to REGISTERED and can be auctioned again.

**Q: What happens if a team's budget runs out?**  
A: They cannot place bids. The system prevents negative budgets.

**Q: Can I release a player and get money back?**  
A: Yes! Use "Release" in team details to add refund back to budget.

**Q: How many auction cycles run?**  
A: Until all players are SOLD. Unsold players keep recycling.

**Q: Can I change team details after creation?**  
A: Yes! Use the update endpoint or refresh the team list page.

---

## 🚀 Performance Tips

- Register 5-10 teams before starting auction
- Register 20-30 players across different roles
- Use reasonable base prices (100K - 2M range)
- Allocate total budget 5-10x of total base prices for competitive auction

---

## 📚 Files to Know

| File | When to Use |
|------|-----------|
| `application.properties` | Changing database/port settings |
| `README.md` | Complete documentation |
| `PROJECT_SUMMARY.md` | Architecture overview |
| `pom.xml` | Adding new dependencies |

---

## 🎯 Next Actions

1. **Now**: Follow setup steps above
2. **Then**: Create 4-5 teams
3. **Then**: Register 15-20 players
4. **Then**: Start auction
5. **Finally**: Monitor and manage teams

---

**Version**: 1.0.0  
**Last Updated**: January 1, 2026  
**Status**: ✅ Ready to Use

For detailed documentation, see README.md

