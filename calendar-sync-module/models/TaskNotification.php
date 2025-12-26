<?php
/**
 * Task Notification Model
 */

require_once __DIR__ . '/../config/Database.php';

class TaskNotification {
    private $db;
    
    public function __construct() {
        $this->db = Database::getInstance();
    }
    
    /**
     * Get notifications for a user
     */
    public function getUserNotifications($uid, $limit = 50, $unreadOnly = false) {
        $sql = "SELECT * FROM task_notifications WHERE uid = ?";
        $params = [$uid];
        
        if ($unreadOnly) {
            $sql .= " AND is_read = FALSE";
        }
        
        $sql .= " ORDER BY created_at DESC LIMIT ?";
        $params[] = $limit;
        
        return $this->db->fetchAll($sql, $params);
    }
    
    /**
     * Create notification
     */
    public function createNotification($data) {
        $sql = "INSERT INTO task_notifications 
                (uid, proj_id, task_id, notification_type, title, message, notified_at)
                VALUES (?, ?, ?, ?, ?, ?, NOW())";
        
        $params = [
            $data['uid'],
            $data['proj_id'],
            $data['task_id'],
            $data['notification_type'],
            $data['title'],
            $data['message']
        ];
        
        $this->db->execute($sql, $params);
        return $this->db->lastInsertId();
    }
    
    /**
     * Mark notification as read
     */
    public function markAsRead($notificationId) {
        $sql = "UPDATE task_notifications SET is_read = TRUE WHERE id = ?";
        return $this->db->execute($sql, [$notificationId]);
    }
    
    /**
     * Mark all notifications as read for a user
     */
    public function markAllAsRead($uid) {
        $sql = "UPDATE task_notifications SET is_read = TRUE WHERE uid = ? AND is_read = FALSE";
        return $this->db->execute($sql, [$uid]);
    }
    
    /**
     * Get unread count
     */
    public function getUnreadCount($uid) {
        $sql = "SELECT COUNT(*) as count FROM task_notifications WHERE uid = ? AND is_read = FALSE";
        $result = $this->db->fetchOne($sql, [$uid]);
        return $result['count'] ?? 0;
    }
    
    /**
     * Delete notification
     */
    public function deleteNotification($id) {
        $sql = "DELETE FROM task_notifications WHERE id = ?";
        return $this->db->execute($sql, [$id]);
    }
}
