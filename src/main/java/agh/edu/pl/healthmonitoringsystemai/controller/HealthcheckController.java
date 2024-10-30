package agh.edu.pl.healthmonitoringsystemai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/health")
public class HealthcheckController {

    @GetMapping("/status")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }
}
