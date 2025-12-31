<?php
/**
 * DEMO API: User Notifications (with mock data)
 * This is a demo version that works without database connection
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once __DIR__ . '/../utils/Response.php';

try {
    // GET: Get notifications
    if ($_SERVER['REQUEST_METHOD'] === 'GET') {
        if (!isset($_GET['uid'])) {
            Response::error('uid is required', 400);
            exit();
        }
        
        $uid = $_GET['uid'];
        $limit = isset($_GET['limit']) ? (int)$_GET['limit'] : 50;
        $unreadOnly = isset($_GET['unread_only']) && $_GET['unread_only'] === 'true';
        
        $mockNotifications = [
            [
                'id' => 1,
                'uid' => $uid,
                'task_id' => 101,
                'notification_type' => 'task_due_soon',
                'message' => 'Task "Complete API Documentation" is due in 2 days',
                'is_read' => false,
                'created_at' => '2025-01-13T10:00:00Z'
            ],
            [
                'id' => 2,
                'uid' => $uid,
                'task_id' => 102,
                'notification_type' => 'issue_created',
                'message' => 'New issue created: "Authentication fails with special characters"',
                'is_read' => false,
                'created_at' => '2025-01-14T10:30:00Z'
            ],
            [
                'id' => 3,
                'uid' => $uid,
                'task_id' => 105,
                'notification_type' => 'task_assigned',
                'message' => 'You have been assigned to task "Unit Tests Implementation"',
                'is_read' => true,
                'created_at' => '2025-01-08T08:00:00Z'
            ],
            [
                'id' => 4,
                'uid' => $uid,
                'task_id' => 102,
                'notification_type' => 'task_overdue',
                'message' => 'Task "Fix Authentication Bug" is overdue',
                'is_read' => false,
                'created_at' => '2025-01-17T09:00:00Z'
            ]
        ];
        
        if ($unreadOnly) {
            $mockNotifications = array_filter($mockNotifications, function($n) {
                return !$n['is_read'];
            });
        }
        
        $mockNotifications = array_slice(array_values($mockNotifications), 0, $limit);
        $unreadCount = count(array_filter($mockNotifications, function($n) {
            return !$n['is_read'];
        }));
        
        Response::success([
            'notifications' => $mockNotifications,
            'unread_count' => $unreadCount,
            'total' => count($mockNotifications),
            '_note' => 'This is DEMO data'
        ]);
    }
    
    // POST: Create notification
    elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $data = json_decode(file_get_contents('php://input'), true);
        
        if (!isset($data['uid']) || !isset($data['task_id']) || !isset($data['notification_type'])) {
            Response::error('Missing required fields: uid, task_id, notification_type', 400);
            exit();
        }
        
        Response::success([
            'id' => rand(100, 999),
            'message' => 'Notification created successfully (DEMO)',
            '_note' => 'This is DEMO data'
        ], 201);
    }
    
    // PUT: Mark as read
    elseif ($_SERVER['REQUEST_METHOD'] === 'PUT') {
        $data = json_decode(file_get_contents('php://input'), true);
        
        if (isset($data['notification_id'])) {
            Response::success([
                'message' => 'Notification marked as read (DEMO)',
                'notification_id' => $data['notification_id'],
                '_note' => 'This is DEMO data'
            ]);
        } elseif (isset($data['uid'])) {
            Response::success([
                'message' => 'All notifications marked as read (DEMO)',
                'uid' => $data['uid'],
                '_note' => 'This is DEMO data'
            ]);
        } else {
            Response::error('notification_id or uid is required', 400);
        }
    }
    
} catch (Exception $e) {
    Response::error($e->getMessage(), 500);
}
