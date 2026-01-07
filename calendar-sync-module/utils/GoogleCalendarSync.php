<?php
/**
 * Google Calendar Sync Service
 * 
 * This service provides integration with Google Calendar API
 * to sync tasks from the calendar-sync-module to Google Calendar
 */

require_once __DIR__ . '/../vendor/autoload.php';
require_once __DIR__ . '/../models/CalendarEvent.php';

class GoogleCalendarSync {
    private $client;
    private $service;
    private $calendarModel;
    
    /**
     * Initialize Google Calendar client
     * 
     * @param string $credentialsPath Path to credentials.json file
     * @param string $tokenPath Path to token.json file (for storing OAuth tokens)
     */
    public function __construct($credentialsPath, $tokenPath = null) {
        if (!file_exists($credentialsPath)) {
            throw new Exception("Credentials file not found: $credentialsPath");
        }
        
        $this->client = new Google_Client();
        $this->client->setApplicationName('Calendar Sync Module');
        $this->client->setScopes(Google_Service_Calendar::CALENDAR);
        $this->client->setAuthConfig($credentialsPath);
        $this->client->setAccessType('offline');
        $this->client->setPrompt('select_account consent');
        
        // Token path for storing OAuth tokens
        if ($tokenPath === null) {
            $tokenPath = __DIR__ . '/../config/token.json';
        }
        
        // Load previously authorized token from file if exists
        if (file_exists($tokenPath)) {
            $accessToken = json_decode(file_get_contents($tokenPath), true);
            $this->client->setAccessToken($accessToken);
        }
        
        // If there is no previous token or it's expired, get a new one
        if ($this->client->isAccessTokenExpired()) {
            // Refresh the token if possible, else fetch a new one
            if ($this->client->getRefreshToken()) {
                $this->client->fetchAccessTokenWithRefreshToken($this->client->getRefreshToken());
            } else {
                // Request authorization from the user
                $authUrl = $this->client->createAuthUrl();
                throw new Exception("Please authorize the application by visiting: $authUrl");
            }
            
            // Save the token to file for future use
            if (!file_exists(dirname($tokenPath))) {
                mkdir(dirname($tokenPath), 0700, true);
            }
            file_put_contents($tokenPath, json_encode($this->client->getAccessToken()));
        }
        
        $this->service = new Google_Service_Calendar($this->client);
        $this->calendarModel = new CalendarEvent();
    }
    
    /**
     * Get authorization URL for OAuth flow
     * 
     * @return string Authorization URL
     */
    public function getAuthorizationUrl() {
        return $this->client->createAuthUrl();
    }
    
    /**
     * Set authorization code and save token
     * 
     * @param string $authCode Authorization code from OAuth redirect
     * @param string $tokenPath Path to save token
     * @return bool Success status
     */
    public function setAuthorizationCode($authCode, $tokenPath = null) {
        if ($tokenPath === null) {
            $tokenPath = __DIR__ . '/../config/token.json';
        }
        
        $accessToken = $this->client->fetchAccessTokenWithAuthCode($authCode);
        $this->client->setAccessToken($accessToken);
        
        // Check if there was an error
        if (array_key_exists('error', $accessToken)) {
            throw new Exception(join(', ', $accessToken));
        }
        
        // Save the token to file
        if (!file_exists(dirname($tokenPath))) {
            mkdir(dirname($tokenPath), 0700, true);
        }
        file_put_contents($tokenPath, json_encode($this->client->getAccessToken()));
        
        return true;
    }
    
    /**
     * Sync a single task to Google Calendar
     * 
     * @param string $taskId Task ID
     * @param string $projId Project ID
     * @return array Result with success status and event details
     */
    public function syncTaskToGoogle($taskId, $projId) {
        // Get task from database
        $tasks = $this->calendarModel->getEvents([
            'proj_id' => $projId
        ]);
        
        $task = null;
        foreach ($tasks as $t) {
            if ($t['task_id'] === $taskId) {
                $task = $t;
                break;
            }
        }
        
        if (!$task) {
            return ['error' => 'Task not found'];
        }
        
        try {
            // Create Google Calendar event
            $event = new Google_Service_Calendar_Event([
                'summary' => $task['task_name'],
                'description' => $this->formatDescription($task),
                'start' => [
                    'dateTime' => date('c', strtotime($task['startAt'])),
                    'timeZone' => 'Asia/Ho_Chi_Minh',
                ],
                'end' => [
                    'dateTime' => date('c', strtotime($task['endAt'])),
                    'timeZone' => 'Asia/Ho_Chi_Minh',
                ],
                'colorId' => $this->getPriorityColor($task['priority']),
                'extendedProperties' => [
                    'private' => [
                        'source' => 'calendar-sync-module',
                        'task_id' => $task['task_id'],
                        'proj_id' => $task['proj_id']
                    ]
                ]
            ]);
            
            $calendarId = 'primary';
            $createdEvent = $this->service->events->insert($calendarId, $event);
            
            return [
                'success' => true,
                'google_event_id' => $createdEvent->getId(),
                'html_link' => $createdEvent->getHtmlLink(),
                'task_id' => $task['task_id'],
                'task_name' => $task['task_name']
            ];
        } catch (Exception $e) {
            return [
                'success' => false,
                'error' => $e->getMessage(),
                'task_id' => $task['task_id'],
                'task_name' => $task['task_name']
            ];
        }
    }
    
    /**
     * Sync all tasks in a date range to Google Calendar
     * 
     * @param string $startDate Start date (Y-m-d format)
     * @param string $endDate End date (Y-m-d format)
     * @param string|null $projId Optional project ID filter
     * @return array Results array with sync status for each task
     */
    public function syncDateRangeToGoogle($startDate, $endDate, $projId = null) {
        $tasks = $this->calendarModel->getEventsByDateRange($startDate, $endDate, $projId);
        $results = [
            'total' => count($tasks),
            'successful' => 0,
            'failed' => 0,
            'details' => []
        ];
        
        foreach ($tasks as $task) {
            $result = $this->syncTaskToGoogle($task['task_id'], $task['proj_id']);
            
            if (isset($result['success']) && $result['success']) {
                $results['successful']++;
            } else {
                $results['failed']++;
            }
            
            $results['details'][] = $result;
        }
        
        return $results;
    }
    
    /**
     * Update an existing Google Calendar event
     * 
     * @param string $googleEventId Google Calendar event ID
     * @param string $taskId Task ID
     * @param string $projId Project ID
     * @return array Result with success status
     */
    public function updateGoogleEvent($googleEventId, $taskId, $projId) {
        // Get task from database
        $tasks = $this->calendarModel->getEvents([
            'proj_id' => $projId
        ]);
        
        $task = null;
        foreach ($tasks as $t) {
            if ($t['task_id'] === $taskId) {
                $task = $t;
                break;
            }
        }
        
        if (!$task) {
            return ['error' => 'Task not found'];
        }
        
        try {
            $calendarId = 'primary';
            $event = $this->service->events->get($calendarId, $googleEventId);
            
            // Update event details
            $event->setSummary($task['task_name']);
            $event->setDescription($this->formatDescription($task));
            $event->setStart(new Google_Service_Calendar_EventDateTime([
                'dateTime' => date('c', strtotime($task['startAt'])),
                'timeZone' => 'Asia/Ho_Chi_Minh',
            ]));
            $event->setEnd(new Google_Service_Calendar_EventDateTime([
                'dateTime' => date('c', strtotime($task['endAt'])),
                'timeZone' => 'Asia/Ho_Chi_Minh',
            ]));
            $event->setColorId($this->getPriorityColor($task['priority']));
            
            $updatedEvent = $this->service->events->update($calendarId, $event->getId(), $event);
            
            return [
                'success' => true,
                'google_event_id' => $updatedEvent->getId(),
                'html_link' => $updatedEvent->getHtmlLink()
            ];
        } catch (Exception $e) {
            return [
                'success' => false,
                'error' => $e->getMessage()
            ];
        }
    }
    
    /**
     * Delete a Google Calendar event
     * 
     * @param string $googleEventId Google Calendar event ID
     * @return array Result with success status
     */
    public function deleteGoogleEvent($googleEventId) {
        try {
            $calendarId = 'primary';
            $this->service->events->delete($calendarId, $googleEventId);
            
            return [
                'success' => true,
                'message' => 'Event deleted successfully'
            ];
        } catch (Exception $e) {
            return [
                'success' => false,
                'error' => $e->getMessage()
            ];
        }
    }
    
    /**
     * List all Google Calendar events in date range
     * 
     * @param string $startDate Start date
     * @param string $endDate End date
     * @return array List of events
     */
    public function listGoogleEvents($startDate, $endDate) {
        try {
            $calendarId = 'primary';
            $optParams = [
                'orderBy' => 'startTime',
                'singleEvents' => true,
                'timeMin' => date('c', strtotime($startDate)),
                'timeMax' => date('c', strtotime($endDate)),
            ];
            
            $results = $this->service->events->listEvents($calendarId, $optParams);
            $events = $results->getItems();
            
            $formattedEvents = [];
            foreach ($events as $event) {
                $formattedEvents[] = [
                    'id' => $event->getId(),
                    'summary' => $event->getSummary(),
                    'start' => $event->getStart()->getDateTime(),
                    'end' => $event->getEnd()->getDateTime(),
                    'html_link' => $event->getHtmlLink()
                ];
            }
            
            return [
                'success' => true,
                'count' => count($formattedEvents),
                'events' => $formattedEvents
            ];
        } catch (Exception $e) {
            return [
                'success' => false,
                'error' => $e->getMessage()
            ];
        }
    }
    
    /**
     * Format task description for Google Calendar
     * 
     * @param array $task Task data
     * @return string Formatted description
     */
    private function formatDescription($task) {
        $description = $task['content'] ?? '';
        $description .= "\n\n---\n";
        $description .= "Project: " . ($task['proj_name'] ?? 'N/A') . "\n";
        $description .= "Status: " . $task['status'] . "\n";
        $description .= "Priority: " . $task['priority'] . "\n";
        $description .= "Task ID: " . $task['task_id'] . "\n";
        
        return $description;
    }
    
    /**
     * Get Google Calendar color ID based on priority
     * 
     * @param string $priority Task priority
     * @return string Color ID
     */
    private function getPriorityColor($priority) {
        $colors = [
            'low' => '2',      // Green
            'medium' => '5',   // Yellow
            'high' => '6',     // Orange
            'critical' => '11' // Red
        ];
        return $colors[$priority] ?? '7'; // Default: Gray
    }
}
