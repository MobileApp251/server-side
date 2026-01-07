# Google Calendar Sync - Setup Guide

## 🔧 Thiết lập Google Calendar API

### Bước 1: Tạo Google Cloud Project

1. Truy cập [Google Cloud Console](https://console.cloud.google.com/)
2. Tạo project mới hoặc chọn project có sẵn
3. Đảm bảo billing đã được kích hoạt (nếu cần)

### Bước 2: Kích hoạt Google Calendar API

1. Vào **APIs & Services** > **Library**
2. Tìm kiếm "Google Calendar API"
3. Click **Enable**

### Bước 3: Tạo OAuth 2.0 Credentials

1. Vào **APIs & Services** > **Credentials**
2. Click **Create Credentials** > **OAuth client ID**
3. Chọn application type: **Desktop app**
4. Đặt tên: "Calendar Sync Module"
5. Click **Create**
6. Download file credentials JSON

### Bước 4: Cấu hình Credentials

1. Đổi tên file đã tải về thành `credentials.json`
2. Di chuyển vào thư mục: `calendar-sync-module/config/credentials.json`
3. Hoặc copy từ file mẫu:
   ```bash
   cp config/credentials.example.json config/credentials.json
   ```
4. Thay thế các giá trị YOUR_CLIENT_ID, YOUR_CLIENT_SECRET, etc.

### Bước 5: Xác thực ứng dụng

1. Khởi động server PHP:
   ```bash
   cd calendar-sync-module
   php -S localhost:8080
   ```

2. Kiểm tra trạng thái:
   ```bash
   curl "http://localhost:8080/api/google-sync.php?action=status"
   ```

3. Lấy URL xác thực:
   ```bash
   curl "http://localhost:8080/api/google-sync.php?action=auth"
   ```

4. Truy cập URL xác thực trong trình duyệt
5. Đăng nhập Google và cho phép quyền truy cập
6. Copy authorization code từ URL redirect
7. Thực hiện callback:
   ```bash
   curl "http://localhost:8080/api/google-sync.php?action=callback&code=YOUR_AUTH_CODE"
   ```

### Bước 6: Test Sync

```bash
# Sync tasks trong khoảng thời gian
curl "http://localhost:8080/api/google-sync.php?action=sync&start_date=2026-01-01&end_date=2026-01-31"

# Sync một task cụ thể
curl "http://localhost:8080/api/google-sync.php?action=sync_task&task_id=123&proj_id=abc"

# Liệt kê events trên Google Calendar
curl "http://localhost:8080/api/google-sync.php?action=list&start_date=2026-01-01&end_date=2026-01-31"
```

## 📝 Cấu trúc Files

```
calendar-sync-module/
├── config/
│   ├── credentials.json         # OAuth credentials (không commit vào git)
│   ├── credentials.example.json # File mẫu
│   └── token.json              # Access token (tự động tạo sau khi auth)
├── utils/
│   └── GoogleCalendarSync.php  # Service class
└── api/
    └── google-sync.php         # API endpoint
```

## 🔒 Bảo mật

**QUAN TRỌNG:** Thêm vào `.gitignore`:

```
config/credentials.json
config/token.json
```

Không bao giờ commit các file này vào git!

## 🎨 Màu sắc Priority trên Google Calendar

- **Low**: Xanh lá (Green)
- **Medium**: Vàng (Yellow)
- **High**: Cam (Orange)
- **Critical**: Đỏ (Red)

## 📊 API Endpoints

### 1. Setup Instructions
```
GET /api/google-sync.php?action=setup
```

### 2. Get Authorization URL
```
GET /api/google-sync.php?action=auth
```

### 3. OAuth Callback
```
GET /api/google-sync.php?action=callback&code=AUTH_CODE
```

### 4. Sync Date Range
```
GET /api/google-sync.php?action=sync&start_date=Y-m-d&end_date=Y-m-d&proj_id=X
```

### 5. Sync Single Task
```
GET /api/google-sync.php?action=sync_task&task_id=X&proj_id=Y
```

### 6. List Google Events
```
GET /api/google-sync.php?action=list&start_date=Y-m-d&end_date=Y-m-d
```

### 7. Check Status
```
GET /api/google-sync.php?action=status
```

## 🐛 Troubleshooting

### Lỗi: "Credentials file not found"
- Kiểm tra file `config/credentials.json` có tồn tại
- Đảm bảo đúng đường dẫn

### Lỗi: "Not authorized"
- Chạy `?action=auth` để lấy URL xác thực
- Hoàn tất OAuth flow

### Lỗi: "Token expired"
- Token sẽ tự động refresh
- Nếu không thành công, xóa `config/token.json` và auth lại

### Lỗi: "Calendar API has not been used"
- Enable Google Calendar API trong Cloud Console
- Chờ vài phút để API được kích hoạt

## 📚 Tài liệu tham khảo

- [Google Calendar API Documentation](https://developers.google.com/calendar/api/guides/overview)
- [Google API PHP Client](https://github.com/googleapis/google-api-php-client)
- [OAuth 2.0 for Desktop Apps](https://developers.google.com/identity/protocols/oauth2/native-app)
