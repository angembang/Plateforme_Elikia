package fr.elikia.backend.controller;

import fr.elikia.backend.security.jwt.RequiredJWTAuth;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @RequiredJWTAuth
    @GetMapping("/api/health")
    public  String health() {
        return "Elikia backend is runing";
    }
}
