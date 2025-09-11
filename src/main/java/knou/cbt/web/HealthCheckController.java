package knou.cbt.web;

import knou.cbt.mapper.HealthCheckMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    private final HealthCheckMapper healthCheckMapper;

    public HealthCheckController(HealthCheckMapper healthCheckMapper) {
        this.healthCheckMapper = healthCheckMapper;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        try {
            String dbResult = healthCheckMapper.healthCheck();
            result.put("status", "UP");
            result.put("db", dbResult);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("status", "DOWN");
            result.put("error", e.getMessage());
            return ResponseEntity.status(503).body(result); // 503 Service Unavailable
        }
    }
}
