<?php
/**
 * DEMO API: Get Events by Date Range (with mock data)
 * This is a demo version that works without database connection
 * Endpoint: GET /api/calendar-demo.php
 * 
 * Query Parameters:
 * - start_date: YYYY-MM-DD (required)
 * - end_date: YYYY-MM-DD (required)
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

require_once __DIR__ . '/../utils/Response.php';

try {
    // Validate required parameters
    if (!isset($_GET['start_date']) || !isset($_GET['end_date'])) {
        Response::error('start_date and end_date are required', 400);
        exit();
    }
    
    $startDate = $_GET['start_date'];
    $endDate = $_GET['end_date'];
    $projId = $_GET['proj_id'] ?? null;
    
    // Validate date format
    if (!preg_match('/^\d{4}-\d{2}-\d{2}$/', $startDate) || !preg_match('/^\d{4}-\d{2}-\d{2}$/', $endDate)) {
        Response::error('Invalid date format. Use YYYY-MM-DD', 400);
        exit();
    }
    
    // Mock data - realistic calendar events
    $mockEvents = [
        [
            'id' => 1,
            'task_id' => 101,
            'proj_id' => '550e8400-e29b-41d4-a716-446655440000',
            'title' => 'Complete API Documentation',
            'description' => 'Write comprehensive Swagger documentation for all endpoints',
            'start_date' => '2025-01-15T09:00:00Z',
            'end_date' => '2025-01-15T17:00:00Z',
            'status' => 'in_progress',
            'priority' => 'high',
            'assigned_to' => '660e8400-e29b-41d4-a716-446655440000',
            'created_at' => '2025-01-10T08:00:00Z',
            'updated_at' => '2025-01-14T15:30:00Z'
        ],
        [
            'id' => 2,
            'task_id' => 102,
            'proj_id' => '550e8400-e29b-41d4-a716-446655440000',
            'title' => 'Fix Authentication Bug',
            'description' => 'Resolve issue with special characters in passwords',
            'start_date' => '2025-01-16T10:00:00Z',
            'end_date' => '2025-01-16T18:00:00Z',
            'status' => 'due',
            'priority' => 'critical',
            'assigned_to' => '660e8400-e29b-41d4-a716-446655440000',
            'created_at' => '2025-01-12T09:00:00Z',
            'updated_at' => '2025-01-12T09:00:00Z'
        ],
        [
            'id' => 3,
            'task_id' => 103,
            'proj_id' => '770e8400-e29b-41d4-a716-446655440000',
            'title' => 'Database Optimization',
            'description' => 'Optimize slow queries and add indexes',
            'start_date' => '2025-01-20T09:00:00Z',
            'end_date' => '2025-01-22T17:00:00Z',
            'status' => 'upcoming',
            'priority' => 'medium',
            'assigned_to' => '770e8400-e29b-41d4-a716-446655440000',
            'created_at' => '2025-01-13T11:00:00Z',
            'updated_at' => '2025-01-13T11:00:00Z'
        ],
        [
            'id' => 4,
            'task_id' => 104,
            'proj_id' => '550e8400-e29b-41d4-a716-446655440000',
            'title' => 'Code Review',
            'description' => 'Review pull requests for calendar module',
            'start_date' => '2025-01-18T14:00:00Z',
            'end_date' => '2025-01-18T16:00:00Z',
            'status' => 'upcoming',
            'priority' => 'low',
            'assigned_to' => '660e8400-e29b-41d4-a716-446655440000',
            'created_at' => '2025-01-14T10:00:00Z',
            'updated_at' => '2025-01-14T10:00:00Z'
        ],
        [
            'id' => 5,
            'task_id' => 105,
            'proj_id' => '550e8400-e29b-41d4-a716-446655440000',
            'title' => 'Unit Tests Implementation',
            'description' => 'Write unit tests for all API endpoints',
            'start_date' => '2025-01-12T09:00:00Z',
            'end_date' => '2025-01-14T17:00:00Z',
            'status' => 'done',
            'priority' => 'high',
            'assigned_to' => '770e8400-e29b-41d4-a716-446655440000',
            'created_at' => '2025-01-08T08:00:00Z',
            'updated_at' => '2025-01-14T17:00:00Z'
        ]
    ];
    
    // Filter by date range
    $filteredEvents = array_filter($mockEvents, function($event) use ($startDate, $endDate) {
        $eventDate = substr($event['start_date'], 0, 10);
        return $eventDate >= $startDate && $eventDate <= $endDate;
    });
    
    // Filter by project if provided
    if ($projId) {
        $filteredEvents = array_filter($filteredEvents, function($event) use ($projId) {
            return $event['proj_id'] === $projId;
        });
    }
    
    // Reset array keys
    $filteredEvents = array_values($filteredEvents);
    
    Response::success([
        'total' => count($filteredEvents),
        'start_date' => $startDate,
        'end_date' => $endDate,
        'events' => $filteredEvents,
        '_note' => 'This is DEMO data. Use /api/calendar.php for real database queries.'
    ]);
    
} catch (Exception $e) {
    Response::error($e->getMessage(), 500);
}
