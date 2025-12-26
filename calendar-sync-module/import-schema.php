<?php
/**
 * Auto-Import Database Schema
 * This script automatically imports schema.sql into Railway database
 */

echo "==============================================\n";
echo "  📦 DATABASE SCHEMA IMPORT TOOL\n";
echo "==============================================\n\n";

// Load database configuration
require_once __DIR__ . '/config/Database.php';

try {
    echo "🔌 Connecting to Railway database...\n";
    $db = Database::getInstance();
    $pdo = $db->getConnection();
    echo "✅ Connected successfully!\n\n";
    
    // Read schema file
    $schemaFile = __DIR__ . '/database/schema.sql';
    
    if (!file_exists($schemaFile)) {
        throw new Exception("Schema file not found: {$schemaFile}");
    }
    
    echo "📄 Reading schema file: database/schema.sql\n";
    $sql = file_get_contents($schemaFile);
    
    if (empty($sql)) {
        throw new Exception("Schema file is empty!");
    }
    
    echo "📊 Processing SQL statements...\n\n";
    
    // Remove comments (lines starting with --)
    $lines = explode("\n", $sql);
    $cleanedLines = array_filter($lines, function($line) {
        $trimmed = trim($line);
        return !empty($trimmed) && !preg_match('/^--/', $trimmed);
    });
    $cleanedSql = implode("\n", $cleanedLines);
    
    // Split SQL statements by semicolon
    $statements = array_filter(
        array_map('trim', explode(';', $cleanedSql)),
        function($stmt) {
            return !empty($stmt);
        }
    );
    
    echo "Found " . count($statements) . " SQL statements to execute\n\n";
    
    $successCount = 0;
    $errorCount = 0;
    $errors = [];
    
    // Execute each statement
    foreach ($statements as $index => $statement) {
        $statementNumber = $index + 1;
        
        try {
            // Extract table name for better logging
            preg_match('/CREATE TABLE.*?`?(\w+)`?/i', $statement, $matches);
            $tableName = $matches[1] ?? 'unknown';
            
            $pdo->exec($statement);
            
            echo "✓ [{$statementNumber}] Created table: {$tableName}\n";
            $successCount++;
            
        } catch (PDOException $e) {
            $errorCount++;
            $errorMsg = $e->getMessage();
            
            // Check if it's "already exists" error
            if (strpos($errorMsg, 'already exists') !== false) {
                echo "⚠ [{$statementNumber}] Table already exists: {$tableName} (skipped)\n";
            } else {
                echo "✗ [{$statementNumber}] Error: {$errorMsg}\n";
                $errors[] = [
                    'statement' => $statementNumber,
                    'table' => $tableName,
                    'error' => $errorMsg
                ];
            }
        }
    }
    
    echo "\n==============================================\n";
    echo "📈 IMPORT SUMMARY\n";
    echo "==============================================\n";
    echo "✅ Successfully executed: {$successCount} statements\n";
    echo "❌ Failed: {$errorCount} statements\n";
    
    if (!empty($errors)) {
        echo "\n🚨 ERRORS DETAIL:\n";
        foreach ($errors as $error) {
            echo "  - Statement #{$error['statement']} ({$error['table']}): {$error['error']}\n";
        }
    }
    
    echo "\n==============================================\n";
    echo "🔍 Verifying created tables...\n";
    echo "==============================================\n";
    
    // Verify tables
    $tables = $pdo->query("SHOW TABLES")->fetchAll(PDO::FETCH_COLUMN);
    
    $expectedTables = ['calendar_events', 'task_notifications', 'task_issues', 'calendar_filters'];
    $foundTables = [];
    $missingTables = [];
    
    foreach ($expectedTables as $table) {
        if (in_array($table, $tables)) {
            echo "✓ {$table}\n";
            $foundTables[] = $table;
        } else {
            echo "✗ {$table} (MISSING)\n";
            $missingTables[] = $table;
        }
    }
    
    echo "\n==============================================\n";
    if (empty($missingTables)) {
        echo "🎉 SUCCESS! All tables created successfully!\n";
    } else {
        echo "⚠️  WARNING: " . count($missingTables) . " table(s) missing!\n";
    }
    echo "==============================================\n";
    
} catch (Exception $e) {
    echo "\n❌ FATAL ERROR: " . $e->getMessage() . "\n";
    echo "Stack trace:\n" . $e->getTraceAsString() . "\n";
    exit(1);
}
