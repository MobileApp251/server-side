<?php
/**
 * API: Get Tasks by Project
 * Endpoint: GET /api/project-tasks.php
 * 
 * Query Parameters:
 * - proj_id: Project ID (required)
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
    if (!isset($_GET['proj_id'])) {
        Response::error('Project ID (proj_id) is required', 400);
        exit();
    }
    
    $eventModel = new CalendarEvent();
    
    $projId = $_GET['proj_id'];
    
    $tasks = $eventModel->getTasksByProject($projId);
    
    Response::success([
        'proj_id' => $projId,
        'total' => count($tasks),
        'tasks' => $tasks
    ]);
    
} catch (Exception $e) {
    Response::error($e->getMessage(), 500);
}
