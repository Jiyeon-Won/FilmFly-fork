package com.sparta.filmfly;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeathCheckController {

    @Value("${server.env}")
    private String env;
    @Value("${server.port}")
    private String serverPort;
    @Value(("${server.serverIp}"))
    private String serverIp;
    @Value(("${serverName}"))
    private String serverName;

    @GetMapping("/hc")
    public ResponseEntity<?> getHeathCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("serverName", serverName);
        response.put("serverIp", serverIp);
        response.put("serverPort", serverPort);
        response.put("env", env);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/env")
    public ResponseEntity<?> getEnv() {
        return ResponseEntity.ok(env);
    }
}