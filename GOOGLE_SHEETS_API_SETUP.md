# Google Sheets API - Setup Guide

## Error Resolution

You received this error:
```
403 Forbidden - Method doesn't allow unregistered callers. 
Please use API Key or other form of API consumer identity to call this API.
```

**Cause**: The Google Sheets API requires authentication. We need to set up an API Key.

**Solution**: Follow the steps below to get an API Key and configure it.

---

## Step-by-Step: Get Google Sheets API Key

### Step 1: Go to Google Cloud Console

1. Open: **https://console.cloud.google.com/apis/credentials**
2. Sign in with your Google account

### Step 2: Create a New Project (if needed)

If you don't have a project:
1. Click "Select a Project" dropdown at top
2. Click "New Project"
3. Enter project name: "Cricket Auction"
4. Click "Create"
5. Wait for project to be created

### Step 3: Enable Google Sheets API

1. Go to: **https://console.cloud.google.com/apis/library**
2. Search for: "Google Sheets API"
3. Click on it
4. Click **"Enable"** button
5. Wait for it to enable (takes a few seconds)

### Step 4: Create API Key

1. Go back to: **https://console.cloud.google.com/apis/credentials**
2. Click **"+ Create Credentials"** button (top left)
3. Select **"API Key"**
4. A dialog will show your API Key
5. **Copy the API Key** (you'll use this in next step)

**Example API Key** (yours will be different):
```
AIzaSyD-qV9nSQMZA8Yj5-qV9nSQMZA8Yj5-qV9
```

### Step 5: Add API Key to Application

1. Open this file: 
   ```
   src/main/resources/application.properties
   ```

2. Find this line:
   ```properties
   google.sheets.api.key=
   ```

3. Add your API Key (replace with your actual key):
   ```properties
   google.sheets.api.key=YOUR_API_KEY_HERE
   ```

   **Example:**
   ```properties
   google.sheets.api.key=AIzaSyD-qV9nSQMZA8Yj5-qV9nSQMZA8Yj5-qV9
   ```

4. **Save the file**

### Step 6: Restart Application

1. Stop the running application (if any)
2. Rebuild:
   ```bash
   cd "C:\Users\soumyadeep DEY\IdeaProjects\SPL-2"
   mvn clean compile
   ```
3. Run:
   ```bash
   mvn spring-boot:run
   ```
   Or:
   ```bash
   java -jar target/SPL-2-0.0.1-SNAPSHOT.jar
   ```

### Step 7: Test Sync

1. Open: **http://localhost:8080/player-registration.html**
2. Click **"🔄 Sync Players from Google Form"**
3. Should now work without 403 error!

---

## Securing Your API Key

### ⚠️ Important Security Notes

**Do NOT:**
- ❌ Share your API Key publicly
- ❌ Commit it to GitHub
- ❌ Post it in forums or chats
- ❌ Send it via email

**Do:**
- ✅ Keep it private
- ✅ Store it in application.properties (not in code)
- ✅ Use environment variables for production
- ✅ Regenerate if accidentally exposed

### For Production Deployment

Instead of hardcoding in application.properties, use environment variable:

**Windows:**
```bash
set GOOGLE_SHEETS_API_KEY=your_api_key_here
```

**Linux/Mac:**
```bash
export GOOGLE_SHEETS_API_KEY=your_api_key_here
```

**Update application.properties:**
```properties
google.sheets.api.key=${GOOGLE_SHEETS_API_KEY}
```

---

## Troubleshooting

### Error: "API Key not valid"
- Make sure you copied the entire API Key
- Check for extra spaces before/after
- Regenerate a new API Key

### Error: "Sheets API not enabled"
- Go to: https://console.cloud.google.com/apis/library
- Search and enable "Google Sheets API"
- Wait a few minutes for it to activate

### Error: "Still getting 403"
- Verify API Key is in application.properties correctly
- Restart the application after adding key
- Check that Sheets API is enabled in your project

### The sync button still doesn't work
1. Check application console for error messages
2. Verify API Key is correct (no typos)
3. Make sure Google Sheets API is enabled
4. Try with a different Google account

---

## Verifying API Key Works

### Test 1: Check Configuration
Look for this in your application console on startup:
```
[INFO] Syncing players from Google Form sheet: 1R7gd375x-UXpw0bfa-T2RkeM7-IoIm58uDRnhJ_wJgo
```

### Test 2: Click Sync Button
If you see:
```
✅ Successfully synced! Added X new players. Total: Y
```
Then the API Key is working! ✓

### Test 3: Check Logs
If you see this in console (good):
```
[INFO] Added player: Virat Kohli
[INFO] Successfully fetched 5 players from Google Sheets
```

If you see this (problem):
```
[ERROR] Error fetching from Google Sheets: 403 Forbidden
```
Then API Key is not configured correctly.

---

## What the API Key Does

With the API Key:
- ✅ Allows access to Google Sheets API
- ✅ Your app can read form responses
- ✅ Sync works automatically
- ✅ No user login needed
- ✅ Works for public and private sheets (if you have access)

---

## Complete Configuration Example

**Final application.properties:**
```properties
spring.application.name=SPL-2

# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/spl_auction_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=soumya@0210
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Server Configuration
server.port=8080

# Google Sheets API Configuration
google.sheets.api.key=AIzaSyD-qV9nSQMZA8Yj5-qV9nSQMZA8Yj5-qV9
```

---

## Need Help?

### Check These Resources
1. **Google Cloud Documentation**: https://cloud.google.com/docs
2. **Google Sheets API Docs**: https://developers.google.com/sheets
3. **API Key Documentation**: https://cloud.google.com/docs/authentication/api-keys

### Common Issues & Fixes

| Issue | Solution |
|-------|----------|
| API Key not working | Verify Sheets API is enabled in your project |
| 403 Forbidden error | Check API Key is in application.properties |
| "Key not found" | Make sure you have an API Key created |
| Still getting errors | Restart application after adding key |

---

## Next Steps

1. ✅ Create Google Cloud Project
2. ✅ Enable Google Sheets API
3. ✅ Create API Key
4. ✅ Add API Key to application.properties
5. ✅ Restart application
6. ✅ Test sync button
7. ✅ Enjoy automatic player imports!

---

**Status**: Setup Required

**Time**: 5-10 minutes

**Difficulty**: Easy

**Your Google Sheet**: Already configured and ready to sync!

