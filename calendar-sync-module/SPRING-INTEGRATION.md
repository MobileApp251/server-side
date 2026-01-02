# 🔗 Tích hợp PHP với Spring Boot Backend

## 📋 Tổng quan

Module PHP calendar-sync có thể sử dụng các tài nguyên (API) của Spring Boot backend thông qua HTTP REST API.

## 🏗️ Kiến trúc

```
┌─────────────────┐      HTTP/REST      ┌──────────────────┐
│   PHP Module    │ ◄─────────────────► │  Spring Boot API │
│  (Calendar)     │                     │  (Notifications)  │
└─────────────────┘                     └──────────────────┘
        │                                         │
        ▼                                         ▼
  ┌──────────┐                            ┌──────────┐
  │ MySQL DB │                            │  MySQL   │
  │ (Tasks)  │                            │  (Users) │
  └──────────┘                            └──────────┘
```

## 📦 Components đã tạo

### 1. SpringApiClient.php
HTTP client tổng quát để gọi bất kỳ Spring Boot API nào

**Location:** `utils/SpringApiClient.php`

**Features:**
- GET, POST, PUT, DELETE requests
- Authentication header support
- Error handling
- Health check

### 2. SpringNotificationService.php
Service chuyên biệt cho notification endpoints

**Location:** `utils/SpringNotificationService.php`

**Features:**
- Get user notifications
- Create notifications
- Mark as read
- Send task notifications (assigned, due soon, overdue)
- Unread count

### 3. Example Integration
File mẫu hướng dẫn sử dụng

**Location:** `examples/spring-notification-integration.php`

## 🚀 Cách sử dụng

### Bước 1: Cấu hình Spring API URL

Thêm vào `.env` hoặc config:
```env
SPRING_API_URL=http://localhost:8082
```

Hoặc truyền trực tiếp khi khởi tạo:
```php
$service = new SpringNotificationService('http://localhost:8082');
```

### Bước 2: Sử dụng trong code PHP

#### A. Lấy notifications từ Spring

```php
require_once __DIR__ . '/../utils/SpringNotificationService.php';

$notificationService = new SpringNotificationService();

// Get notifications
$userId = "550e8400-e29b-41d4-a716-446655440000";
$notifications = $notificationService->getUserNotifications($userId);

if ($notifications) {
    foreach ($notifications as $notif) {
        echo $notif['title'] . ": " . $notif['message'] . "\n";
    }
}
```

#### B. Gửi notification khi tạo task

```php
// When creating a task in PHP
$taskId = 123;
$assignedUserId = "user-uuid";
$taskTitle = "Complete documentation";
$projectId = "project-uuid";

// Send notification via Spring
$notificationService->sendTaskAssignmentNotification(
    $assignedUserId,
    $taskId,
    $taskTitle,
    $projectId
);
```

#### C. Kiểm tra unread count

```php
$unreadCount = $notificationService->getUnreadCount($userId);
echo "You have {$unreadCount} unread notifications";
```

#### D. Mark as read

```php
$notificationId = 123;
$notificationService->markAsRead($notificationId);
```

### Bước 3: Tích hợp vào API endpoints

#### Ví dụ: notifications.php

```php
<?php
require_once __DIR__ . '/../utils/Response.php';
require_once __DIR__ . '/../utils/SpringNotificationService.php';

$springService = new SpringNotificationService();

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $uid = $_GET['uid'] ?? null;
    
    if (!$uid) {
        Response::error('uid is required', 400);
        exit();
    }
    
    // Get notifications from Spring Boot
    $notifications = $springService->getUserNotifications($uid);
    $unreadCount = $springService->getUnreadCount($uid);
    
    Response::success([
        'notifications' => $notifications,
        'unread_count' => $unreadCount,
        'source' => 'spring-boot'
    ]);
}
```

## 📝 API Endpoints cần có ở Spring Boot

PHP module cần Spring Boot có các endpoints sau:

### 1. Get Notifications
```
GET /api/notifications?userId={userId}&unreadOnly={true/false}&limit={limit}
```

### 2. Create Notification
```
POST /api/notifications
Body: {
  "userId": "string",
  "taskId": "number",
  "type": "TASK_ASSIGNED|TASK_DUE_SOON|TASK_OVERDUE",
  "title": "string",
  "message": "string",
  "priority": "NORMAL|HIGH|URGENT"
}
```

### 3. Mark as Read
```
PUT /api/notifications/{notificationId}/read
```

### 4. Mark All as Read
```
PUT /api/notifications/user/{userId}/read-all
```

### 5. Get Unread Count
```
GET /api/notifications/user/{userId}/unread-count
Response: { "count": number }
```

### 6. Delete Notification
```
DELETE /api/notifications/{notificationId}
```

### 7. Health Check
```
GET /actuator/health
```

## 🔐 Authentication

Nếu Spring Boot yêu cầu authentication:

```php
$service = new SpringNotificationService();

// Set JWT token
$token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
$service->setAuthToken($token);

// Now all requests will include Authorization header
$notifications = $service->getUserNotifications($userId);
```

## 🧪 Testing

### Test từ command line:

```bash
cd calendar-sync-module
php examples/spring-notification-integration.php
```

### Test trong browser:

Tạo file `test-spring-integration.php` trong `api/`:

```php
<?php
require_once __DIR__ . '/../utils/SpringNotificationService.php';

header('Content-Type: application/json');

$service = new SpringNotificationService();

// Health check
$health = $service->isAvailable();

Response::success([
    'spring_service_available' => $health,
    'spring_api_url' => 'http://localhost:8082'
]);
```

Truy cập: `http://localhost:8080/api/test-spring-integration.php`

## 🔧 Troubleshooting

### Lỗi: Connection refused
**Nguyên nhân:** Spring Boot chưa chạy hoặc sai port  
**Giải pháp:** 
```bash
# Check Spring Boot đang chạy
curl http://localhost:8082/actuator/health

# Hoặc kiểm tra port
netstat -ano | findstr :8082
```

### Lỗi: CORS
**Nguyên nhân:** Spring Boot chặn CORS  
**Giải pháp:** Thêm CORS config trong Spring:
```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("*")
                    .allowedMethods("*");
            }
        };
    }
}
```

### Lỗi: 401 Unauthorized
**Nguyên nhân:** Thiếu hoặc sai authentication token  
**Giải pháp:** Set token trước khi gọi API
```php
$service->setAuthToken($validToken);
```

## 🎯 Use Cases

### 1. Sync notifications khi tạo task

```php
// In CalendarEvent model
public function createTask($taskData) {
    // Save to PHP database
    $taskId = $this->saveToDatabase($taskData);
    
    // Send notification via Spring
    $notificationService = new SpringNotificationService();
    $notificationService->sendTaskAssignmentNotification(
        $taskData['assigned_to'],
        $taskId,
        $taskData['title'],
        $taskData['proj_id']
    );
    
    return $taskId;
}
```

### 2. Scheduled notifications (cron job)

```php
// File: cron/check-due-tasks.php
require_once __DIR__ . '/../utils/SpringNotificationService.php';
require_once __DIR__ . '/../models/CalendarEvent.php';

$eventModel = new CalendarEvent();
$notificationService = new SpringNotificationService();

// Get tasks due in 2 days
$dueSoonTasks = $eventModel->getTasksDueInDays(2);

foreach ($dueSoonTasks as $task) {
    $notificationService->sendTaskDueSoonNotification(
        $task['assigned_to'],
        $task['task_id'],
        $task['title'],
        $task['end_date']
    );
}
```

### 3. Fallback mechanism

```php
// Try Spring first, fallback to PHP notification
$springService = new SpringNotificationService();

if ($springService->isAvailable()) {
    // Use Spring notification
    $result = $springService->sendTaskAssignmentNotification(...);
} else {
    // Fallback to PHP notification table
    $phpNotificationModel = new TaskNotification();
    $phpNotificationModel->createNotification(...);
}
```

## 📚 Tài liệu tham khảo

- `utils/SpringApiClient.php` - Generic HTTP client
- `utils/SpringNotificationService.php` - Notification service
- `examples/spring-notification-integration.php` - Usage examples

## 🚀 Next Steps

1. **Setup Spring Boot:** Đảm bảo Spring Boot API đang chạy
2. **Configure URL:** Set `SPRING_API_URL` in config
3. **Test connection:** Run `php examples/spring-notification-integration.php`
4. **Integrate:** Use service trong PHP APIs
5. **Deploy:** Configure production URLs

---

**Note:** PHP module có thể hoạt động độc lập (với PHP notification table) hoặc tích hợp với Spring Boot để sử dụng notification system của Spring.
