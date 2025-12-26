<?php
/**
 * API: Get Overdue Tasks
 * Endpoint: GET /api/overdue.php
 * 
 * Query Parameters:
 * - user_id: UUID (optional)
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
    $eventModel = new CalendarEvent();
    
    $uid = $_GET['uid'] ?? null;
    
    $tasks = $eventModel->getOverdueTasks($uid);
    
    Response::success($tasks);
    
} catch (Exception $e) {
    Response::error($e->getMessage(), 500);
}
