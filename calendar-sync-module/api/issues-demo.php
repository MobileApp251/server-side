<?php
/**
 * DEMO API: Task Issues (with mock data)
 * This is a demo version that works without database connection
 * 
 * Supports: GET, POST, PUT, DELETE (all return mock data)
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once __DIR__ . '/../utils/Response.php';

try {
    // GET: Get issues for a task
    if ($_SERVER['REQUEST_METHOD'] === 'GET') {
        if (!isset($_GET['task_id']) || !isset($_GET['proj_id'])) {
            Response::error('task_id and proj_id are required', 400);
            exit();
        }
        
        $taskId = (int)$_GET['task_id'];
        $projId = $_GET['proj_id'];
        
        $mockIssues = [
            [
                'id' => 1,
                'task_id' => $taskId,
                'proj_id' => $projId,
                'issue_title' => 'Authentication fails with special characters',
                'issue_description' => 'Users cannot login when password contains @#$ symbols',
                'severity' => 'high',
                'status' => 'open',
                'reported_by' => '660e8400-e29b-41d4-a716-446655440000',
                'created_at' => '2025-01-14T10:30:00Z',
                'updated_at' => '2025-01-14T10:30:00Z'
            ],
            [
                'id' => 2,
                'task_id' => $taskId,
                'proj_id' => $projId,
                'issue_title' => 'Session timeout too short',
                'issue_description' => 'Users are logged out after 5 minutes of inactivity',
                'severity' => 'medium',
                'status' => 'in_progress',
                'reported_by' => '770e8400-e29b-41d4-a716-446655440000',
                'created_at' => '2025-01-13T14:00:00Z',
                'updated_at' => '2025-01-14T09:15:00Z'
            ]
        ];
        
        Response::success([
            'total' => count($mockIssues),
            'task_id' => $taskId,
            'proj_id' => $projId,
            'issues' => $mockIssues,
            '_note' => 'This is DEMO data'
        ]);
    }
    
    // POST: Create new issue
    elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $data = json_decode(file_get_contents('php://input'), true);
        
        if (!isset($data['task_id']) || !isset($data['proj_id']) || !isset($data['issue_title'])) {
            Response::error('Missing required fields: task_id, proj_id, issue_title', 400);
            exit();
        }
        
        Response::success([
            'id' => rand(100, 999),
            'message' => 'Issue created successfully (DEMO)',
            '_note' => 'This is DEMO data - no actual database insert'
        ], 201);
    }
    
    // PUT: Update issue
    elseif ($_SERVER['REQUEST_METHOD'] === 'PUT') {
        $data = json_decode(file_get_contents('php://input'), true);
        
        if (!isset($data['id'])) {
            Response::error('Issue id is required', 400);
            exit();
        }
        
        Response::success([
            'message' => 'Issue updated successfully (DEMO)',
            'id' => $data['id'],
            '_note' => 'This is DEMO data - no actual database update'
        ]);
    }
    
    // DELETE: Delete issue
    elseif ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
        $data = json_decode(file_get_contents('php://input'), true);
        
        if (!isset($data['id'])) {
            Response::error('Issue id is required', 400);
            exit();
        }
        
        Response::success([
            'message' => 'Issue deleted successfully (DEMO)',
            'id' => $data['id'],
            '_note' => 'This is DEMO data - no actual database delete'
        ]);
    }
    
} catch (Exception $e) {
    Response::error($e->getMessage(), 500);
}
