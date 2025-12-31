<?php
/**
 * Index file - Swagger Documentation Entry Point
 * This file serves the Swagger UI with proper CORS headers
 */

// Set CORS headers
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Read and serve the HTML file
$htmlFile = __DIR__ . '/api-docs.html';
if (file_exists($htmlFile)) {
    header('Content-Type: text/html; charset=utf-8');
    readfile($htmlFile);
} else {
    http_response_code(404);
    echo "Documentation file not found.";
}
?>>
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
            <li>Không cần import schema - Module query trực tiếp từ bảng tasks có sẵn</li>
            <li>Đảm bảo database Railway đã có các bảng: tasks, projects, users, task_assignees</li>
            <li>Start PHP server: <code>php -S localhost:8000</code></li>
            <li>Test API: <code>http://localhost:8000/api/statistics.php</code></li>
        </ol>

        <h2>📡 API Endpoints</h2>

        <div class="endpoint">
            <h3><span class="method get">GET</span> /api/statistics.php</h3>
            <p>Get statistics for tasks (total, completed, in progress, etc.)</p>
            <p><strong>Query Parameters:</strong></p>
            <ul>
                <li><code>proj_id</code> - Project ID (optional, if not provided returns all tasks)</li>
            </ul>
            <pre>curl "http://localhost:8000/api/statistics.php?proj_id=your-project-id"</pre>
        </div>

        <div class="endpoint">
            <h3><span class="method get">GET</span> /api/events.php</h3>
            <p>Get tasks with filters (calendar events)</p>
            <p><strong>Query Parameters:</strong></p>
            <ul>
                <li><code>status</code> - Filter by status: open, progress, done, reopen, close (comma-separated)</li>
                <li><code>start_date</code> - Start date (YYYY-MM-DD)</li>
                <li><code>end_date</code> - End date (YYYY-MM-DD)</li>
                <li><code>proj_id</code> - Project ID</li>
                <li><code>priority</code> - Priority filter: high, medium, low (comma-separated)</li>
                <li><code>uid</code> - User ID (filter by assignee)</li>
            </ul>
            <pre>curl "http://localhost:8000/api/events.php?status=progress,open&proj_id=your-project-id"</pre>
        </div>

        <div class="endpoint">
            <h3><span class="method get">GET</span> /api/calendar.php</h3>
            <p>Get tasks by date range (calendar view)</p>
            <p><strong>Required Parameters:</strong></p>
            <ul>
                <li><code>start_date</code> - Start date (YYYY-MM-DD)</li>
                <li><code>end_date</code> - End date (YYYY-MM-DD)</li>
            </ul>
            <p><strong>Optional Parameters:</strong></p>
            <ul>
                <li><code>proj_id</code> - Project ID</li>
            </ul>
            <pre>curl "http://localhost:8000/api/calendar.php?start_date=2025-08-01&end_date=2025-08-31"</pre>
        </div>

        <div class="endpoint">
            <h3><span class="method get">GET</span> /api/upcoming.php</h3>
            <p>Get upcoming tasks (due soon, status: open/progress/reopen)</p>
            <p><strong>Parameters:</strong></p>
            <ul>
                <li><code>days</code> - Number of days to look ahead (default: 7)</li>
                <li><code>uid</code> - User ID (filter by assignee)</li>
                <li><code>proj_id</code> - Project ID</li>
            </ul>
            <pre>curl "http://localhost:8000/api/upcoming.php?days=7&uid=your-user-id"</pre>
        </div>

        <div class="endpoint">
            <h3><span class="method get">GET</span> /api/overdue.php</h3>
            <p>Get overdue tasks (not done/close but past end date)</p>
            <p><strong>Parameters:</strong></p>
            <ul>
                <li><code>uid</code> - User ID (filter by assignee)</li>
                <li><code>proj_id</code> - Project ID</li>
            </ul>
            <pre>curl "http://localhost:8000/api/overdue.php?proj_id=your-project-id"</pre>
        </div>

        <div class="endpoint">
            <h3><span class="method get">GET</span> /api/user-tasks.php</h3>
            <p>Get all tasks assigned to a specific user</p>
            <p><strong>Required Parameters:</strong></p>
            <ul>
                <li><code>uid</code> - User ID</li>
            </ul>
            <p><strong>Optional Parameters:</strong></p>
            <ul>
                <li><code>proj_id</code> - Project ID (filter by project)</li>
            </ul>
            <pre>curl "http://localhost:8000/api/user-tasks.php?uid=your-user-id"</pre>
        </div>

        <div class="endpoint">
            <h3><span class="method get">GET</span> /api/project-tasks.php</h3>
            <p>Get all tasks in a project with assignees</p>
            <p><strong>Required Parameters:</strong></p>
            <ul>
                <li><code>proj_id</code> - Project ID</li>
            </ul>
            <pre>curl "http://localhost:8000/api/project-tasks.php?proj_id=your-project-id"</pre>
        </div>

        <h2>🎨 Status Values (From Java Enum)</h2>
        <div>
            <span class="status-badge status-upcoming">open</span>
            <span class="status-badge status-progress">progress</span>
            <span class="status-badge status-done">done</span>
            <span class="status-badge status-upcoming">reopen</span>
            <span class="status-badge">close</span>
        </div>

        <h2>📊 Priority Values (From Java Enum)</h2>
        <div>
            <span class="status-badge status-due">high</span>
            <span class="status-badge status-progress">medium</span>
            <span class="status-badge">low</span>
        </div>

        <h2>📚 Documentation</h2>
        <p>Module này query <strong>trực tiếp từ các bảng có sẵn</strong>:</p>
        <ul>
            <li><code>tasks</code> - Dữ liệu task từ Java application</li>
            <li><code>projects</code> - Thông tin project</li>
            <li><code>users</code> - Thông tin user</li>
            <li><code>task_assignees</code> - Quan hệ assignee</li>
        </ul>
        <p><strong>Không cần tạo bảng mới!</strong> Tất cả APIs đều query từ bảng Java có sẵn.</p>

        <h2>🔗 Response Format</h2>
        <p>Tất cả APIs trả về JSON format:</p>
        <pre>{
  "success": true,
  "data": [...],
  "message": "Success"
}</pre>
    </div>
</body>
</html>
