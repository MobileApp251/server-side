# 🎯 Demo API Endpoints

## Purpose

These demo endpoints return mock data and work **WITHOUT database connection**. Perfect for:
- Testing Swagger UI functionality
- Demonstrating API structure
- Frontend development without backend
- API documentation validation

## Available Demo Endpoints

### ✅ Working Demo APIs (No Database Required)

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/calendar-demo.php` | GET | Calendar events with mock data |
| `/api/issues-demo.php` | GET, POST, PUT, DELETE | Task issues with mock data |
| `/api/notifications-demo.php` | GET, POST, PUT | User notifications with mock data |

### ⚠️ Real APIs (Require Database)

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/calendar.php` | GET | Real calendar events from database |
| `/api/events.php` | GET | Real filtered events |
| `/api/user-tasks.php` | GET | Real user tasks |
| `/api/project-tasks.php` | GET | Real project tasks |
| `/api/upcoming.php` | GET | Real upcoming tasks |
| `/api/overdue.php` | GET | Real overdue tasks |
| `/api/issues.php` | GET, POST, PUT, DELETE | Real issues from database |
| `/api/notifications.php` | GET, POST, PUT | Real notifications from database |
| `/api/statistics.php` | GET | Real statistics from database |

---

## 🧪 Testing Demo Endpoints

### 1. Calendar Demo
```bash
# Get mock calendar events
curl "http://localhost:8080/api/calendar-demo.php?start_date=2025-01-01&end_date=2025-01-31"

# Filter by project
curl "http://localhost:8080/api/calendar-demo.php?start_date=2025-01-01&end_date=2025-01-31&proj_id=550e8400-e29b-41d4-a716-446655440000"
```

**Expected Response:**
```json
{
  "success": true,
  "data": {
    "total": 5,
    "start_date": "2025-01-01",
    "end_date": "2025-01-31",
    "events": [...],
    "_note": "This is DEMO data"
  }
}
```

### 2. Issues Demo
```bash
# Get mock issues
curl "http://localhost:8080/api/issues-demo.php?task_id=123&proj_id=550e8400-e29b-41d4-a716-446655440000"

# Create mock issue
curl -X POST "http://localhost:8080/api/issues-demo.php" \
  -H "Content-Type: application/json" \
  -d '{
    "task_id": 123,
    "proj_id": "550e8400-e29b-41d4-a716-446655440000",
    "issue_title": "Test Issue",
    "severity": "high"
  }'
```

### 3. Notifications Demo
```bash
# Get mock notifications
curl "http://localhost:8080/api/notifications-demo.php?uid=660e8400-e29b-41d4-a716-446655440000"

# Get unread only
curl "http://localhost:8080/api/notifications-demo.php?uid=660e8400-e29b-41d4-a716-446655440000&unread_only=true&limit=10"
```

---

## 📋 Testing in Swagger UI

1. **Start Server:**
   ```bash
   php -S localhost:8080
   ```

2. **Open Swagger UI:**
   - Navigate to: http://localhost:8080

3. **Test Demo Endpoints:**
   - Since demo endpoints aren't in swagger.yaml, you can test them directly via curl or browser
   - Or add them to a custom collection

4. **Test Real Endpoints:**
   - Use Swagger UI to test real endpoints
   - These require database connection

---

## 🔧 Switching Between Demo and Real

### In Your Frontend Code:

```javascript
// Development - use demo endpoints
const API_BASE = 'http://localhost:8080/api';
const USE_DEMO = true;

const endpoints = {
  calendar: USE_DEMO ? `${API_BASE}/calendar-demo.php` : `${API_BASE}/calendar.php`,
  issues: USE_DEMO ? `${API_BASE}/issues-demo.php` : `${API_BASE}/issues.php`,
  notifications: USE_DEMO ? `${API_BASE}/notifications-demo.php` : `${API_BASE}/notifications.php`,
};

// Fetch calendar events
fetch(`${endpoints.calendar}?start_date=2025-01-01&end_date=2025-01-31`)
  .then(res => res.json())
  .then(data => console.log(data));
```

### In Your Tests:

```javascript
// test.config.js
module.exports = {
  apiBaseUrl: process.env.USE_DEMO === 'true' 
    ? 'http://localhost:8080/api' 
    : 'https://production-api.com/api',
  endpoints: {
    calendar: process.env.USE_DEMO === 'true' ? 'calendar-demo.php' : 'calendar.php',
  }
};
```

---

## 🎯 Mock Data Details

### Calendar Events (5 tasks)
- Mix of statuses: in_progress, due, upcoming, done
- Mix of priorities: high, critical, medium, low
- Different projects
- Realistic dates and descriptions

### Issues (2 per task)
- Different severities: high, medium
- Different statuses: open, in_progress
- Realistic issue descriptions

### Notifications (4 notifications)
- Different types: task_due_soon, issue_created, task_assigned, task_overdue
- Mix of read/unread
- Linked to demo tasks

---

## ✅ Advantages of Demo Endpoints

1. **No Database Required** - Start testing immediately
2. **Predictable Data** - Same data every time for consistent testing
3. **Fast Response** - No database queries
4. **Safe Testing** - Can't corrupt real data
5. **Easy Development** - Frontend team can work independently

## ⚠️ Limitations

- Data doesn't persist (POST/PUT/DELETE don't actually save)
- Limited filtering logic
- Fixed dataset
- No authentication
- No pagination

---

## 🚀 Next Steps

1. **For Testing:** Use demo endpoints to validate Swagger UI
2. **For Development:** Use demo endpoints for frontend development
3. **For Production:** Configure database and use real endpoints
4. **For CI/CD:** Use demo endpoints in automated tests

---

**Quick Test:**
```bash
# Test if demo endpoint works
curl "http://localhost:8080/api/calendar-demo.php?start_date=2025-01-01&end_date=2025-01-31"

# Should return JSON with success: true and 5 events
```
