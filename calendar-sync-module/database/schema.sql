-- Calendar Sync Module Database Schema
-- This schema extends the existing Jira Clone application with calendar sync capabilities
-- IMPORTANT: This should be added to the same database as the Java application (railway)

-- Table: calendar_events
-- Stores calendar events synced with tasks from the existing tasks table
CREATE TABLE IF NOT EXISTS calendar_events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    proj_id VARCHAR(36) NOT NULL COMMENT 'Foreign key to projects.proj_id',
    task_id INT NOT NULL COMMENT 'Combined with proj_id forms FK to tasks',
    sync_status ENUM('synced', 'pending', 'failed') NOT NULL DEFAULT 'pending',
    last_sync_at TIMESTAMP NULL,
    calendar_notes TEXT COMMENT 'Additional calendar-specific notes',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_proj_task (proj_id, task_id),
    INDEX idx_sync_status (sync_status),
    FOREIGN KEY (proj_id) REFERENCES projects(proj_id) ON DELETE CASCADE,
    CONSTRAINT fk_calendar_task FOREIGN KEY (proj_id, task_id) REFERENCES tasks(proj_id, task_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: task_notifications
-- Stores notifications for upcoming and overdue tasks
CREATE TABLE IF NOT EXISTS task_notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    uid VARCHAR(36) NOT NULL COMMENT 'Foreign key to users.uid',
    proj_id VARCHAR(36) NOT NULL COMMENT 'Foreign key to projects.proj_id',
    task_id INT NOT NULL COMMENT 'Combined with proj_id forms FK to tasks',
    notification_type ENUM('upcoming', 'overdue', 'assigned', 'updated') NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notified_at TIMESTAMP NULL,
    INDEX idx_uid (uid),
    INDEX idx_proj_task (proj_id, task_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (uid) REFERENCES users(uid) ON DELETE CASCADE,
    FOREIGN KEY (proj_id) REFERENCES projects(proj_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: task_issues (sub-issues within a task)
CREATE TABLE IF NOT EXISTS task_issues (
    id INT AUTO_INCREMENT PRIMARY KEY,
    proj_id VARCHAR(36) NOT NULL COMMENT 'Foreign key to projects.proj_id',
    task_id INT NOT NULL COMMENT 'Combined with proj_id forms FK to tasks',
    issue_title VARCHAR(255) NOT NULL,
    issue_content TEXT,
    status ENUM('open', 'in_progress', 'resolved', 'closed') NOT NULL DEFAULT 'open',
    priority ENUM('high', 'medium', 'low') NOT NULL DEFAULT 'medium',
    created_by VARCHAR(36) NOT NULL COMMENT 'Foreign key to users.uid',
    assigned_to VARCHAR(36) COMMENT 'Foreign key to users.uid',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_proj_task (proj_id, task_id),
    INDEX idx_status (status),
    INDEX idx_created_by (created_by),
    INDEX idx_assigned_to (assigned_to),
    FOREIGN KEY (created_by) REFERENCES users(uid) ON DELETE CASCADE,
    FOREIGN KEY (assigned_to) REFERENCES users(uid) ON DELETE SET NULL,
    FOREIGN KEY (proj_id) REFERENCES projects(proj_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: calendar_filters (user-specific filters for calendar view)
CREATE TABLE IF NOT EXISTS calendar_filters (
    id INT AUTO_INCREMENT PRIMARY KEY,
    uid VARCHAR(36) NOT NULL COMMENT 'Foreign key to users.uid',
    filter_name VARCHAR(100) NOT NULL,
    status_filters JSON COMMENT 'Array of status values: ["open", "progress", "done"]',
    priority_filters JSON COMMENT 'Array of priority values: ["high", "medium", "low"]',
    start_date DATE,
    end_date DATE,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_uid (uid),
    FOREIGN KEY (uid) REFERENCES users(uid) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

