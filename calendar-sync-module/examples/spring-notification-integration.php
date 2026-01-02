<?php
/**
 * Example: Using Spring Notification Service from PHP
 * 
 * This example shows how to integrate PHP calendar module 
 * with Spring Boot notification system
 */

require_once __DIR__ . '/../utils/SpringNotificationService.php';

// Initialize the service
$springNotificationService = new SpringNotificationService('http://localhost:8082');

// Optional: Set authentication token if Spring requires it
// $springNotificationService->setAuthToken('your-jwt-token-here');

// Example 1: Get user notifications from Spring
function getUserNotificationsExample($userId) {
    global $springNotificationService;
    
    $notifications = $springNotificationService->getUserNotifications($userId, false, 20);
    
    if ($notifications) {
        echo "Found " . count($notifications) . " notifications\n";
        foreach ($notifications as $notification) {
            echo "- {$notification['title']}: {$notification['message']}\n";
        }
    } else {
        echo "No notifications or error occurred\n";
    }
}

// Example 2: Create notification when task is assigned
function notifyTaskAssignment($userId, $taskId, $taskTitle, $projectId) {
    global $springNotificationService;
    
    $result = $springNotificationService->sendTaskAssignmentNotification(
        $userId,
        $taskId,
        $taskTitle,
        $projectId
    );
    
    if ($result) {
        echo "Notification sent successfully!\n";
    } else {
        echo "Failed to send notification\n";
    }
}

// Example 3: Check unread count
function checkUnreadCount($userId) {
    global $springNotificationService;
    
    $count = $springNotificationService->getUnreadCount($userId);
    echo "User has {$count} unread notifications\n";
}

// Example 4: Mark notification as read
function markNotificationRead($notificationId) {
    global $springNotificationService;
    
    $success = $springNotificationService->markAsRead($notificationId);
    
    if ($success) {
        echo "Notification marked as read\n";
    } else {
        echo "Failed to mark notification as read\n";
    }
}

// Example 5: Send task due soon notification
function notifyTaskDueSoon($userId, $taskId, $taskTitle, $dueDate) {
    global $springNotificationService;
    
    $result = $springNotificationService->sendTaskDueSoonNotification(
        $userId,
        $taskId,
        $taskTitle,
        $dueDate
    );
    
    return $result !== null;
}

// Example 6: Check if Spring service is available
function checkSpringServiceHealth() {
    global $springNotificationService;
    
    if ($springNotificationService->isAvailable()) {
        echo "✅ Spring Boot notification service is available\n";
        return true;
    } else {
        echo "❌ Spring Boot notification service is not available\n";
        return false;
    }
}

// Run examples if this file is executed directly
if (php_sapi_name() === 'cli' && basename(__FILE__) === basename($_SERVER['PHP_SELF'])) {
    echo "=== Spring Notification Service Examples ===\n\n";
    
    // Check if Spring service is up
    checkSpringServiceHealth();
    echo "\n";
    
    // Example user and task IDs
    $userId = "550e8400-e29b-41d4-a716-446655440000";
    $taskId = 123;
    
    // Get notifications
    echo "1. Getting user notifications:\n";
    getUserNotificationsExample($userId);
    echo "\n";
    
    // Check unread count
    echo "2. Checking unread count:\n";
    checkUnreadCount($userId);
    echo "\n";
    
    // Send task assignment notification
    echo "3. Sending task assignment notification:\n";
    notifyTaskAssignment($userId, $taskId, "Complete API Documentation", "project-uuid");
    echo "\n";
    
    // Send due soon notification
    echo "4. Sending task due soon notification:\n";
    notifyTaskDueSoon($userId, $taskId, "Complete API Documentation", "2026-01-05");
    echo "\n";
}
