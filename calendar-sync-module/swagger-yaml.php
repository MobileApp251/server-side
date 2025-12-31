<?php
/**
 * Serve Swagger YAML with proper CORS headers
 */

// Set CORS headers
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Content-Type: application/x-yaml; charset=utf-8');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Read and serve the YAML file
$yamlFile = __DIR__ . '/swagger.yaml';
if (file_exists($yamlFile)) {
    readfile($yamlFile);
} else {
    http_response_code(404);
    echo "error: 'Swagger YAML file not found'";
}
