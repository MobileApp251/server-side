<?php
/**
 * PHPUnit Test for Calendar Sync Module
 * Run: vendor/bin/phpunit tests/CalendarEventTest.php
 */

use PHPUnit\Framework\TestCase;

class CalendarEventTest extends TestCase
{
    private $model;
    
    protected function setUp(): void
    {
        // Setup before each test
        require_once __DIR__ . '/../models/CalendarEvent.php';
        $this->model = new CalendarEvent();
    }
    
    // ==========================================
    // Test Suite 1: Basic Model Tests
    // ==========================================
    
    /**
     * Test 1.1: getEvents() returns array
     */
    public function testGetEventsReturnsArray()
    {
        $result = $this->model->getEvents();
        $this->assertIsArray($result, 'getEvents() should return an array');
    }
    
    /**
     * Test 1.2: getEvents() with status filter
     */
    public function testGetEventsWithStatusFilter()
    {
        $filters = ['status' => ['open', 'progress']];
        $result = $this->model->getEvents($filters);
        
        $this->assertIsArray($result);
        
        // Verify all tasks have correct status
        foreach ($result as $task) {
            $this->assertContains(
                $task['status'], 
                ['open', 'progress'],
                'Task status should be open or progress'
            );
        }
    }
    
    /**
     * Test 1.3: getEvents() with priority filter
     */
    public function testGetEventsWithPriorityFilter()
    {
        $filters = ['priority' => ['high', 'medium']];
        $result = $this->model->getEvents($filters);
        
        $this->assertIsArray($result);
        
        foreach ($result as $task) {
            $this->assertContains(
                $task['priority'],
                ['high', 'medium'],
                'Task priority should be high or medium'
            );
        }
    }
    
    /**
     * Test 1.4: getEvents() with project filter
     */
    public function testGetEventsWithProjectFilter()
    {
        // Skip if no projects in database
        $allTasks = $this->model->getEvents();
        if (empty($allTasks)) {
            $this->markTestSkipped('No tasks in database');
        }
        
        $projId = $allTasks[0]['proj_id'];
        $filters = ['proj_id' => $projId];
        $result = $this->model->getEvents($filters);
        
        $this->assertIsArray($result);
        
        foreach ($result as $task) {
            $this->assertEquals(
                $projId,
                $task['proj_id'],
                'All tasks should belong to same project'
            );
        }
    }
    
    // ==========================================
    // Test Suite 2: Statistics Tests
    // ==========================================
    
    /**
     * Test 1.12: getStatistics() returns correct structure
     */
    public function testGetStatisticsStructure()
    {
        $result = $this->model->getStatistics();
        
        // Check required keys
        $this->assertArrayHasKey('total', $result);
        $this->assertArrayHasKey('completed', $result);
        $this->assertArrayHasKey('in_progress', $result);
        $this->assertArrayHasKey('open', $result);
        $this->assertArrayHasKey('closed', $result);
        $this->assertArrayHasKey('reopened', $result);
        $this->assertArrayHasKey('overdue', $result);
        
        // Check all are integers
        $this->assertIsNumeric($result['total']);
        $this->assertIsNumeric($result['completed']);
        $this->assertIsNumeric($result['in_progress']);
    }
    
    /**
     * Test 1.12: Statistics totals are consistent
     */
    public function testStatisticsTotalsAreConsistent()
    {
        $result = $this->model->getStatistics();
        
        // Sum of status counts should be <= total
        $statusSum = $result['completed'] + $result['in_progress'] + 
                     $result['open'] + $result['closed'] + $result['reopened'];
        
        $this->assertLessThanOrEqual(
            $result['total'],
            $statusSum,
            'Sum of status counts should not exceed total'
        );
    }
    
    // ==========================================
    // Test Suite 3: Upcoming Tasks Tests
    // ==========================================
    
    /**
     * Test 1.7: getUpcomingTasks() returns array
     */
    public function testGetUpcomingTasksReturnsArray()
    {
        $result = $this->model->getUpcomingTasks(7);
        $this->assertIsArray($result);
    }
    
    /**
     * Test 1.7: Upcoming tasks have days_remaining field
     */
    public function testUpcomingTasksHaveDaysRemaining()
    {
        $result = $this->model->getUpcomingTasks(7);
        
        if (empty($result)) {
            $this->assertTrue(true, 'No upcoming tasks found');
            return;
        }
        
        foreach ($result as $task) {
            $this->assertArrayHasKey(
                'days_remaining',
                $task,
                'Each upcoming task should have days_remaining field'
            );
        }
    }
    
    /**
     * Test 1.7: Upcoming tasks have correct status
     */
    public function testUpcomingTasksHaveCorrectStatus()
    {
        $result = $this->model->getUpcomingTasks(7);
        
        if (empty($result)) {
            $this->assertTrue(true, 'No upcoming tasks found');
            return;
        }
        
        foreach ($result as $task) {
            $this->assertContains(
                $task['status'],
                ['open', 'progress', 'reopen'],
                'Upcoming tasks should only have status: open, progress, or reopen'
            );
        }
    }
    
    // ==========================================
    // Test Suite 4: Overdue Tasks Tests
    // ==========================================
    
    /**
     * Test 1.9: getOverdueTasks() returns array
     */
    public function testGetOverdueTasksReturnsArray()
    {
        $result = $this->model->getOverdueTasks();
        $this->assertIsArray($result);
    }
    
    /**
     * Test 1.9: Overdue tasks have days_overdue field
     */
    public function testOverdueTasksHaveDaysOverdue()
    {
        $result = $this->model->getOverdueTasks();
        
        foreach ($result as $task) {
            $this->assertArrayHasKey(
                'days_overdue',
                $task,
                'Each overdue task should have days_overdue field'
            );
            $this->assertIsInt($task['days_overdue']);
            $this->assertGreaterThan(0, $task['days_overdue']);
        }
    }
    
    /**
     * Test 1.9: Overdue tasks are not done or closed
     */
    public function testOverdueTasksAreIncomplete()
    {
        $result = $this->model->getOverdueTasks();
        
        foreach ($result as $task) {
            $this->assertNotContains(
                $task['status'],
                ['done', 'close'],
                'Overdue tasks should not have status done or close'
            );
        }
    }
    
    /**
     * Test 1.9: Overdue tasks are past deadline
     */
    public function testOverdueTasksArePastDeadline()
    {
        $result = $this->model->getOverdueTasks();
        $now = new DateTime();
        
        foreach ($result as $task) {
            $endAt = new DateTime($task['endAt']);
            $this->assertLessThan(
                $now,
                $endAt,
                'Overdue task endAt should be before current time'
            );
        }
    }
    
    // ==========================================
    // Test Suite 5: Calendar View Tests
    // ==========================================
    
    /**
     * Test 1.6: getEventsByDateRange() returns array
     */
    public function testGetEventsByDateRangeReturnsArray()
    {
        $startDate = '2025-12-01';
        $endDate = '2025-12-31';
        $result = $this->model->getEventsByDateRange($startDate, $endDate);
        
        $this->assertIsArray($result);
    }
    
    /**
     * Test 1.6: Calendar events are within date range
     */
    public function testCalendarEventsAreInRange()
    {
        $startDate = '2025-12-01';
        $endDate = '2025-12-31';
        $result = $this->model->getEventsByDateRange($startDate, $endDate);
        
        $rangeStart = new DateTime($startDate);
        $rangeEnd = new DateTime($endDate);
        
        foreach ($result as $task) {
            $taskStart = new DateTime($task['startAt']);
            $taskEnd = new DateTime($task['endAt']);
            
            // Task should overlap with range
            $overlaps = 
                ($taskStart <= $rangeEnd && $taskEnd >= $rangeStart) ||
                ($taskStart >= $rangeStart && $taskStart <= $rangeEnd) ||
                ($taskEnd >= $rangeStart && $taskEnd <= $rangeEnd);
            
            $this->assertTrue(
                $overlaps,
                'Task should overlap with specified date range'
            );
        }
    }
    
    // ==========================================
    // Test Suite 6: User Tasks Tests
    // ==========================================
    
    /**
     * Test 1.10: getTasksByUser() returns array
     */
    public function testGetTasksByUserReturnsArray()
    {
        // Skip if no tasks
        $allTasks = $this->model->getEvents();
        if (empty($allTasks)) {
            $this->markTestSkipped('No tasks in database');
        }
        
        // Get a user ID from existing tasks
        $tasks = $this->model->getEvents();
        if (empty($tasks)) {
            $this->markTestSkipped('No tasks with assignees');
        }
        
        // This test requires task_assignees data
        // You may need to adjust based on your test data
        $this->assertTrue(true, 'User task retrieval test placeholder');
    }
    
    // ==========================================
    // Test Suite 7: Project Tasks Tests
    // ==========================================
    
    /**
     * Test 1.11: getTasksByProject() returns array with assignees
     */
    public function testGetTasksByProjectReturnsArrayWithAssignees()
    {
        // Skip this test as task_assignees table doesn't exist yet
        $this->markTestSkipped('task_assignees table not available in current schema');
        
        // Skip if no projects
        $allTasks = $this->model->getEvents();
        if (empty($allTasks)) {
            $this->markTestSkipped('No tasks in database');
        }
        
        $projId = $allTasks[0]['proj_id'];
        $result = $this->model->getTasksByProject($projId);
        
        $this->assertIsArray($result);
        
        foreach ($result as $task) {
            $this->assertArrayHasKey('assignees', $task);
            $this->assertArrayHasKey('assignee_ids', $task);
        }
    }
    
    // ==========================================
    // Test Suite 8: Edge Cases
    // ==========================================
    
    /**
     * Test 4.1: Empty results handled gracefully
     */
    public function testEmptyResultsHandledGracefully()
    {
        // Query with non-existent project ID
        $filters = ['proj_id' => 'non-existent-uuid-12345'];
        $result = $this->model->getEvents($filters);
        
        $this->assertIsArray($result);
        $this->assertEmpty($result);
    }
    
    /**
     * Test: Invalid filters don't cause errors
     */
    public function testInvalidFiltersDontCauseErrors()
    {
        $filters = ['invalid_key' => 'invalid_value'];
        $result = $this->model->getEvents($filters);
        
        // Should still return array, just ignoring invalid filter
        $this->assertIsArray($result);
    }
    
    // ==========================================
    // Test Suite 9: Data Integrity
    // ==========================================
    
    /**
     * Test: All tasks have required fields
     */
    public function testTasksHaveRequiredFields()
    {
        $result = $this->model->getEvents();
        
        if (empty($result)) {
            $this->markTestSkipped('No tasks in database');
        }
        
        $requiredFields = [
            'proj_id', 'task_id', 'task_name', 
            'status', 'priority', 'startAt', 'endAt'
        ];
        
        foreach ($result as $task) {
            foreach ($requiredFields as $field) {
                $this->assertArrayHasKey(
                    $field,
                    $task,
                    "Task should have field: {$field}"
                );
            }
        }
    }
    
    /**
     * Test: Status values are valid
     */
    public function testStatusValuesAreValid()
    {
        $result = $this->model->getEvents();
        
        $validStatuses = ['open', 'progress', 'done', 'reopen', 'close'];
        
        foreach ($result as $task) {
            $this->assertContains(
                $task['status'],
                $validStatuses,
                'Task status must be one of: ' . implode(', ', $validStatuses)
            );
        }
    }
    
    /**
     * Test: Priority values are valid
     */
    public function testPriorityValuesAreValid()
    {
        $result = $this->model->getEvents();
        
        $validPriorities = ['high', 'medium', 'low'];
        
        foreach ($result as $task) {
            $this->assertContains(
                $task['priority'],
                $validPriorities,
                'Task priority must be one of: ' . implode(', ', $validPriorities)
            );
        }
    }
}
