<?php

class Response {
    /**
     * Send success response
     * 
     * @param mixed $data Response data
     * @param string|int $message Success message or row count
     * @param int $code HTTP status code
     * @return void
     */
    public static function success($data = [], $message = 'Success', int $code = 200): void {
        http_response_code($code);
        header('Content-Type: application/json');
        echo json_encode([
            'success' => true,
            'message' => $message,
            'data' => $data,
            'timestamp' => date('Y-m-d H:i:s')
        ]);
        exit;
    }

    /**
     * Send error response
     * 
     * @param string $message Error message
     * @param int $code HTTP status code
     * @param mixed $errors Additional error details
     * @return void
     */
    public static function error(string $message = 'Error', int $code = 400, $errors = null): void {
        http_response_code($code);
        header('Content-Type: application/json');
        
        $response = [
            'success' => false,
            'message' => $message,
            'timestamp' => date('Y-m-d H:i:s')
        ];
        
        if ($errors !== null) {
            $response['errors'] = $errors;
        }
        
        echo json_encode($response);
        exit;
    }
}
