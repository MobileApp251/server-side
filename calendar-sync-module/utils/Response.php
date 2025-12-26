<?php
/**
 * Response Utility
 * Helper for consistent API responses
 */

class Response {
    /**
     * Send success response
     */
    public static function success($data, $statusCode = 200) {
        http_response_code($statusCode);
        echo json_encode([
            'success' => true,
            'data' => $data,
            'timestamp' => date('Y-m-d H:i:s')
        ], JSON_PRETTY_PRINT);
        exit();
    }
    
    /**
     * Send error response
     */
    public static function error($message, $statusCode = 400, $errors = null) {
        http_response_code($statusCode);
        $response = [
            'success' => false,
            'message' => $message,
            'timestamp' => date('Y-m-d H:i:s')
        ];
        
        if ($errors !== null) {
            $response['errors'] = $errors;
        }
        
        echo json_encode($response, JSON_PRETTY_PRINT);
        exit();
    }
}
