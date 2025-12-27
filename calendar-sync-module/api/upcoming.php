<?php
/**
 * API: Get Upcoming Tasks
 * Endpoint: GET /api/upcoming.php
 * 
 * Query Parameters:
 * - days: number of days to look ahead (default: 7)
 * - uid: User ID (optional)
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
    $eventModel = new CalendarEvent();
    
    $days = isset($_GET['days']) ? (int)$_GET['days'] : 7;
    $uid = $_GET['uid'] ?? null;
    $projId = $_GET['proj_id'] ?? null;
    
    $tasks = $eventModel->getUpcomingTasks($days, $uid, $projId);
    
    Response::success($tasks);
    
} catch (Exception $e) {
    Response::error($e->getMessage(), 500);
}
