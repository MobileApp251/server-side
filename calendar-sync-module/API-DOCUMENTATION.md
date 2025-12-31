# Calendar Sync Module - API Documentation

## 📚 Overview

This is the complete Swagger/OpenAPI documentation for the Calendar Sync Module. The module provides a comprehensive REST API for managing calendar events, tasks, issues, notifications, and analytics.

## 🚀 Quick Start

### Viewing the Documentation

**⚠️ IMPORTANT: You MUST use a local server to view the documentation properly due to CORS restrictions.**

#### Option 1: Using Start Script (Easiest - Windows)
```bash
# Double-click or run:
START-SERVER.bat

# Then open in browser:
# http://localhost:8080
```

#### Option 2: Using PHP Built-in Server
```bash
# Navigate to the module directory
cd calendar-sync-module

# Start the server
php -S localhost:8080

# Open in browser:
# http://localhost:8080
```

The documentation will automatically open at `http://localhost:8080` and you can test all APIs directly.

#### Option 3: Using Swagger Editor Online (View Only)
1. Go to https://editor.swagger.io/
2. Copy the contents of `swagger.yaml`
3. Paste into the Swagger Editor

**Note:** Opening `api-docs.html` directly in browser (file://) will NOT work due to CORS restrictions.

#### Option 4: Using VS Code Extension
1. Install "Swagger Viewer" extension in VS Code
2. Open `swagger.yaml`
3. Right-click and select "Preview Swagger"

## 📖 API Endpoints

### Calendar
- `GET /api/calendar.php` - Get events by date range

### Events
- `GET /api/events.php` - Get filtered calendar events

### Tasks
- `GET /api/user-tasks.php` - Get tasks by user
- `GET /api/project-tasks.php` - Get tasks by project
- `GET /api/upcoming.php` - Get upcoming tasks
- `GET /api/overdue.php` - Get overdue tasks

### Issues
- `GET /api/issues.php` - Get issues for a task
- `POST /api/issues.php` - Create new issue
- `PUT /api/issues.php` - Update issue
- `DELETE /api/issues.php` - Delete issue

### Notifications
- `GET /api/notifications.php` - Get user notifications
- `POST /api/notifications.php` - Create notification
- `PUT /api/notifications.php` - Mark notification(s) as read

### Statistics
- `GET /api/statistics.php` - Get comprehensive statistics

## 🔑 Authentication

Currently, the API does not enforce authentication. However, security schemes are defined in the Swagger documentation for future implementation:

- **API Key Authentication**: Header-based `X-API-Key`
- **Bearer Authentication**: JWT tokens

To enable authentication, uncomment the security section at the bottom of `swagger.yaml`.

## 📝 Request Examples

### Get Calendar Events
```bash
# Make sure PHP server is running first!
# Start with: php -S localhost:8080

curl -X GET "http://localhost:8080/api/calendar.php?start_date=2025-01-01&end_date=2025-01-31" \
  -H "Content-Type: application/json"
```

### Create an Issue
```bash
curl -X POST "http://localhost:8080/api/issues.php" \
  -H "Content-Type: application/json" \
  -d '{
    "task_id": 123,
    "proj_id": "550e8400-e29b-41d4-a716-446655440000",
    "issue_title": "Bug in authentication",
    "issue_description": "Users cannot login",
    "severity": "high"
  }'
```

### Get User Notifications
```bash
curl -X GET "http://localhost:8080/api/notifications.php?uid=660e8400-e29b-41d4-a716-446655440000&limit=10" \
  -H "Content-Type: application/json"
```

## ⚠️ Troubleshooting

### CORS Errors in Swagger UI

If you see errors like:
- "Failed to fetch"
- "CORS"
- "Network Failure"
- "URL scheme must be 'http' or 'https' for CORS request"

**Solution:**
1. Make sure you're running the PHP server: `php -S localhost:8080`
2. Access documentation via `http://localhost:8080` (NOT by opening the HTML file directly)
3. Verify all API files have CORS headers (they should already be configured)

### Server Configuration

The module includes:
- `index.php` - Main entry point with CORS headers
- `swagger-yaml.php` - Serves YAML with proper headers
- `.htaccess` - Apache CORS configuration
- `START-SERVER.bat` - Quick start script for Windows

## 🧪 Testing with Swagger UI

The Swagger UI interface (`api-docs.html`) provides an interactive way to test all APIs:

1. Open `api-docs.html` in your browser
2. Expand any endpoint
3. Click "Try it out"
4. Fill in the required parameters
5. Click "Execute"
6. View the response

## 📦 Response Format

All API responses follow this standard format:

### Success Response
```json
{
  "success": true,
  "data": {
    // Response data varies by endpoint
  }
}
```

### Error Response
```json
{
  "success": false,
  "error": "Error message",
  "code": 400
}
```

## 🎯 Status Codes

- `200 OK` - Request successful
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid parameters or missing required fields
- `500 Internal Server Error` - Server-side error

## 🛠️ Development

### Updating the Documentation

When you add or modify API endpoints:

1. Edit `swagger.yaml` to reflect the changes
2. Test the changes in Swagger Editor or Swagger UI
3. Ensure all required fields and responses are documented
4. Update examples if needed

### Tools

- **Swagger Editor**: https://editor.swagger.io/
- **Swagger UI**: Included in `api-docs.html`
- **OpenAPI Specification**: https://swagger.io/specification/

## 📋 Data Models

### CalendarEvent
- `id`: integer
- `task_id`: integer
- `proj_id`: UUID
- `title`: string
- `description`: string
- `start_date`: datetime
- `end_date`: datetime
- `status`: enum (done, in_progress, due, upcoming)
- `priority`: enum (high, medium, low)

### TaskIssue
- `id`: integer
- `task_id`: integer
- `proj_id`: UUID
- `issue_title`: string
- `issue_description`: string
- `severity`: enum (low, medium, high, critical)
- `status`: enum (open, in_progress, resolved, closed)

### Notification
- `id`: integer
- `uid`: UUID
- `task_id`: integer
- `notification_type`: enum
- `message`: string
- `is_read`: boolean

## 🔗 Related Files

- `swagger.yaml` - OpenAPI specification
- `api-docs.html` - Interactive Swagger UI
- `api/` - PHP API endpoints
- `models/` - Data models
- `config/` - Configuration files

## 💡 Tips

1. **Use the filter feature** in Swagger UI to quickly find endpoints
2. **Export the Swagger spec** to use with API testing tools like Postman
3. **Try it out** feature works best when the PHP server is running
4. **Check the Models section** at the bottom of Swagger UI for data structures

## 📞 Support

For issues or questions:
- Check the main `README.md` in the project root
- Review `MIGRATION-GUIDE.md` for integration details
- Examine the test files in `tests/` directory

## 📄 License

MIT License - See main project LICENSE file

---

**Last Updated**: December 30, 2025  
**API Version**: 1.0.0  
**OpenAPI Version**: 3.0.3
