<?php
/**
 * Google Calendar Sync API Endpoint
 * 
 * This endpoint allows syncing tasks to Google Calendar
 * 
 * Endpoints:
 * - GET /api/google-sync.php?action=sync&start_date=Y-m-d&end_date=Y-m-d - Sync tasks in date range
 * - GET /api/google-sync.php?action=sync_task&task_id=X&proj_id=Y - Sync single task
 * - GET /api/google-sync.php?action=auth - Get authorization URL
 * - GET /api/google-sync.php?action=callback&code=X - Handle OAuth callback
 * - GET /api/google-sync.php?action=list&start_date=Y-m-d&end_date=Y-m-d - List Google Calendar events
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

require_once __DIR__ . '/../utils/GoogleCalendarSync.php';
require_once __DIR__ . '/../utils/Response.php';

try {
    $action = $_GET['action'] ?? 'sync';
    $credentialsPath = __DIR__ . '/../config/credentials.json';
    
    // Check if credentials file exists
    if (!file_exists($credentialsPath) && $action !== 'setup') {
        Response::error('Credentials file not found. Please create config/credentials.json with your Google OAuth credentials', 500);
        exit;
    }
    
    switch ($action) {
        case 'setup':
            // Provide setup instructions
            Response::success([
                'message' => 'Google Calendar Sync Setup Instructions',
                'steps' => [
                    '1. Go to https://console.cloud.google.com/',
                    '2. Create a new project or select existing one',
                    '3. Enable Google Calendar API',
                    '4. Create OAuth 2.0 credentials (Desktop app)',
                    '5. Download credentials.json',
                    '6. Place it in calendar-sync-module/config/credentials.json',
                    '7. Visit /api/google-sync.php?action=auth to authorize'
                ],
                'credentials_path' => $credentialsPath,
                'credentials_exists' => file_exists($credentialsPath)
            ]);
            break;
            
        case 'auth':
            // Get authorization URL
            try {
                $syncService = new GoogleCalendarSync($credentialsPath);
                Response::error('Already authorized. Token exists.');
            } catch (Exception $e) {
                // Extract auth URL from exception message
                if (strpos($e->getMessage(), 'Please authorize') !== false) {
                    preg_match('/visiting: (.+)$/', $e->getMessage(), $matches);
                    $authUrl = $matches[1] ?? null;
                    
                    if ($authUrl) {
                        Response::success([
                            'message' => 'Please visit this URL to authorize the application',
                            'authorization_url' => $authUrl,
                            'callback_url' => 'http://' . $_SERVER['HTTP_HOST'] . '/api/google-sync.php?action=callback&code=YOUR_CODE'
                        ]);
                    } else {
                        throw $e;
                    }
                } else {
                    throw $e;
                }
            }
            break;
            
        case 'callback':
            // Handle OAuth callback
            $code = $_GET['code'] ?? null;
            
            if (!$code) {
                Response::error('Authorization code is required', 400);
                exit;
            }
            
            try {
                $client = new Google_Client();
                $client->setAuthConfig($credentialsPath);
                $client->setAccessType('offline');
                
                $accessToken = $client->fetchAccessTokenWithAuthCode($code);
                
                if (array_key_exists('error', $accessToken)) {
                    throw new Exception(join(', ', $accessToken));
                }
                
                // Save token
                $tokenPath = __DIR__ . '/../config/token.json';
                if (!file_exists(dirname($tokenPath))) {
                    mkdir(dirname($tokenPath), 0700, true);
                }
                file_put_contents($tokenPath, json_encode($accessToken));
                
                Response::success([
                    'message' => 'Authorization successful! You can now sync tasks to Google Calendar.',
                    'token_saved' => $tokenPath
                ]);
            } catch (Exception $e) {
                Response::error('Authorization failed: ' . $e->getMessage(), 500);
            }
            break;
            
        case 'sync':
            // Sync tasks in date range to Google Calendar
            $startDate = $_GET['start_date'] ?? null;
            $endDate = $_GET['end_date'] ?? null;
            $projId = $_GET['proj_id'] ?? null;
            
            if (!$startDate || !$endDate) {
                Response::error('start_date and end_date are required (format: Y-m-d)', 400);
                exit;
            }
            
            // Validate date format
            $datePattern = '/^\d{4}-\d{2}-\d{2}$/';
            if (!preg_match($datePattern, $startDate) || !preg_match($datePattern, $endDate)) {
                Response::error('Invalid date format. Use Y-m-d format (e.g., 2026-01-31)', 400);
                exit;
            }
            
            try {
                $syncService = new GoogleCalendarSync($credentialsPath);
                $results = $syncService->syncDateRangeToGoogle($startDate, $endDate, $projId);
                
                Response::success([
                    'message' => 'Sync completed',
                    'period' => [
                        'start' => $startDate,
                        'end' => $endDate
                    ],
                    'project_id' => $projId,
                    'results' => $results
                ]);
            } catch (Exception $e) {
                if (strpos($e->getMessage(), 'Please authorize') !== false) {
                    Response::error('Not authorized. Please visit /api/google-sync.php?action=auth first', 401);
                } else {
                    Response::error('Sync failed: ' . $e->getMessage(), 500);
                }
            }
            break;
            
        case 'sync_task':
            // Sync single task to Google Calendar
            $taskId = $_GET['task_id'] ?? null;
            $projId = $_GET['proj_id'] ?? null;
            
            if (!$taskId || !$projId) {
                Response::error('task_id and proj_id are required', 400);
                exit;
            }
            
            try {
                $syncService = new GoogleCalendarSync($credentialsPath);
                $result = $syncService->syncTaskToGoogle($taskId, $projId);
                
                if (isset($result['error'])) {
                    Response::error($result['error'], 404);
                } else {
                    Response::success([
                        'message' => 'Task synced successfully',
                        'result' => $result
                    ]);
                }
            } catch (Exception $e) {
                if (strpos($e->getMessage(), 'Please authorize') !== false) {
                    Response::error('Not authorized. Please visit /api/google-sync.php?action=auth first', 401);
                } else {
                    Response::error('Sync failed: ' . $e->getMessage(), 500);
                }
            }
            break;
            
        case 'list':
            // List Google Calendar events
            $startDate = $_GET['start_date'] ?? date('Y-m-01');
            $endDate = $_GET['end_date'] ?? date('Y-m-t');
            
            try {
                $syncService = new GoogleCalendarSync($credentialsPath);
                $result = $syncService->listGoogleEvents($startDate, $endDate);
                
                if ($result['success']) {
                    Response::success($result);
                } else {
                    Response::error($result['error'], 500);
                }
            } catch (Exception $e) {
                if (strpos($e->getMessage(), 'Please authorize') !== false) {
                    Response::error('Not authorized. Please visit /api/google-sync.php?action=auth first', 401);
                } else {
                    Response::error('Failed to list events: ' . $e->getMessage(), 500);
                }
            }
            break;
            
        case 'status':
            // Check sync status and configuration
            $tokenPath = __DIR__ . '/../config/token.json';
            $credentialsExists = file_exists($credentialsPath);
            $tokenExists = file_exists($tokenPath);
            
            $status = [
                'configured' => $credentialsExists,
                'authorized' => $tokenExists,
                'credentials_path' => $credentialsPath,
                'token_path' => $tokenPath
            ];
            
            if (!$credentialsExists) {
                $status['message'] = 'Not configured. Run ?action=setup for instructions.';
            } elseif (!$tokenExists) {
                $status['message'] = 'Not authorized. Visit /api/google-sync.php?action=auth to authorize.';
            } else {
                $status['message'] = 'Ready to sync!';
            }
            
            Response::success($status);
            break;
            
        default:
            Response::error('Invalid action. Available actions: setup, auth, callback, sync, sync_task, list, status', 400);
            break;
    }
    
} catch (Exception $e) {
    Response::error('Server error: ' . $e->getMessage(), 500);
}
