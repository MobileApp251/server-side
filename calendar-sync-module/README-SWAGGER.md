# 📖 Swagger Documentation - Quick Guide

## ⚡ Quick Start (3 Steps)

### Step 1: Start the Server
```bash
# Option A: Double-click (Windows)
START-SERVER.bat

# Option B: Command line
php -S localhost:8080
```

### Step 2: Open Browser
Navigate to: **http://localhost:8080**

### Step 3: Test APIs
Click on any endpoint → "Try it out" → Fill parameters → "Execute"

---

## ✅ What's Included

| File | Purpose |
|------|---------|
| `swagger.yaml` | OpenAPI 3.0 specification |
| `swagger.json` | JSON version for Postman |
| `api-docs.html` | Interactive Swagger UI |
| `index.php` | Server entry point with CORS |
| `swagger-yaml.php` | YAML server with CORS |
| `.htaccess` | Apache CORS config |
| `postman-collection.json` | Postman collection |
| `START-SERVER.bat` | Quick start script |
| `DEMO-ENDPOINTS.md` | Demo API documentation |

### 🎯 Demo Endpoints (No Database Required)
- `/api/calendar-demo.php` - Mock calendar events
- `/api/issues-demo.php` - Mock issues  
- `/api/notifications-demo.php` - Mock notifications

**Use demo endpoints to test Swagger UI without database!**

---

## 🎯 Testing APIs

### Option A: Test with DEMO endpoints (No Database)
```bash
# These work immediately without any setup!
curl "http://localhost:8080/api/calendar-demo.php?start_date=2025-01-01&end_date=2025-01-31"
curl "http://localhost:8080/api/issues-demo.php?task_id=123&proj_id=550e8400-e29b-41d4-a716-446655440000"
curl "http://localhost:8080/api/notifications-demo.php?uid=660e8400-e29b-41d4-a716-446655440000"
```

### Option B: Test with REAL endpoints (Requires Database)
1. Configure database in `config/config.php`
2. Run database schema: `database/schema.sql`
3. Test real endpoints via Swagger UI

### From Swagger UI (http://localhost:8080)
1. Find the endpoint you want to test
2. Click "Try it out"
3. Enter parameters (e.g., dates, IDs)
4. Click "Execute"
5. See the response below

### From Postman
1. Import `postman-collection.json`
2. Set environment variable `baseUrl` = `http://localhost:8080`
3. Send requests

### From cURL
```bash
# Example: Get calendar events
curl -X GET "http://localhost:8080/api/calendar.php?start_date=2025-01-01&end_date=2025-01-31"
```

---

## 🚫 Common Errors & Fixes

### ❌ "Failed to fetch" / CORS Error
**Cause:*Either:
- Use DEMO endpoints: `/api/calendar-demo.php` instead of `/api/calendar.php`
- Or configure database in `config/config.php` and run `database/schema.sql`

### ❌ "Required parameter is missing" (400 error)
**Cause:** Missing required query parameters  
**Fix:** Make sure to provide all required parameters:
- `/api/calendar.php` needs `start_date` and `end_date`
- `/api/user-tasks.php` needs `uid`
- `/api/project-tasks.php` needs `proj_id`
- etc.

**Quick Test with Demo:**
```bash
# This WILL work without database:
curl "http://localhost:8080/api/calendar-demo.php?start_date=2025-01-01&end_date=2025-01-31"
```
**Fix:** Run `php -S localhost:8080` and use `http://localhost:8080`

### ❌ "Connection refused"
**Cause:** PHP server not started  
**Fix:** Run `START-SERVER.bat` or `php -S localhost:8080`

### ❌ "404 Not Found"
**Cause:** Wrong URL or path  
**Fix:** Ensure you're at `http://localhost:8080` (root, not /api-docs.html)

### ❌ Database connection errors when testing
**Cause:** Database not configured  
**Fix:** Check `config/config.php` and ensure database is set up

---

## 📚 All Endpoints

### 📅 Calendar
- `GET /api/calendar.php` - Events by date range

### 📋 Events  
- `GET /api/events.php` - Filtered events

### ✅ Tasks
- `GET /api/user-tasks.php` - User's tasks
- `GET /api/project-tasks.php` - Project tasks
- `GET /api/upcoming.php` - Upcoming tasks
- `GET /api/overdue.php` - Overdue tasks

### 🐛 Issues
- `GET /api/issues.php` - Get issues
- `POST /api/issues.php` - Create issue
- `PUT /api/issues.php` - Update issue
- `DELETE /api/issues.php` - Delete issue

### 🔔 Notifications
- `GET /api/notifications.php` - Get notifications
- `POST /api/notifications.php` - Create notification
- `PUT /api/notifications.php` - Mark as read

### 📊 Statistics
- `GET /api/statistics.php` - Get stats

---

## 💡 Pro Tips

1. **Use filters** in Swagger UI to quickly find endpoints
2. **Save requests** in Postman for repeated testing
3. **Check Models** section in Swagger UI for data structures
4. **Use examples** provided in each endpoint
5. **Test OPTIONS** requests to verify CORS configuration

---

## 🆘 Need Help?

1. Check full documentation: `API-DOCUMENTATION.md`
2. Review migration guide: `MIGRATION-GUIDE.md`
3. Run unit tests: `tests/` directory
4. Check database schema: `database/schema.sql`

---

**🚀 Start Now:** Run `START-SERVER.bat` and open http://localhost:8080
