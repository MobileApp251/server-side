<?php
/**
 * Database Configuration
 * Update these settings with your phpMyAdmin database credentials
 */

return [
    'database' => [
        'host' => 'ballast.proxy.rlwy.net',
        'port' => '26781',
        'dbname' => 'railway',
        'username' => 'root',
        'password' => 'QznRsDBFrPpRsjQsVRNYdAGeNsdATliC',
        'charset' => 'utf8mb4',
        'options' => [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES => false,
        ]
    ],
    
    'app' => [
        'name' => 'Calendar Sync Module',
        'version' => '1.0.0',
        'timezone' => 'Asia/Ho_Chi_Minh',
        'debug' => true,
    ],
    
    'cors' => [
        'allowed_origins' => ['*'],
        'allowed_methods' => ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
        'allowed_headers' => ['Content-Type', 'Authorization'],
    ],
    
    'notification' => [
        'upcoming_task_days' => 3, // Notify 3 days before task due date
        'check_interval_minutes' => 60, // Check for upcoming tasks every hour
    ],
];
