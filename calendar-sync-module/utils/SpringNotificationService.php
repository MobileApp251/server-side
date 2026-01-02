<?php
/**
 * Spring Notification Service
 * Service to interact with Spring Boot notification endpoints
 */

require_once __DIR__ . '/SpringApiClient.php';

class SpringNotificationService {
    private $client;
    
    public function __construct($springApiUrl = null) {
        $this->client = new SpringApiClient($springApiUrl);
    }
    
    /**
     * Set authentication token for API calls
     * @param string $token JWT token
     */
    public function setAuthToken($token) {
        $this->client->setAuthToken($token);
    }
    
    /**
     * Get notifications for a user
     * @param string $userId User ID
     * @param bool $unreadOnly Get only unread notifications
     * @param int $limit Number of notifications to retrieve
     * @return array Notifications
     */
    public function getUserNotifications($userId, $unreadOnly = false, $limit = 50) {
        try {
            $params = [
                'userId' => $userId,
                'unreadOnly' => $unreadOnly ? 'true' : 'false',
                'limit' => $limit
            ];
            
            $response = $this->client->get('/api/notifications', $params);
            return $response['data'];
        } catch (Exception $e) {
            error_log("Error fetching notifications from Spring: " . $e->getMessage());
            return null;
        }
    }
    
    /**
     * Create a new notification
     * @param array $notificationData Notification data
     * @return array Created notification
     */
    public function createNotification($notificationData) {
        try {
            $response = $this->client->post('/api/notifications', $notificationData);
            return $response['data'];
        } catch (Exception $e) {
            error_log("Error creating notification in Spring: " . $e->getMessage());
            return null;
        }
    }
    
    /**
     * Send task assignment notification
     * @param string $userId User being assigned
     * @param int $taskId Task ID
     * @param string $taskTitle Task title
     * @param string $projectId Project ID
     * @return array Notification result
     */
    public function sendTaskAssignmentNotification($userId, $taskId, $taskTitle, $projectId) {
        $data = [
            'userId' => $userId,
            'taskId' => $taskId,
            'type' => 'TASK_ASSIGNED',
            'title' => 'New Task Assigned',
            'message' => "You have been assigned to task: {$taskTitle}",
            'projectId' => $projectId,
            'priority' => 'NORMAL'
        ];
        
        return $this->createNotification($data);
    }
    
    /**
     * Send task due soon notification
     * @param string $userId User ID
     * @param int $taskId Task ID
     * @param string $taskTitle Task title
     * @param string $dueDate Due date
     * @return array Notification result
     */
    public function sendTaskDueSoonNotification($userId, $taskId, $taskTitle, $dueDate) {
        $data = [
            'userId' => $userId,
            'taskId' => $taskId,
            'type' => 'TASK_DUE_SOON',
            'title' => 'Task Due Soon',
            'message' => "Task '{$taskTitle}' is due on {$dueDate}",
            'priority' => 'HIGH'
        ];
        
        return $this->createNotification($data);
    }
    
    /**
     * Send task overdue notification
     * @param string $userId User ID
     * @param int $taskId Task ID
     * @param string $taskTitle Task title
     * @return array Notification result
     */
    public function sendTaskOverdueNotification($userId, $taskId, $taskTitle) {
        $data = [
            'userId' => $userId,
            'taskId' => $taskId,
            'type' => 'TASK_OVERDUE',
            'title' => 'Task Overdue',
            'message' => "Task '{$taskTitle}' is overdue!",
            'priority' => 'URGENT'
        ];
        
        return $this->createNotification($data);
    }
    
    /**
     * Mark notification as read
     * @param int $notificationId Notification ID
     * @return bool Success status
     */
    public function markAsRead($notificationId) {
        try {
            $response = $this->client->put("/api/notifications/{$notificationId}/read");
            return $response['success'];
        } catch (Exception $e) {
            error_log("Error marking notification as read: " . $e->getMessage());
            return false;
        }
    }
    
    /**
     * Mark all notifications as read for a user
     * @param string $userId User ID
     * @return bool Success status
     */
    public function markAllAsRead($userId) {
        try {
            $response = $this->client->put("/api/notifications/user/{$userId}/read-all");
            return $response['success'];
        } catch (Exception $e) {
            error_log("Error marking all notifications as read: " . $e->getMessage());
            return false;
        }
    }
    
    /**
     * Get unread notification count
     * @param string $userId User ID
     * @return int Unread count
     */
    public function getUnreadCount($userId) {
        try {
            $response = $this->client->get("/api/notifications/user/{$userId}/unread-count");
            return $response['data']['count'] ?? 0;
        } catch (Exception $e) {
            error_log("Error getting unread count: " . $e->getMessage());
            return 0;
        }
    }
    
    /**
     * Delete notification
     * @param int $notificationId Notification ID
     * @return bool Success status
     */
    public function deleteNotification($notificationId) {
        try {
            $response = $this->client->delete("/api/notifications/{$notificationId}");
            return $response['success'];
        } catch (Exception $e) {
            error_log("Error deleting notification: " . $e->getMessage());
            return false;
        }
    }
    
    /**
     * Check if Spring notification service is available
     * @return bool True if service is up
     */
    public function isAvailable() {
        return $this->client->healthCheck();
    }
}
