-- Seed data for Calendar Sync Module
-- Sample data for testing and development
-- Note: Adjust UUIDs to match actual data in your database

-- Insert sample calendar events (syncing with existing tasks)
INSERT INTO calendar_events (proj_id, task_id, sync_status, calendar_notes) VALUES
('your-project-uuid-here', 1, 'synced', 'Mobile App Development - tracked on calendar'),
('your-project-uuid-here', 2, 'synced', 'Data Mining task - calendar tracking enabled'),
('your-project-uuid-here', 3, 'synced', 'Report preparation - calendar sync active');

-- Insert sample notifications (use actual user UIDs from your database)
INSERT INTO task_notifications (uid, proj_id, task_id, notification_type, title, message, is_read, notified_at) VALUES
('user-uuid-1', 'project-uuid-1', 3, 'upcoming', 'Upcoming task', 'Task "Report for CO4031" is upcoming', FALSE, NOW()),
('user-uuid-1', 'project-uuid-1', 4, 'overdue', 'Overdue task', 'Task "APIs Design for CO4029" is overdue', FALSE, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('user-uuid-1', 'project-uuid-1', 1, 'assigned', 'New task was assigned for you', 'You have been assigned to a new task', TRUE, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('user-uuid-1', 'project-uuid-1', 2, 'updated', 'Task updated', 'Task has been updated', TRUE, DATE_SUB(NOW(), INTERVAL 3 DAY));

-- Insert sample task issues
INSERT INTO task_issues (proj_id, task_id, issue_title, issue_content, status, priority, created_by) VALUES
('project-uuid-1', 1, 'Issue #1', 'No need to do', 'open', 'low', 'user-uuid-1'),
('project-uuid-1', 1, 'Issue #2', 'No need to do', 'in_progress', 'medium', 'user-uuid-1'),
('project-uuid-1', 1, 'Issue #3', 'No need to do', 'resolved', 'high', 'user-uuid-1');
