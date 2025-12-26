<?php
/**
 * API: Get Events by Date Range (for calendar view)
 * Endpoint: GET /api/calendar.php
 * 
 * Query Parameters:
 * - start_date: YYYY-MM-DD (required)
 * - end_date: YYYY-MM-DD (required)
 * - project_id: UUID (optional)
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
    if (!isset($_GET['start_date']) || !isset($_GET['end_date'])) {
        Response::error('start_date and end_date are required', 400);
        exit();
    }
    
    $eventModel = new CalendarEvent();
    
    $startDate = $_GET['start_date'];
    $endDate = $_GET['end_date'];
    $projId = $_GET['proj_id'] ?? null;
    
    $events = $eventModel->getEventsByDateRange($startDate, $endDate, $projId);
    
    Response::success($events);
    
} catch (Exception $e) {
    Response::error($e->getMessage(), 500);
}
