<?php
/**
 * API: Get Calendar Events with Filters
 * Endpoint: GET /api/events.php
 * 
 * Query Parameters:
 * - status: array of status values (done, in_progress, due, upcoming)
 * - start_date: YYYY-MM-DD
 * - end_date: YYYY-MM-DD
 * - project_id: UUID
 * - priority: array of priority values (high, medium, low)
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
    
    // Build filters from query parameters
    $filters = [];
    
    if (isset($_GET['status'])) {
        $filters['status'] = is_array($_GET['status']) ? $_GET['status'] : explode(',', $_GET['status']);
    }
    
    if (isset($_GET['start_date'])) {
        $filters['start_date'] = $_GET['start_date'];
    }
    
    if (isset($_GET['end_date'])) {
        $filters['end_date'] = $_GET['end_date'];
    }
    
    if (isset($_GET['proj_id'])) {
        $filters['proj_id'] = $_GET['proj_id'];
    }
    
    if (isset($_GET['priority'])) {
        $filters['priority'] = is_array($_GET['priority']) ? $_GET['priority'] : explode(',', $_GET['priority']);
    }
    
    if (isset($_GET['uid'])) {
        $filters['uid'] = $_GET['uid'];
    }
    
    $events = $eventModel->getEvents($filters);
    
    Response::success($events);
    
} catch (Exception $e) {
    Response::error($e->getMessage(), 500);
}
