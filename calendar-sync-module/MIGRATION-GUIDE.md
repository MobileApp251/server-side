# Calendar Sync Module - MIGRATION GUIDE

## 🔄 Đồng bộ với Java Application

Module PHP này đã được điều chỉnh để hoàn toàn đồng bộ với dự án Java Spring Boot hiện tại.

### ✅ Những gì đã đồng bộ:

#### 1. **Database Connection**
- ✅ Sử dụng cùng database Railway: `ballast.proxy.rlwy.net:26781`
- ✅ Database name: `railway`
- ✅ Credentials đã được cấu hình trong `config/config.php`

#### 2. **Table Structure**
- ✅ **calendar_events**: Link trực tiếp với `tasks` table qua composite FK (proj_id, task_id)
- ✅ **task_notifications**: Sử dụng `uid` thay vì `user_id`, foreign keys đến `users.uid`
- ✅ **task_issues**: Sử dụng `proj_id` và `task_id` đồng bộ với Java
- ✅ **calendar_filters**: Sử dụng `uid` cho user references

#### 3. **Column Names**
- ✅ `proj_id` (UUID VARCHAR(36)) - đồng bộ với `projects.proj_id`
- ✅ `task_id` (INT) - đồng bộ với `tasks.task_id`
- ✅ `uid` (UUID VARCHAR(36)) - đồng bộ với `users.uid`
- ✅ `startAt`, `endAt` (TIMESTAMP) - đồng bộ với Java Instant fields
- ✅ `createAt`, `updateAt` - đồng bộ với Java audit fields

#### 4. **Enum Values**
Java TaskStatus enum values:
```java
OPEN("open")
PROGRESS("progress")
DONE("done")
REOPEN("reopen")
CLOSE("close")
```

Java TaskPriority enum values:
```java
HIGH("high")
MEDIUM("medium")
LOW("low")
```

✅ **Đã áp dụng trong PHP**: Các query filter theo đúng giá trị này

#### 5. **API Changes**
Tất cả APIs đã được cập nhật:
- ✅ `project_id` → `proj_id`
- ✅ `user_id` → `uid`
- ✅ Query trực tiếp từ `tasks` table thay vì `calendar_events`
- ✅ JOIN với `calendar_events` để lấy sync status

### 📋 Cách sử dụng:

#### Bước 1: Import Database Schema
```sql
-- Chạy trong MySQL Workbench hoặc phpMyAdmin
-- Kết nối đến: ballast.proxy.rlwy.net:26781
-- Database: railway

SOURCE /path/to/calendar-sync-module/database/schema.sql;
```

#### Bước 2: Verify Configuration
File `config/config.php` đã có sẵn cấu hình Railway database:
```php
'host' => 'ballast.proxy.rlwy.net',
'port' => '26781',
'dbname' => 'railway',
```

#### Bước 3: Test Connection
```bash
cd calendar-sync-module
php -S localhost:8000

# Test
curl http://localhost:8000/api/events.php
```

### 🔗 Integration với Java Backend

#### Option 1: Direct PHP API Calls (Recommended)
Frontend có thể gọi trực tiếp PHP APIs:
```javascript
// Get calendar events
fetch('http://localhost:8000/api/events.php?status=open,progress&proj_id=UUID')
  .then(res => res.json())
  .then(data => console.log(data));
```

#### Option 2: Java Proxy (Advanced)
Tạo một Controller trong Java để proxy requests đến PHP:
```java
@RestController
@RequestMapping("/api/calendar")
public class CalendarProxyController {
    @GetMapping("/events")
    public ResponseEntity<?> getEvents(@RequestParam Map<String, String> params) {
        // Forward to PHP API
        String phpUrl = "http://localhost:8000/api/events.php?" + buildQueryString(params);
        // ... HTTP client call
    }
}
```

### 📊 Data Flow

```
Frontend
   ↓
   ├─→ Java API (CRUD Tasks)  ──→  MySQL Railway (tasks table)
   │                                    ↑
   └─→ PHP API (Calendar Sync) ────────┘
        - Notifications
        - Calendar View
        - Issues
```

### ⚠️ Important Notes

1. **Task Management**: Tasks vẫn được quản lý bởi Java application
2. **Calendar Sync**: PHP module chỉ thêm calendar sync metadata và notifications
3. **Status Updates**: Task status chỉ được update bởi Java, không được update từ PHP
4. **Foreign Keys**: Schema có đầy đủ foreign keys, đảm bảo referential integrity

### 🔍 API Examples với Java data

```bash
# Get tasks from Java database với calendar sync status
curl "http://localhost:8000/api/events.php?status=open,progress"

# Response sẽ bao gồm:
{
  "success": true,
  "data": [
    {
      "proj_id": "uuid-from-java",
      "task_id": 1,
      "task_name": "Mobile App",
      "content": "Develop mobile app",
      "startAt": "2025-08-01 00:00:00",
      "endAt": "2025-08-17 00:00:00",
      "status": "progress",  // From Java enum
      "priority": "high",     // From Java enum
      "sync_status": "synced",
      "calendar_notes": "Calendar notes here"
    }
  ]
}
```

### 🛠️ Development Workflow

1. **Java Team**: Quản lý tasks CRUD trong Java application
2. **PHP Module**: Xử lý calendar sync, notifications, issues
3. **Frontend**: Integrate cả 2 APIs

### 📝 Migration Checklist

- [x] Database schema aligned with Java entities
- [x] Column names match Java fields (proj_id, task_id, uid)
- [x] Enum values match Java enums
- [x] Foreign keys reference Java tables
- [x] API parameters use Java naming conventions
- [x] Queries join with tasks table instead of duplicating data
- [x] Railway database credentials configured
- [x] Timestamp fields match Java Instant type

## ✨ Benefits

1. **Single Source of Truth**: Tasks data từ Java, PHP chỉ bổ sung metadata
2. **Data Consistency**: Foreign keys đảm bảo referential integrity
3. **Real-time**: PHP queries trực tiếp từ tasks table, luôn up-to-date
4. **Scalable**: Dễ dàng mở rộng thêm calendar features mà không ảnh hưởng Java
