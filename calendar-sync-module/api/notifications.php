<?php
/**
 * API: User Notifications
 * Endpoint: GET /api/notifications.php
 * 
 * Query Parameters:
 * - uid: User UUID (required)
 * - limit: number (default: 50)
 * - unread_only: boolean (default: false)
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once __DIR__ . '/../models/TaskNotification.php';
require_once __DIR__ . '/../utils/Response.php';

try {
    $notificationModel = new TaskNotification();
    
    // GET: Get notifications
    if ($_SERVER['REQUEST_METHOD'] === 'GET') {
        if (!isset($_GET['uid'])) {
            Response::error('uid is required', 400);
            exit();
        }
        
        $uid = $_GET['uid'];
        $limit = isset($_GET['limit']) ? (int)$_GET['limit'] : 50;
        $unreadOnly = isset($_GET['unread_only']) && $_GET['unread_only'] === 'true';
        
        $notifications = $notificationModel->getUserNotifications($uid, $limit, $unreadOnly);
        $unreadCount = $notificationModel->getUnreadCount($uid);
        
        Response::success([
            'notifications' => $notifications,
            'unread_count' => $unreadCount
        ]);
    }
    
    // POST: Create notification
    elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $data = json_decode(file_get_contents('php://input'), true);
        
        if (!isset($data['uid']) || !isset($data['task_id']) || !isset($data['notification_type'])) {
            Response::error('Missing required fields', 400);
            exit();
        }
        
        $notificationId = $notificationModel->createNotification($data);
        
        Response::success(['id' => $notificationId], 201);
    }
    
    // PUT: Mark as read
    elseif ($_SERVER['REQUEST_METHOD'] === 'PUT') {
        $data = json_decode(file_get_contents('php://input'), true);
        
        if (isset($data['notification_id'])) {
            $notificationModel->markAsRead($data['notification_id']);
            Response::success(['message' => 'Notification marked as read']);
        } elseif (isset($data['uid'])) {
            $notificationModel->markAllAsRead($data['uid']);
            Response::success(['message' => 'All notifications marked as read']);
        } else {
            Response::error('notification_id or uid is required', 400);
        }
    }
    
} catch (Exception $e) {
    Response::error($e->getMessage(), 500);
}
