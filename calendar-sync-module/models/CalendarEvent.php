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
     * Note: This queries the tasks table directly and joins with calendar_events
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
                    ce.sync_status,
                    ce.calendar_notes,
                    ce.last_sync_at
                FROM tasks t
                LEFT JOIN calendar_events ce ON t.proj_id = ce.proj_id AND t.task_id = ce.task_id
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
                    ce.sync_status,
                    ce.calendar_notes
                FROM tasks t
                LEFT JOIN calendar_events ce ON t.proj_id = ce.proj_id AND t.task_id = ce.task_id
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
    public function getUpcomingTasks($days = 3, $uid = null) {
        $sql = "SELECT 
                    t.proj_id,
                    t.task_id,
                    t.task_name,
                    t.content,
                    t.startAt,
                    t.endAt,
                    t.status,
                    t.priority
                FROM tasks t
                WHERE t.status IN ('open', 'progress', 'reopen') 
                AND t.endAt BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL ? DAY)
                ORDER BY t.endAt ASC";
        
        return $this->db->fetchAll($sql, [$days]);
    }
    
    /**
     * Get overdue tasks
     * Tasks that are not done/close but past their end date
     */
    public function getOverdueTasks($uid = null) {
        $sql = "SELECT 
                    t.proj_id,
                    t.task_id,
                    t.task_name,
                    t.content,
                    t.startAt,
                    t.endAt,
                    t.status,
                    t.priority
                FROM tasks t
                WHERE t.status NOT IN ('done', 'close') 
                AND t.endAt < NOW()
                ORDER BY t.endAt ASC";
        
        return $this->db->fetchAll($sql);
    }
    
    /**
     * Create new calendar event (sync existing task to calendar)
     */
    public function createEvent($data) {
        $sql = "INSERT INTO calendar_events 
                (proj_id, task_id, sync_status, calendar_notes)
                VALUES (?, ?, ?, ?)";
        
        $params = [
            $data['proj_id'],
            $data['task_id'],
            $data['sync_status'] ?? 'synced',
            $data['calendar_notes'] ?? ''
        ];
        
        $this->db->execute($sql, $params);
        return $this->db->lastInsertId();
    }
    
    /**
     * Update calendar event
     */
    public function updateEvent($id, $data) {
        $fields = [];
        $params = [];
        
        foreach ($data as $key => $value) {
            $fields[] = "$key = ?";
            $params[] = $value;
        }
        
        $params[] = $id;
        
        $sql = "UPDATE calendar_events SET " . implode(', ', $fields) . " WHERE id = ?";
        
        return $this->db->execute($sql, $params);
    }
    
    /**
     * Delete calendar event
     */
    public function deleteEvent($id) {
        $sql = "DELETE FROM calendar_events WHERE id = ?";
        return $this->db->execute($sql, [$id]);
    }
    
    /**
     * Sync event status
     */
    public function syncEvent($id, $status = 'synced') {
        $sql = "UPDATE calendar_events SET sync_status = ?, last_sync_at = NOW() WHERE id = ?";
        return $this->db->execute($sql, [$status, $id]);
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
