<?php
/**
 * Index file - API Documentation
 */
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Calendar Sync Module - API Documentation</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; line-height: 1.6; padding: 20px; background: #f4f4f4; }
        .container { max-width: 1200px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #333; border-bottom: 3px solid #0066cc; padding-bottom: 10px; margin-bottom: 20px; }
        h2 { color: #0066cc; margin-top: 30px; margin-bottom: 15px; }
        h3 { color: #555; margin-top: 20px; margin-bottom: 10px; }
        .endpoint { background: #f9f9f9; padding: 15px; border-left: 4px solid #0066cc; margin: 15px 0; }
        .method { display: inline-block; padding: 4px 8px; border-radius: 4px; font-weight: bold; margin-right: 10px; }
        .get { background: #61affe; color: white; }
        .post { background: #49cc90; color: white; }
        .put { background: #fca130; color: white; }
        .delete { background: #f93e3e; color: white; }
        code { background: #f4f4f4; padding: 2px 6px; border-radius: 3px; font-family: monospace; }
        pre { background: #2d2d2d; color: #f8f8f2; padding: 15px; border-radius: 5px; overflow-x: auto; }
        .status-badge { display: inline-block; padding: 4px 12px; border-radius: 12px; font-size: 12px; margin: 2px; }
        .status-done { background: #4caf50; color: white; }
        .status-progress { background: #ffc107; color: black; }
        .status-due { background: #f44336; color: white; }
        .status-upcoming { background: #9e9e9e; color: white; }
    </style>
</head>
<body>
    <div class="container">
        <h1>📅 Calendar Sync Module - API Documentation</h1>
        <p><strong>Version:</strong> 1.0.0</p>
        <p><strong>Base URL:</strong> <code>http://localhost:8000/api/</code></p>
        
        <h2>🚀 Quick Start</h2>
        <ol>
            <li>Import <code>database/schema.sql</code> vào phpMyAdmin</li>
            <li>Cấu hình <code>config/config.php</code></li>
            <li>Start PHP server: <code>php -S localhost:8000</code></li>
            <li>Test API: <code>http://localhost:8000/api/events.php</code></li>
        </ol>

        <h2>📡 API Endpoints</h2>

        <div class="endpoint">
            <h3><span class="method get">GET</span> /api/events.php</h3>
            <p>Get calendar events with filters</p>
            <p><strong>Query Parameters:</strong></p>
            <ul>
                <li><code>status</code> - Filter by status (comma-separated)</li>
                <li><code>start_date</code> - Start date (YYYY-MM-DD)</li>
                <li><code>end_date</code> - End date (YYYY-MM-DD)</li>
                <li><code>project_id</code> - Project UUID</li>
                <li><code>priority</code> - Priority filter (high,medium,low)</li>
            </ul>
            <pre>curl "http://localhost:8000/api/events.php?status=in_progress,done"</pre>
        </div>

        <div class="endpoint">
            <h3><span class="method get">GET</span> /api/calendar.php</h3>
            <p>Get events by date range (calendar view)</p>
            <p><strong>Required Parameters:</strong> start_date, end_date</p>
            <pre>curl "http://localhost:8000/api/calendar.php?start_date=2025-08-01&end_date=2025-08-31"</pre>
        </div>

        <div class="endpoint">
            <h3><span class="method get">GET</span> /api/upcoming.php</h3>
            <p>Get upcoming tasks (due soon)</p>
            <p><strong>Parameters:</strong> days (default: 3)</p>
            <pre>curl "http://localhost:8000/api/upcoming.php?days=7"</pre>
        </div>

        <div class="endpoint">
            <h3><span class="method get">GET</span> /api/overdue.php</h3>
            <p>Get overdue tasks</p>
            <pre>curl "http://localhost:8000/api/overdue.php"</pre>
        </div>

        <div class="endpoint">
            <h3><span class="method get">GET</span> <span class="method post">POST</span> <span class="method put">PUT</span> /api/notifications.php</h3>
            <p>Manage user notifications</p>
            <p><strong>GET:</strong> user_id, limit, unread_only</p>
            <p><strong>POST:</strong> Create new notification</p>
            <p><strong>PUT:</strong> Mark as read</p>
        </div>

        <div class="endpoint">
            <h3><span class="method get">GET</span> <span class="method post">POST</span> <span class="method put">PUT</span> <span class="method delete">DELETE</span> /api/issues.php</h3>
            <p>Task issues CRUD operations</p>
        </div>

        <div class="endpoint">
            <h3><span class="method get">GET</span> /api/statistics.php</h3>
            <p>Get statistics for events and issues</p>
            <pre>curl "http://localhost:8000/api/statistics.php?project_id=UUID"</pre>
        </div>

        <h2>🎨 Status Values</h2>
        <div>
            <span class="status-badge status-done">done</span>
            <span class="status-badge status-progress">in_progress</span>
            <span class="status-badge status-due">due</span>
            <span class="status-badge status-upcoming">upcoming</span>
        </div>

        <h2>📚 Documentation</h2>
        <p>Xem file <code>README.md</code> để biết thêm chi tiết về cài đặt, cấu hình và sử dụng.</p>

        <h2>🔗 Resources</h2>
        <ul>
            <li>Database Schema: <code>database/schema.sql</code></li>
            <li>Sample Data: <code>database/seed.sql</code></li>
            <li>Config: <code>config/config.php</code></li>
        </ul>
    </div>
</body>
</html>
