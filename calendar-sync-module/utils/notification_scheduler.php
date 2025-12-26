<?php
/**
 * Notification Scheduler
 * Run this script via cron job to automatically check for upcoming/overdue tasks
 * and send notifications
 * 
 * Cron example (every hour):
 * 0 * * * * php /path/to/notification_scheduler.php
 */

require_once __DIR__ . '/../config/config.php';
require_once __DIR__ . '/../models/CalendarEvent.php';
require_once __DIR__ . '/../models/TaskNotification.php';

$config = require __DIR__ . '/../config/config.php';
$notificationDays = $config['notification']['upcoming_task_days'];

try {
    $eventModel = new CalendarEvent();
    $notificationModel = new TaskNotification();
    
    echo "[" . date('Y-m-d H:i:s') . "] Starting notification scheduler...\n";
    
    // Check for upcoming tasks
    $upcomingTasks = $eventModel->getUpcomingTasks($notificationDays);
    echo "Found " . count($upcomingTasks) . " upcoming tasks\n";
    
    foreach ($upcomingTasks as $task) {
        // Create notification for upcoming task
        $notificationData = [
            'uid' => 'system', // TODO: Get actual uid from task_assignees table
            'proj_id' => $task['proj_id'],
            'task_id' => $task['task_id'],
            'notification_type' => 'upcoming',
            'title' => 'Upcoming task',
            'message' => "Task \"{$task['task_name']}\" is due on " . date('d/m/Y', strtotime($task['endAt']))
        ];
        
        $notificationModel->createNotification($notificationData);
        echo "Created upcoming notification for task: {$task['task_name']}\n";
    }
    
    // Check for overdue tasks
    $overdueTasks = $eventModel->getOverdueTasks();
    echo "Found " . count($overdueTasks) . " overdue tasks\n";
    
    foreach ($overdueTasks as $task) {
        // NOTE: Task status is managed by Java application, don't update here
        
        // Create notification for overdue task
        $notificationData = [
            'uid' => 'system', // TODO: Get actual uid from task_assignees table
            'proj_id' => $task['proj_id'],
            'task_id' => $task['task_id'],
            'notification_type' => 'overdue',
            'title' => 'Overdue task',
            'message' => "Task \"{$task['task_name']}\" is overdue! Due date was " . date('d/m/Y', strtotime($task['endAt']))
        ];
        
        $notificationModel->createNotification($notificationData);
        echo "Created overdue notification for task: {$task['task_name']}\n";
    }
    
    echo "[" . date('Y-m-d H:i:s') . "] Notification scheduler completed successfully\n";
    
} catch (Exception $e) {
    echo "[" . date('Y-m-d H:i:s') . "] ERROR: " . $e->getMessage() . "\n";
    exit(1);
}
