<?php
/**
 * Sync Existing Tasks to Calendar
 * This script syncs all existing tasks from the tasks table to calendar_events
 */

echo "==============================================\n";
echo "  📅 SYNC EXISTING TASKS TO CALENDAR\n";
echo "==============================================\n\n";

require_once __DIR__ . '/config/Database.php';

try {
    $db = Database::getInstance();
    $pdo = $db->getConnection();
    
    echo "🔌 Connected to Railway database\n\n";
    
    // Check existing tasks
    echo "📊 Checking existing tasks in database...\n";
    $tasksQuery = "SELECT proj_id, task_id, title, status, priority, startAt, endAt FROM tasks ORDER BY created_at DESC LIMIT 20";
    $tasks = $pdo->query($tasksQuery)->fetchAll();
    
    if (empty($tasks)) {
        echo "⚠️  No tasks found in database!\n";
        echo "Please create some tasks in your Java application first.\n";
        exit(0);
    }
    
    echo "✓ Found " . count($tasks) . " tasks\n\n";
    echo "🔄 Syncing tasks to calendar_events...\n\n";
    
    $insertQuery = "INSERT INTO calendar_events (proj_id, task_id, sync_status, calendar_notes, last_sync_at) 
                    VALUES (:proj_id, :task_id, 'synced', :notes, NOW())
                    ON DUPLICATE KEY UPDATE 
                        sync_status = 'synced', 
                        last_sync_at = NOW(),
                        calendar_notes = :notes";
    
    $stmt = $pdo->prepare($insertQuery);
    
    $syncCount = 0;
    $errorCount = 0;
    
    foreach ($tasks as $task) {
        try {
            $notes = sprintf(
                "Task: %s | Status: %s | Priority: %s",
                $task['title'],
                $task['status'],
                $task['priority']
            );
            
            $stmt->execute([
                'proj_id' => $task['proj_id'],
                'task_id' => $task['task_id'],
                'notes' => $notes
            ]);
            
            echo "✓ Synced: [{$task['proj_id']}-{$task['task_id']}] {$task['title']}\n";
            $syncCount++;
            
        } catch (PDOException $e) {
            echo "✗ Error syncing task [{$task['proj_id']}-{$task['task_id']}]: {$e->getMessage()}\n";
            $errorCount++;
        }
    }
    
    echo "\n==============================================\n";
    echo "📈 SYNC SUMMARY\n";
    echo "==============================================\n";
    echo "✅ Successfully synced: {$syncCount} tasks\n";
    echo "❌ Failed: {$errorCount} tasks\n";
    
    // Verify calendar_events data
    echo "\n==============================================\n";
    echo "🔍 VERIFICATION\n";
    echo "==============================================\n";
    
    $countQuery = "SELECT COUNT(*) as total FROM calendar_events";
    $total = $pdo->query($countQuery)->fetch()['total'];
    
    echo "Total calendar events: {$total}\n";
    
    if ($total > 0) {
        echo "\n📋 Sample calendar events:\n";
        $sampleQuery = "SELECT ce.*, t.title, t.status, t.priority 
                       FROM calendar_events ce
                       JOIN tasks t ON ce.proj_id = t.proj_id AND ce.task_id = t.task_id
                       LIMIT 5";
        $samples = $pdo->query($sampleQuery)->fetchAll();
        
        foreach ($samples as $idx => $sample) {
            echo "\n" . ($idx + 1) . ". Task: {$sample['title']}\n";
            echo "   Project ID: {$sample['proj_id']}\n";
            echo "   Task ID: {$sample['task_id']}\n";
            echo "   Status: {$sample['status']} | Priority: {$sample['priority']}\n";
            echo "   Sync Status: {$sample['sync_status']}\n";
            echo "   Last Sync: {$sample['last_sync_at']}\n";
        }
    }
    
    echo "\n==============================================\n";
    echo "🎉 SYNC COMPLETED!\n";
    echo "==============================================\n";
    echo "You can now test the calendar APIs:\n";
    echo "- http://localhost:8000/api/events.php\n";
    echo "- http://localhost:8000/api/calendar.php\n";
    echo "- http://localhost:8000/api/statistics.php\n";
    
} catch (Exception $e) {
    echo "\n❌ ERROR: " . $e->getMessage() . "\n";
    exit(1);
}
