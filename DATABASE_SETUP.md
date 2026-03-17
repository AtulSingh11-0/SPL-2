# Database Setup Guide - Cricket Auction System

## Problem
The application cannot start because the MySQL database `spl_auction_db` does not exist.

**Error:** `Unknown database 'spl_auction_db'`

---

## Solution: Create the Database

### Option 1: Using MySQL Workbench (Recommended for Windows)

1. **Open MySQL Workbench**
2. **Create a new connection or use existing**
3. **Run this SQL command:**
   ```sql
   CREATE DATABASE IF NOT EXISTS spl_auction_db;
   ```
4. **Click Execute (lightning bolt icon)**
5. **Verify the database was created**

### Option 2: Using Command Line

**Windows Command Prompt:**
```cmd
cd "C:\Program Files\MySQL\MySQL Server 8.0\bin"
mysql -u root -psoumya@0210 -e "CREATE DATABASE spl_auction_db;"
```

**Windows PowerShell:**
```powershell
# Note: Use backtick to escape @ symbol if needed
mysql -u root -p"soumya@0210" -e "CREATE DATABASE spl_auction_db;"
```

**Verify creation:**
```cmd
mysql -u root -psoumya@0210 -e "SHOW DATABASES LIKE 'spl_auction_db';"
```

### Option 3: Using MySQL CLI (mysql command)

If MySQL is in your system PATH:
```bash
mysql -h localhost -u root -psoumya@0210 -e "CREATE DATABASE spl_auction_db;"
```

---

## Database Credentials

From `application.properties`:
- **Host**: localhost
- **Port**: 3306
- **Username**: root
- **Password**: soumya@0210
- **Database Name**: spl_auction_db

---

## After Creating the Database

### Step 1: Verify the Database Exists

**Using MySQL Workbench:**
- Connect to your MySQL server
- Look in the left panel under "SCHEMAS"
- You should see `spl_auction_db`

**Using Command Line:**
```cmd
mysql -u root -psoumya@0210 -e "SHOW DATABASES;"
```

### Step 2: Build the Project

```cmd
cd "C:\Users\soumyadeep DEY\IdeaProjects\SPL-2"
.\mvnw clean compile
```

Expected output:
```
[INFO] BUILD SUCCESS
```

### Step 3: Run the Application

**Option A: Using Maven**
```cmd
.\mvnw spring-boot:run
```

**Option B: Using JAR**
```cmd
java -jar target/SPL-2-0.0.1-SNAPSHOT.jar
```

### Step 4: Access the Application

Open your browser:
```
http://localhost:8080/index.html
```

---

## What Happens After

1. **Hibernate Auto-DDL** will create all tables automatically:
   - `players` table
   - `teams` table
   - `bids` table
   - `auction_state` table

2. **No manual table creation needed** - Spring Boot + Hibernate handles it

3. **Tables are created on first run** with the configured columns and relationships

---

## Troubleshooting

### If You Get: "Access denied for user 'root'@'localhost'"
- Check the password in `application.properties`
- Make sure MySQL is running
- Verify credentials are correct

### If You Get: "MySQL server is not running"
1. Start MySQL service:
   - Windows: Services → MySQL80 → Start
   - Or use: `net start MySQL80`

### If Database Still Doesn't Exist
```cmd
# Try this SQL directly:
mysql -u root -psoumya@0210 -v -e "CREATE DATABASE IF NOT EXISTS spl_auction_db; SHOW DATABASES;"
```

### If MySQL Command Not Found
Add MySQL to PATH or use full path:
```cmd
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql" -u root -psoumya@0210 -e "CREATE DATABASE spl_auction_db;"
```

---

## Quick Setup Checklist

- [ ] MySQL Server is installed
- [ ] MySQL Server is running
- [ ] Database `spl_auction_db` is created
- [ ] Application properties have correct credentials
- [ ] Project compiles: `.\mvnw clean compile`
- [ ] Application starts: `.\mvnw spring-boot:run`
- [ ] Can access: `http://localhost:8080`
- [ ] Tables created automatically

---

## Database Schema (Auto-Created)

The following tables will be created automatically:

### players
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

### teams
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

### bids
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

### auction_state
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

---

## Next Steps After Setup

1. **Register Teams**: Go to Add Team page
2. **Register Players**: Go to Register Player page
3. **Start Auction**: Go to Auction page
4. **Manage Teams**: Go to Team Management page

---

**Status**: Follow these steps to fix the database connection error

**Support**: If still having issues, check:
- MySQL service status
- Database credentials in application.properties
- Firewall settings
- MySQL port (3306) accessibility

