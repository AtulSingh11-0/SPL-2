# DATABASE ERROR FIX - COMPLETE GUIDE

## Problem Summary

When you tried to start the Spring Boot application, you got this error:

```
org.hibernate.exception.SQLGrammarException: 
Unable to obtain isolated JDBC connection 
[Unknown database 'spl_auction_db']
```

**Root Cause**: The MySQL database `spl_auction_db` doesn't exist yet.

---

## Why This Happened

1. Application tries to connect to MySQL database at startup
2. Hibernate configuration is set to `ddl-auto=update` (which updates existing schema)
3. But database must exist first for Hibernate to connect
4. Database was never created manually

---

## How to Fix It

### Prerequisites
- MySQL Server 8.0+ installed
- MySQL is running
- You have access to MySQL with credentials: `root` / `soumya@0210`

### Step-by-Step Fix

#### Method 1: MySQL Workbench (Easiest - Recommended)

1. **Open MySQL Workbench**
   - Start → Search → MySQL Workbench → Open

2. **Create New Query**
   - File → New Query Tab
   - Or click the "+" icon next to query tabs

3. **Paste SQL Command**
   ```sql
   CREATE DATABASE IF NOT EXISTS spl_auction_db;
   ```

4. **Execute**
   - Click lightning bolt icon (Execute)
   - Or press Ctrl+Enter

5. **Verify Success**
   - You should see message: "Query executed successfully"
   - In left panel, refresh (right-click Schemas → Refresh)
   - `spl_auction_db` should appear

#### Method 2: Command Line

**Windows PowerShell:**
```powershell
# Navigate to your project
cd "C:\Users\soumyadeep DEY\IdeaProjects\SPL-2"

# Create database
mysql -u root -psoumya@0210 -e "CREATE DATABASE spl_auction_db;"

# Verify
mysql -u root -psoumya@0210 -e "SHOW DATABASES LIKE 'spl_auction_db';"
```

**Windows Command Prompt:**
```cmd
mysql -u root -psoumya@0210 -e "CREATE DATABASE spl_auction_db;"
```

If `mysql` command not found, use full path:
```cmd
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql" -u root -psoumya@0210 -e "CREATE DATABASE spl_auction_db;"
```

---

## After Creating Database

### Rebuild and Run Application

```powershell
cd "C:\Users\soumyadeep DEY\IdeaProjects\SPL-2"

# Clean build
.\mvnw clean compile

# Run application
.\mvnw spring-boot:run
```

### Expected Output

```
[INFO] BUILD SUCCESS
...
2026-01-01T... INFO 12345 --- [  restartedMain] o.s.b.w.e.tomcat.TomcatWebServer: Tomcat started on port(s): 8080
2026-01-01T... INFO 12345 --- [  restartedMain] com.example.spl2.Spl2Application: Started Spl2Application in X.XXX seconds
```

### Access Application

```
http://localhost:8080/index.html
```

---

## What Happens Next

1. **Hibernate Auto-DDL**: Spring Boot automatically creates tables:
   - `players` - Player data
   - `teams` - Team data
   - `bids` - Bid history
   - `auction_state` - Auction progress

2. **No Manual SQL Needed**: All tables created automatically

3. **Database Ready**: You can immediately start using the application

---

## Database Configuration

**File**: `src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/spl_auction_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=soumya@0210
spring.jpa.hibernate.ddl-auto=update
```

**What each line means:**
- `url`: Connect to MySQL on localhost, port 3306, database `spl_auction_db`
- `username`: Login as user `root`
- `password`: Password is `soumya@0210`
- `ddl-auto=update`: Automatically create/update tables if needed

---

## Verify Database Created

### In MySQL Workbench

1. Open MySQL Workbench
2. Look at left panel under "SCHEMAS"
3. Find `spl_auction_db`
4. Click arrow to expand
5. Should see tables after running application:
   - players
   - teams
   - bids
   - auction_state

### In Command Line

```cmd
mysql -u root -psoumya@0210 spl_auction_db -e "SHOW TABLES;"
```

Should output:
```
+------------------------+
| Tables_in_spl_auction_db|
+------------------------+
| auction_state          |
| bids                   |
| players                |
| teams                  |
+------------------------+
```

---

## Common Issues & Solutions

### Issue 1: "Unknown database 'spl_auction_db'"
**Solution**: Follow the "How to Fix It" section above

### Issue 2: "Access denied for user 'root'"
**Solution**: 
- Check password is exactly: `soumya@0210`
- Make sure no typos
- Verify MySQL user 'root' exists

### Issue 3: "MySQL server has gone away"
**Solution**:
- MySQL is not running
- Windows: Start → Services → MySQL80 → Start
- Or in Command Prompt (as Admin): `net start MySQL80`

### Issue 4: "mysql command not found"
**Solution**:
- MySQL bin directory not in PATH
- Use full path: `C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql`
- Or add to system PATH

### Issue 5: Database created but still getting error
**Solution**:
- Restart the Spring Boot application
- Clear any cached connections
- Check connection properties in `application.properties`

---

## Testing the Setup

After starting the application, test these:

1. **Homepage loads**: http://localhost:8080/index.html
2. **Add a team**: Click "Add Team" button
3. **Register a player**: Click "Register Player" button
4. **Start auction**: Click "Auction" button
5. **Tables created**: Check database (see "Verify" section above)

---

## Configuration Details

### MySQL Requirements
- Version: 8.0+
- Charset: utf8mb4 (default)
- Collation: utf8mb4_unicode_ci (default)
- Engine: InnoDB (default)

### Spring Boot Configuration
- DDL Mode: `update` (auto-create/update tables)
- Show SQL: `false` (don't log all SQL)
- Format SQL: `true` (pretty print SQL in logs)

---

## Files Created

- **DATABASE_SETUP.md** - Comprehensive database setup guide
- **QUICK_DATABASE_FIX.txt** - Quick reference card
- **This file** - Complete problem and solution

---

## Next Actions

1. ✅ Create database (follow "How to Fix It" above)
2. ✅ Restart application
3. ✅ Access http://localhost:8080/index.html
4. ✅ Start using the system!

---

## Support

If you still have issues:

1. **Check MySQL is running**
   - Windows: Services → MySQL80 should say "Running"
   - Test: `mysql -u root -psoumya@0210 -e "SELECT 1;"`

2. **Verify credentials**
   - Check `application.properties` for correct username/password
   - Test: `mysql -u root -psoumya@0210 -e "SHOW DATABASES;"`

3. **Check database exists**
   - Command: `mysql -u root -psoumya@0210 -e "SHOW DATABASES LIKE 'spl_auction_db';"`

4. **Check port 3306 is open**
   - Firewall might be blocking
   - Windows Defender → Allow MySQL through firewall

---

**Status**: ✅ Complete Fix Available

**Time to Fix**: ~5 minutes

**Difficulty**: Easy - Just create one database

**After Fix**: Application will be fully functional!

