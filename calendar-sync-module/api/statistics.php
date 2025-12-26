<?php
/**
 * API: Statistics
 * Endpoint: GET /api/statistics.php
 * 
 * Query Parameters:
 * - proj_id: UUID (optional)
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
require_once __DIR__ . '/../models/TaskIssue.php';
require_once __DIR__ . '/../utils/Response.php';

try {
    $eventModel = new CalendarEvent();
    $issueModel = new TaskIssue();
    
    $projId = $_GET['proj_id'] ?? null;
    
    $eventStats = $eventModel->getStatistics($projId);
    $issueStats = $issueModel->getIssueStatistics($projId);
    
    Response::success([
        'events' => $eventStats,
        'issues' => $issueStats
    ]);
    
} catch (Exception $e) {
    Response::error($e->getMessage(), 500);
}
