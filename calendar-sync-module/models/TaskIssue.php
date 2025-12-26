<?php
/**
 * Task Issue Model
 */

require_once __DIR__ . '/../config/Database.php';

class TaskIssue {
    private $db;
    
    public function __construct() {
        $this->db = Database::getInstance();
    }
    
    /**
     * Get issues for a task
     */
    public function getTaskIssues($taskId, $projId) {
        $sql = "SELECT * FROM task_issues WHERE task_id = ? AND proj_id = ? ORDER BY created_at DESC";
        return $this->db->fetchAll($sql, [$taskId, $projId]);
    }
    
    /**
     * Create new issue
     */
    public function createIssue($data) {
        $sql = "INSERT INTO task_issues 
                (proj_id, task_id, issue_title, issue_content, status, priority, created_by, assigned_to)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        $params = [
            $data['proj_id'],
            $data['task_id'],
            $data['issue_title'],
            $data['issue_content'] ?? '',
            $data['status'] ?? 'open',
            $data['priority'] ?? 'medium',
            $data['created_by'],
            $data['assigned_to'] ?? null
        ];
        
        $this->db->execute($sql, $params);
        return $this->db->lastInsertId();
    }
    
    /**
     * Update issue
     */
    public function updateIssue($id, $data) {
        $fields = [];
        $params = [];
        
        foreach ($data as $key => $value) {
            $fields[] = "$key = ?";
            $params[] = $value;
        }
        
        $params[] = $id;
        
        $sql = "UPDATE task_issues SET " . implode(', ', $fields) . " WHERE id = ?";
        
        return $this->db->execute($sql, $params);
    }
    
    /**
     * Delete issue
     */
    public function deleteIssue($id) {
        $sql = "DELETE FROM task_issues WHERE id = ?";
        return $this->db->execute($sql, [$id]);
    }
    
    /**
     * Get issue statistics
     */
    public function getIssueStatistics($projId = null) {
        $sql = "SELECT 
                    COUNT(*) as total,
                    SUM(CASE WHEN status = 'open' THEN 1 ELSE 0 END) as open,
                    SUM(CASE WHEN status = 'in_progress' THEN 1 ELSE 0 END) as in_progress,
                    SUM(CASE WHEN status = 'resolved' THEN 1 ELSE 0 END) as resolved,
                    SUM(CASE WHEN status = 'closed' THEN 1 ELSE 0 END) as closed
                FROM task_issues";
        
        if ($projId) {
            $sql .= " WHERE proj_id = ?";
            return $this->db->fetchOne($sql, [$projId]);
        }
        
        return $this->db->fetchOne($sql);
    }
}
