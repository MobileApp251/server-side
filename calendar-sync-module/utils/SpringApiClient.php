<?php
/**
 * Spring Boot API Client
 * HTTP client to communicate with Spring Boot backend
 */

class SpringApiClient {
    private $baseUrl;
    private $timeout;
    private $headers;
    
    /**
     * Constructor
     * @param string $baseUrl Spring Boot API base URL (e.g., http://localhost:8082)
     */
    public function __construct($baseUrl = null, $timeout = 30) {
        $this->baseUrl = $baseUrl ?? getenv('SPRING_API_URL') ?? 'http://localhost:8082';
        $this->timeout = $timeout;
        $this->headers = [
            'Content-Type: application/json',
            'Accept: application/json'
        ];
    }
    
    /**
     * Set authentication token
     * @param string $token JWT or Bearer token
     */
    public function setAuthToken($token) {
        $this->headers[] = "Authorization: Bearer {$token}";
    }
    
    /**
     * Make GET request
     * @param string $endpoint API endpoint (e.g., /api/notifications)
     * @param array $params Query parameters
     * @return array Response data
     */
    public function get($endpoint, $params = []) {
        $url = $this->baseUrl . $endpoint;
        
        if (!empty($params)) {
            $url .= '?' . http_build_query($params);
        }
        
        return $this->request('GET', $url);
    }
    
    /**
     * Make POST request
     * @param string $endpoint API endpoint
     * @param array $data Request body
     * @return array Response data
     */
    public function post($endpoint, $data = []) {
        $url = $this->baseUrl . $endpoint;
        return $this->request('POST', $url, $data);
    }
    
    /**
     * Make PUT request
     * @param string $endpoint API endpoint
     * @param array $data Request body
     * @return array Response data
     */
    public function put($endpoint, $data = []) {
        $url = $this->baseUrl . $endpoint;
        return $this->request('PUT', $url, $data);
    }
    
    /**
     * Make DELETE request
     * @param string $endpoint API endpoint
     * @return array Response data
     */
    public function delete($endpoint) {
        $url = $this->baseUrl . $endpoint;
        return $this->request('DELETE', $url);
    }
    
    /**
     * Make HTTP request using cURL
     * @param string $method HTTP method
     * @param string $url Full URL
     * @param array $data Request body (optional)
     * @return array Response data
     * @throws Exception on request failure
     */
    private function request($method, $url, $data = null) {
        $ch = curl_init();
        
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_TIMEOUT, $this->timeout);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $this->headers);
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $method);
        
        if ($data !== null && in_array($method, ['POST', 'PUT', 'PATCH'])) {
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
        }
        
        // For development, disable SSL verification
        // Remove these in production!
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, false);
        
        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $error = curl_error($ch);
        
        curl_close($ch);
        
        if ($error) {
            throw new Exception("cURL Error: {$error}");
        }
        
        $decodedResponse = json_decode($response, true);
        
        if ($httpCode >= 400) {
            $errorMsg = $decodedResponse['message'] ?? $decodedResponse['error'] ?? 'Unknown error';
            throw new Exception("API Error ({$httpCode}): {$errorMsg}");
        }
        
        return [
            'status_code' => $httpCode,
            'data' => $decodedResponse,
            'success' => $httpCode >= 200 && $httpCode < 300
        ];
    }
    
    /**
     * Check if Spring Boot API is reachable
     * @return bool True if API is up
     */
    public function healthCheck() {
        try {
            $response = $this->get('/actuator/health');
            return $response['success'];
        } catch (Exception $e) {
            return false;
        }
    }
}
