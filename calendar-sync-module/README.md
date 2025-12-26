# Calendar Sync Module - Issue vs Calendar Sync

Module PHP độc lập cho chức năng đồng bộ hóa Issues với Calendar trong dự án CloneJira.

## 📋 Tính năng

### 1. **Calendar Event Management**
- Quản lý events liên kết với tasks
- Filter theo status (Done, In Progress, Due, Upcoming, Overdue)
- Filter theo khoảng thời gian (start_date, end_date)
- Filter theo priority (High, Medium, Low)
- Calendar view theo tháng/tuần

### 2. **Task Notifications**
- Thông báo upcoming tasks (trước deadline)
- Thông báo overdue tasks (quá hạn)
- Thông báo task assignments
- Thông báo task updates
- Đánh dấu đã đọc/chưa đọc

### 3. **Task Issues**
- Quản lý issues trong mỗi task
- CRUD operations cho issues
- Track issue status và priority
- Assign issues cho members

### 4. **Statistics & Reports**
- Thống kê tasks theo status
- Thống kê issues
- Dashboard metrics

## 🗂️ Cấu trúc thư mục

```
calendar-sync-module/
├── api/                    # API Endpoints
│   ├── events.php         # Get/filter calendar events
│   ├── calendar.php       # Calendar view by date range
│   ├── upcoming.php       # Get upcoming tasks
│   ├── overdue.php        # Get overdue tasks
│   ├── notifications.php  # Notification management
│   ├── issues.php         # Task issues CRUD
│   └── statistics.php     # Statistics & metrics
├── config/                # Configuration
│   ├── config.php        # Main config file
│   └── Database.php      # Database connection
├── models/               # Data models
│   ├── CalendarEvent.php
│   ├── TaskNotification.php
│   └── TaskIssue.php
├── database/             # Database scripts
│   ├── schema.sql       # Database schema
│   └── seed.sql         # Sample data
├── utils/               # Utilities
│   ├── Response.php     # API response helper
│   └── notification_scheduler.php  # Cron job for notifications
└── README.md

```

## 🚀 Cài đặt

### 1. **Cấu hình Database (phpMyAdmin)**

1. Tạo database mới:
```sql
CREATE DATABASE jira_clone_calendar CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Import schema:
```bash
# Trong phpMyAdmin, chọn database và import file:
database/schema.sql
```

3. (Optional) Import sample data:
```bash
database/seed.sql
```

### 2. **Cấu hình PHP**

Cập nhật file `config/config.php` với thông tin database của bạn:

```php
'database' => [
    'host' => 'localhost',
    'port' => '3306',
    'dbname' => 'jira_clone_calendar',
    'username' => 'root',
    'password' => 'your_password',
    // ...
]
```

### 3. **Cấu hình Web Server**

#### Với XAMPP/WAMP:
1. Copy thư mục `calendar-sync-module` vào `htdocs/`
2. Truy cập: `http://localhost/calendar-sync-module/api/events.php`

#### Với PHP Built-in Server:
```bash
cd calendar-sync-module
php -S localhost:8000
```

Truy cập: `http://localhost:8000/api/events.php`

## 📡 API Endpoints

### 1. **Get Calendar Events with Filters**
```
GET /api/events.php
```

**Query Parameters:**
- `status`: done, in_progress, due, upcoming, overdue (comma-separated)
- `start_date`: YYYY-MM-DD
- `end_date`: YYYY-MM-DD
- `project_id`: UUID
- `priority`: high, medium, low (comma-separated)

**Example:**
```bash
curl "http://localhost:8000/api/events.php?status=in_progress,upcoming&start_date=2025-08-01&end_date=2025-08-31"
```

### 2. **Get Calendar View by Date Range**
```
GET /api/calendar.php?start_date=2025-08-01&end_date=2025-08-31
```

### 3. **Get Upcoming Tasks**
```
GET /api/upcoming.php?days=3
```

### 4. **Get Overdue Tasks**
```
GET /api/overdue.php
```

### 5. **Notifications**

**Get notifications:**
```
GET /api/notifications.php?user_id=UUID&limit=50&unread_only=true
```

**Create notification:**
```
POST /api/notifications.php
Content-Type: application/json

{
  "user_id": "user-uuid",
  "task_id": 1,
  "project_id": "project-uuid",
  "notification_type": "upcoming",
  "title": "Upcoming task",
  "message": "Task is due soon"
}
```

**Mark as read:**
```
PUT /api/notifications.php
Content-Type: application/json

{
  "notification_id": 1
}
```

### 6. **Task Issues**

**Get issues:**
```
GET /api/issues.php?task_id=1&project_id=UUID
```

**Create issue:**
```
POST /api/issues.php
Content-Type: application/json

{
  "task_id": 1,
  "project_id": "project-uuid",
  "issue_title": "Issue #1",
  "issue_content": "Description",
  "status": "open",
  "priority": "high",
  "created_by": "user-uuid"
}
```

**Update issue:**
```
PUT /api/issues.php
Content-Type: application/json

{
  "id": 1,
  "status": "resolved"
}
```

**Delete issue:**
```
DELETE /api/issues.php
Content-Type: application/json

{
  "id": 1
}
```

### 7. **Statistics**
```
GET /api/statistics.php?project_id=UUID
```

## 🔔 Notification Scheduler

Để tự động kiểm tra và gửi thông báo cho upcoming/overdue tasks, setup cron job:

```bash
# Chạy mỗi giờ
0 * * * * php /path/to/calendar-sync-module/utils/notification_scheduler.php

# Hoặc test thủ công:
php utils/notification_scheduler.php
```

## 🎨 Mapping với Figma Design

### Dashboard Filters:
- **Status Filter**: API `/api/events.php?status=done,in_progress,due,upcoming`
- **Date Filter**: API `/api/events.php?start_date=2025-08-17&end_date=2025-08-17`

### Calendar View:
- **Calendar Grid**: API `/api/calendar.php?start_date=2025-08-01&end_date=2025-08-31`

### Notifications:
- **Notification List**: API `/api/notifications.php?user_id=UUID`
- **Unread Badge**: API returns `unread_count`

### Task Detail:
- **Issues Section**: API `/api/issues.php?task_id=1&project_id=UUID`

## 🔧 Status Values

Dựa theo design Figma:

- `done` - Done (xanh lá)
- `in_progress` - In Progress (vàng)
- `due` - Due (đỏ)
- `upcoming` - Upcoming (xám)
- `overdue` - Over due (đỏ đậm)
- `in_review` - In Review (tím)
- `reject` - Reject (đỏ)
- `in_comming` - In Comming (xanh nhạt)

## 📊 Database Schema

### Tables:
1. **calendar_events** - Lưu events và sync với tasks
2. **task_notifications** - Lưu thông báo cho users
3. **task_issues** - Lưu issues của tasks
4. **calendar_filters** - Lưu filter preferences (optional)

## 🔗 Tích hợp với Java Backend

Module này có thể hoạt động độc lập hoặc tích hợp với Java backend:

1. **Độc lập**: Frontend gọi trực tiếp PHP APIs
2. **Thông qua Java**: Java backend proxy requests tới PHP module

## 🐛 Troubleshooting

### Database Connection Error:
- Kiểm tra credentials trong `config/config.php`
- Đảm bảo MySQL đang chạy
- Kiểm tra database name đã tạo chưa

### CORS Issues:
- Headers đã được cấu hình trong mỗi API file
- Nếu vẫn lỗi, thêm domain cụ thể vào `allowed_origins`

### 404 Not Found:
- Kiểm tra đường dẫn file
- Đảm bảo web server đang chạy
- Kiểm tra `.htaccess` nếu dùng Apache

## 📝 License

MIT License - Tự do sử dụng cho dự án CloneJira
