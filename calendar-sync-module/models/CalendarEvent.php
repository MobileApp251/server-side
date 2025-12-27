<?php
/**
 * Calendar Event Model
 */

require_once __DIR__ . '/../config/Database.php';

class CalendarEvent {
    private $db;
    
    public function __construct() {
        $this->db = Database::getInstance();
    }
    
    /**
     * Get all calendar events with optional filters
     * Queries directly from tasks table - no additional tables needed
     */
    public function getEvents($filters = []) {
        $sql = "SELECT 
                    t.proj_id,
                    t.task_id,
                    t.task_name,
                    t.content,
                    t.startAt,
                    t.endAt,
                    t.status,
                    t.priority,
                    t.createAt,
                    t.updateAt,
                    p.proj_name
                FROM tasks t
                LEFT JOIN projects p ON t.proj_id = p.proj_id
                WHERE 1=1";
        $params = [];
        
        if (!empty($filters['status'])) {
            $placeholders = str_repeat('?,', count($filters['status']) - 1) . '?';
            $sql .= " AND t.status IN ($placeholders)";
            $params = array_merge($params, $filters['status']);
        }
        
        if (!empty($filters['start_date'])) {
            $sql .= " AND t.startAt >= ?";
            $params[] = $filters['start_date'];
        }
        
        if (!empty($filters['end_date'])) {
            $sql .= " AND t.endAt <= ?";
            $params[] = $filters['end_date'];
        }
        
        if (!empty($filters['proj_id'])) {
            $sql .= " AND t.proj_id = ?";
            $params[] = $filters['proj_id'];
        }
        
        if (!empty($filters['priority'])) {
            $placeholders = str_repeat('?,', count($filters['priority']) - 1) . '?';
            $sql .= " AND t.priority IN ($placeholders)";
            $params = array_merge($params, $filters['priority']);
        }
        
        $sql .= " ORDER BY t.startAt ASC";
        
        return $this->db->fetchAll($sql, $params);
    }
    
    /**
     * Get events by date range for calendar view
     * Queries directly from tasks table
     */
    public function getEventsByDateRange($startDate, $endDate, $projId = null) {
        $sql = "SELECT 
                    t.proj_id,
                    t.task_id,
                    t.task_name,
                    t.content,
                    t.startAt,
                    t.endAt,
                    t.status,
                    t.priority,
                    t.createAt,
                    t.updateAt,
                    p.proj_name
                FROM tasks t
                LEFT JOIN projects p ON t.proj_id = p.proj_id
                WHERE (t.startAt BETWEEN ? AND ?) 
                   OR (t.endAt BETWEEN ? AND ?)
                   OR (t.startAt <= ? AND t.endAt >= ?)";
        
        $params = [$startDate, $endDate, $startDate, $endDate, $startDate, $endDate];
        
        if ($projId) {
            $sql .= " AND t.proj_id = ?";
            $params[] = $projId;
        }
        
        $sql .= " ORDER BY t.startAt ASC";
        
        return $this->db->fetchAll($sql, $params);
    }
    
    /**
     * Get upcoming tasks (due soon)
     * Status: open, progress, reopen (not done or close)
     */
    public function getUpcomingTasks($days = 7, $uid = null, $projId = null) {
        $sql = "SELECT 
                    t.proj_id,
                    t.task_id,
                    t.task_name,
                    t.content,
                    t.startAt,
                    t.endAt,
                    t.status,
                    t.priority,
                    p.proj_name,
                    DATEDIFF(t.endAt, NOW()) as days_remaining
                FROM tasks t
                LEFT JOIN projects p ON t.proj_id = p.proj_id
                WHERE t.status IN ('open', 'progress', 'reopen') 
                AND t.endAt BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL ? DAY)";
        
        $params = [$days];
        
        if ($projId) {
            $sql .= " AND t.proj_id = ?";
            $params[] = $projId;
        }
        
        if ($uid) {
            $sql .= " AND EXISTS (SELECT 1 FROM task_assignees ta WHERE ta.proj_id = t.proj_id AND ta.task_id = t.task_id AND ta.uid = ?)";
            $params[] = $uid;
        }
        
        $sql .= " ORDER BY t.endAt ASC";
        
        return $this->db->fetchAll($sql, $params);
    }
    
    /**
     * Get overdue tasks
     * Tasks that are not done/close but past their end date
     */
    public function getOverdueTasks($uid = null, $projId = null) {
        $sql = "SELECT 
                    t.proj_id,
                    t.task_id,
                    t.task_name,
                    t.content,
                    t.startAt,
                    t.endAt,
                    t.status,
                    t.priority,
                    p.proj_name,
                    DATEDIFF(NOW(), t.endAt) as days_overdue
                FROM tasks t
                LEFT JOIN projects p ON t.proj_id = p.proj_id
                WHERE t.status NOT IN ('done', 'close') 
                AND t.endAt < NOW()";
        
        $params = [];
        
        if ($projId) {
            $sql .= " AND t.proj_id = ?";
            $params[] = $projId;
        }
        
        if ($uid) {
            $sql .= " AND EXISTS (SELECT 1 FROM task_assignees ta WHERE ta.proj_id = t.proj_id AND ta.task_id = t.task_id AND ta.uid = ?)";
            $params[] = $uid;
        }
        
        $sql .= " ORDER BY t.endAt ASC";
        
        return $this->db->fetchAll($sql, $params);
    }
    
    /**
     * Get tasks by assignee (user)
     */
    public function getTasksByUser($uid, $projId = null) {
        $sql = "SELECT 
                    t.proj_id,
                    t.task_id,
                    t.task_name,
                    t.content,
                    t.startAt,
                    t.endAt,
                    t.status,
                    t.priority,
                    p.proj_name
                FROM tasks t
                INNER JOIN task_assignees ta ON t.proj_id = ta.proj_id AND t.task_id = ta.task_id
                LEFT JOIN projects p ON t.proj_id = p.proj_id
                WHERE ta.uid = ?";
        
        $params = [$uid];
        
        if ($projId) {
            $sql .= " AND t.proj_id = ?";
            $params[] = $projId;
        }
        
        $sql .= " ORDER BY t.endAt ASC";
        
        return $this->db->fetchAll($sql, $params);
    }
    
    /**
     * Get tasks by project with assignees
     */
    public function getTasksByProject($projId) {
        $sql = "SELECT 
                    t.proj_id,
                    t.task_id,
                    t.task_name,
                    t.content,
                    t.startAt,
                    t.endAt,
                    t.status,
                    t.priority,
                    t.createAt,
                    t.updateAt,
                    p.proj_name,
                    GROUP_CONCAT(DISTINCT u.fullname SEPARATOR ', ') as assignees,
                    GROUP_CONCAT(DISTINCT u.uid SEPARATOR ',') as assignee_ids
                FROM tasks t
                LEFT JOIN projects p ON t.proj_id = p.proj_id
                LEFT JOIN task_assignees ta ON t.proj_id = ta.proj_id AND t.task_id = ta.task_id
                LEFT JOIN users u ON ta.uid = u.uid
                WHERE t.proj_id = ?
                GROUP BY t.proj_id, t.task_id
                ORDER BY t.startAt ASC";
        
        return $this->db->fetchAll($sql, [$projId]);
    }
    
    /**
     * Get event statistics
     */
    public function getStatistics($projId = null) {
        $sql = "SELECT 
                    COUNT(*) as total,
                    SUM(CASE WHEN t.status = 'done' THEN 1 ELSE 0 END) as completed,
                    SUM(CASE WHEN t.status = 'progress' THEN 1 ELSE 0 END) as in_progress,
                    SUM(CASE WHEN t.status = 'open' THEN 1 ELSE 0 END) as open,
                    SUM(CASE WHEN t.status = 'close' THEN 1 ELSE 0 END) as closed,
                    SUM(CASE WHEN t.status = 'reopen' THEN 1 ELSE 0 END) as reopened,
                    SUM(CASE WHEN t.status NOT IN ('done', 'close') AND t.endAt < NOW() THEN 1 ELSE 0 END) as overdue
                FROM tasks t";
        
        if ($projId) {
            $sql .= " WHERE t.proj_id = ?";
            return $this->db->fetchOne($sql, [$projId]);
        }
        
        return $this->db->fetchOne($sql);
    }
}
