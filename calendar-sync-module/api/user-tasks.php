<?php
/**
 * API: Get Tasks by User
 * Endpoint: GET /api/user-tasks.php
 * 
 * Query Parameters:
 * - uid: User ID (required)
 * - proj_id: Project ID (optional)
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once __DIR__ . '/../models/CalendarEvent.php';
require_once __DIR__ . '/../utils/Response.php';

try {
    if (!isset($_GET['uid'])) {
        Response::error('User ID (uid) is required', 400);
        exit();
    }
    
    $eventModel = new CalendarEvent();
    
    $uid = $_GET['uid'];
    $projId = $_GET['proj_id'] ?? null;
    
    $tasks = $eventModel->getTasksByUser($uid, $projId);
    
    Response::success([
        'uid' => $uid,
        'total' => count($tasks),
        'tasks' => $tasks
    ]);
    
} catch (Exception $e) {
    Response::error($e->getMessage(), 500);
}
