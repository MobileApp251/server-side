<?php
/**
 * API: Task Issues
 * Endpoint: GET /api/issues.php
 * 
 * Query Parameters:
 * - task_id: number (required)
 * - proj_id: UUID (required)
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once __DIR__ . '/../models/TaskIssue.php';
require_once __DIR__ . '/../utils/Response.php';

try {
    $issueModel = new TaskIssue();
    
    // GET: Get issues for a task
    if ($_SERVER['REQUEST_METHOD'] === 'GET') {
        if (!isset($_GET['task_id']) || !isset($_GET['proj_id'])) {
            Response::error('task_id and proj_id are required', 400);
            exit();
        }
        
        $taskId = (int)$_GET['task_id'];
        $projId = $_GET['proj_id'];
        
        $issues = $issueModel->getTaskIssues($taskId, $projId);
        
        Response::success($issues);
    }
    
    // POST: Create new issue
    elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $data = json_decode(file_get_contents('php://input'), true);
        
        if (!isset($data['task_id']) || !isset($data['proj_id']) || !isset($data['issue_title'])) {
            Response::error('Missing required fields', 400);
            exit();
        }
        
        $issueId = $issueModel->createIssue($data);
        
        Response::success(['id' => $issueId], 201);
    }
    
    // PUT: Update issue
    elseif ($_SERVER['REQUEST_METHOD'] === 'PUT') {
        $data = json_decode(file_get_contents('php://input'), true);
        
        if (!isset($data['id'])) {
            Response::error('Issue id is required', 400);
            exit();
        }
        
        $id = $data['id'];
        unset($data['id']);
        
        $issueModel->updateIssue($id, $data);
        
        Response::success(['message' => 'Issue updated successfully']);
    }
    
    // DELETE: Delete issue
    elseif ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
        $data = json_decode(file_get_contents('php://input'), true);
        
        if (!isset($data['id'])) {
            Response::error('Issue id is required', 400);
            exit();
        }
        
        $issueModel->deleteIssue($data['id']);
        
        Response::success(['message' => 'Issue deleted successfully']);
    }
    
} catch (Exception $e) {
    Response::error($e->getMessage(), 500);
}
