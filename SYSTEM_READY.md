# CRICKET AUCTION SYSTEM - FINAL COMPLETE SETUP

## ✅ SYSTEM IS READY TO USE!

### 🚀 HOW TO START

**Step 1: Build the application (first time only)**
```powershell
cd "C:\Users\soumyadeep DEY\IdeaProjects\SPL-2"
.\mvnw.cmd clean package -DskipTests
```

**Step 2: Run the application**
```powershell
.\mvnw.cmd spring-boot:run
```

**EXPECTED OUTPUT IN TERMINAL:**
```
2026-03-17T21:XX:XX.XXX+05:30  INFO ... Tomcat started on port 8080
Application entry point: http://localhost:8080/
```

**Step 3: Open in Browser**
```
http://localhost:8080/
```

---

## 📋 COMPLETE WORKFLOW GUIDE

### PHASE 1: IMPORT PLAYERS FROM CSV

**On the Home Page:**
1. Click **"Import Players CSV"** button (yellow/orange button)
2. Or click **"Import from Sheets"** in navigation bar

**On the Import Page:**
1. Click **"Import CSV"** tab (if not already selected)
2. Click **"Choose File"** and select your CSV file
3. CSV Format (MUST have this header row):
   ```
   Name	Date Of Birth	Age	Role	Batting Type	Bowling Type	Image	Category
   ```
   **Important:** All players will be imported, NO age restrictions
4. Click **"Upload CSV and Import"** button
5. Wait for results showing:
   - Total Processed
   - Successfully Imported  
   - Failed
6. All players now in database with status: REGISTERED

**Example CSV Row:**
```
Virat Kohli	1988-11-05	35	Batter	Right-hand	None	https://example.com/virat.png	International
```

---

### PHASE 2: CREATE TEAMS

**On the Home Page:**
1. Click **"Add New Team"** button (purple button)
2. Fill in:
   - Team Name
   - Total Budget (e.g., 10000000 for 1 Crore)
   - Captain Name
3. Click **"Create Team"**
4. Team appears with remaining budget = total budget
5. Repeat to create more teams

---

### PHASE 3: START AUCTION

**On the Home Page:**
1. Click **"Start Auction"** button (cyan/turquoise button)
2. Or click **"Auction"** in navigation bar

**On Auction Page:**
1. Click **"Initialize Auction"** button
   - System copies all non-retained players from REGISTERED to AUCTION
   - Organizes by Role Priority:
     1. All-rounder Bowling
     2. All-rounder Batting  
     3. Only Batsman
     4. Only Bowler
     5. Wicket Keeper
   - Within each role, players are randomly shuffled
   - Queue is ready for sequential retrieval

---

### PHASE 4: SPIN PLAYERS & CONDUCT AUCTION

**On Auction Page - Once Initialized:**

1. **Click "Spin Next Player" button**
   - Next player from queue appears with details:
     - Player Name
     - Age
     - Role
     - Base Price (₹500,000 default)
     - Image

2. **Two Options Appear:**

   **OPTION A: SELL PLAYER**
   - Enter Team Name
   - Enter Sold Price (₹ amount)
   - Click "SELL" button
   - System validates:
     - Team exists
     - Team has enough remaining budget
     - Player not already sold
   - If valid:
     - Player removed from AUCTION
     - Player inserted into SOLD table
     - Team's remaining budget REDUCED by sold price
     - Player associated with team
   
   **OPTION B: MARK UNSOLD**
   - Click "UNSOLD" button
   - Player removed from AUCTION
   - Player inserted into UNSOLD table

3. **Automatic Cycle:**
   - When AUCTION becomes empty
   - System checks UNSOLD table
   - If players exist in UNSOLD:
     - Move all from UNSOLD → AUCTION
     - Clear UNSOLD table
     - Reapply shuffle logic
     - Continue auction cycle
   - Repeat until both AUCTION and UNSOLD are empty
   - All players are either:
     - In SOLD (purchased by teams)
     - Permanently UNSOLD (no team bought)

---

### PHASE 5: VIEW RESULTS

**Teams Page:**
1. Click **"Navigate Teams"** button or in nav bar
2. See all teams with:
   - Team Name
   - Remaining Budget (updated after each sale)
   - Captain Name
3. Click on team card to view details:
   - All players bought by this team
   - Players' details
   - Release button (to unsell and add back to auction)

**Sold Players Page:**
1. Navigate to view all sold players
2. Shows all players in SOLD table with:
   - Player Name
   - Age, Role, Image
   - Team Name (who bought)
   - Sold Price
   - Timestamp

**Unsold Players Page:**
1. Navigate to view all permanently unsold players
2. Shows all players in UNSOLD table

---

## 🎯 API ENDPOINTS (FOR REFERENCE)

### Import Endpoints
```
POST /api/import/upload-csv           - Upload CSV file
POST /api/import/upload-excel          - Upload Excel file
POST /api/import/players-from-url      - Import from Google Sheets URL
POST /api/import/players-from-sheets   - Import from Spreadsheet ID
```

### Player Endpoints  
```
GET  /api/players                  - List all (with filters)
GET  /api/players?status=REGISTERED - Only registered players
GET  /api/players/{id}             - Get single player
POST /api/players                  - Create player
PUT  /api/players/{id}             - Update player
DELETE /api/players/{id}           - Delete player
```

### Team Endpoints
```
GET  /api/teams                    - List all teams
POST /api/teams                    - Create team
GET  /api/teams/{id}               - Get team details
PUT  /api/teams/{id}               - Update team
DELETE /api/teams/{id}             - Delete team
POST /api/teams/{teamId}/release-player/{playerId} - Release player
```

### Auction Endpoints
```
POST /api/auction/initialize       - Start auction (copy to AUCTION table)
GET  /api/auction/next-player      - Get next player in queue
POST /api/auction/sell-player/{playerId} - Sell player
POST /api/auction/unsold-player/{playerId} - Mark unsold
POST /api/auction/move-to-next-category - Move to next role category
GET  /api/auction/status           - Get current auction status
GET  /api/auction/categories       - List all categories
```

---

## ✅ CSV FILE REQUIREMENTS

### Header Row (Row 1):
```
Name | Date Of Birth | Age | Role | Batting Type | Bowling Type | Image | Category
```

### Data Rows (Starting Row 2):
- **Name**: Player name (required)
- **Date Of Birth**: YYYY-MM-DD format (optional)
- **Age**: Number (any age accepted, NO RESTRICTION)
- **Role**: One of:
  - All-rounder Bowling
  - All-rounder Batting
  - Only Batsman
  - Only Bowler
  - Wicket Keeper
- **Batting Type**: Right-hand, Left-hand, etc. (optional)
- **Bowling Type**: Right-arm fast, Left-arm spin, etc. (optional)
- **Image**: URL to player image (optional)
- **Category**: Domestic, International, etc. (optional)

### Example CSV Content:
```
Name	Date Of Birth	Age	Role	Batting Type	Bowling Type	Image	Category
Virat Kohli	1988-11-05	35	Only Batsman	Right-hand	None	https://example.com/virat.png	International
Jasprit Bumrah	1993-12-06	31	Only Bowler	N/A	Right-arm fast	https://example.com/bumrah.png	International
Rohit Sharma	1987-04-30	36	Only Batsman	Right-hand	None	https://example.com/rohit.png	International
Rishabh Pant	1997-10-04	26	Wicket Keeper	Right-hand	Right-arm medium	https://example.com/pant.png	International
```

---

## 🔧 IMPORTANT FEATURES IMPLEMENTED

✅ **Age Validation Removed** - All players accepted regardless of age
✅ **CSV Structure Matches** - Name, DOB, Age, Role, Batting Type, Bowling Type, Image, Category
✅ **All Players Imported** - No skipping based on age or other criteria
✅ **Default Base Price** - ₹500,000 set for all imported players
✅ **Entry Point Printed** - Terminal shows "Application entry point: http://localhost:8080/"
✅ **No Static Demo Data** - All demo/example players removed from UI
✅ **Frontend Connected to Backend** - All buttons call real API endpoints
✅ **Role Priority Ordering** - Auction follows correct category sequence
✅ **Shuffle Within Role** - Random selection within each role group
✅ **Budget Management** - Team budget decremented on sale, incremented on release
✅ **Atomic Transactions** - All operations are transactional and safe
✅ **Unsold Recycling** - Unsold players automatically re-enter auction when empty

---

## 🐛 TROUBLESHOOTING

### Issue: "Port 8080 already in use"
```powershell
# Find process using port 8080
$proc = Get-NetTCPConnection -LocalPort 8080 | Select-Object OwningProcess
Stop-Process -Id $proc.OwningProcess -Force

# Then run the app again
.\mvnw.cmd spring-boot:run
```

### Issue: "Players not importing from CSV"
- Check CSV has header row in row 1
- Check columns are separated by TAB or comma (comma preferred)
- Check player names and roles are not empty
- Check CSV file is in UTF-8 encoding

### Issue: "Can't see Import button"
- Make sure you're on http://localhost:8080/
- Click "Import Players CSV" button on home page (yellow/orange button)
- Or click "Import from Sheets" in navigation bar

### Issue: "Teams not updating after sale"
- Check team budget was sufficient
- Refresh page (F5) to see updated values
- Check browser console for error messages

---

## 📊 DATABASE TABLES

The system uses these tables:

1. **Player** - All player data with status (REGISTERED/SOLD/UNSOLD)
2. **Team** - Team information with budget tracking
3. **Auction** - Active auction pool (non-retained players during auction)
4. **Sold** - Players sold with team and price info
5. **Unsold** - Players not sold during current cycle
6. **AuctionState** - Tracks current auction state and progress

---

## ✨ COMPLETE END-TO-END EXAMPLE

**1. CSV FILE (52 players):**
```
Upload "SPL 2026 form (Responses) - Form responses 1.csv"
All 52 players imported with status REGISTERED
```

**2. CREATE TEAMS:**
```
- Team A (Budget: ₹10,00,000) Captain: Gautam Gambhir
- Team B (Budget: ₹10,00,000) Captain: Sanjay Bangar
- etc...
```

**3. INITIALIZE AUCTION:**
```
- All 52 players copied to AUCTION table
- Sorted by Role Priority + Shuffled
- Queue ready
```

**4. AUCTION LOOP:**
```
Spin 1: Virat Kohli → Sold to Team A for ₹2,00,000
Spin 2: Jasprit Bumrah → Unsold
Spin 3: Rohit Sharma → Sold to Team B for ₹1,80,000
...continue until AUCTION empty...
- All unsold players from UNSOLD moved back to AUCTION
- Continue auction
...repeat until all players assigned or permanent unsold...
```

**5. FINAL RESULT:**
```
- Team A: Virat, Rohit, Rishabh (15 players total)
- Team B: Jasprit, Bumrah, Sharma (14 players total)
- etc...
- Permanently Unsold: (5 players with no bids)
```

---

## 🎬 START NOW!

1. Open PowerShell
2. Run: `cd "C:\Users\soumyadeep DEY\IdeaProjects\SPL-2"`
3. Run: `.\mvnw.cmd spring-boot:run`
4. Wait for: `Application entry point: http://localhost:8080/`
5. Open browser: `http://localhost:8080/`
6. Click "Import Players CSV"
7. Upload your CSV file
8. Follow the workflow!

**SYSTEM IS FULLY FUNCTIONAL AND READY TO USE! 🎉**

