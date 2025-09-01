package com.example.tickets;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    // Exponer ping tanto en /ping como en /api/ping para evitar confusiones
    @GetMapping({"/ping", "/api/ping"})
    public String ping() {
        return "pong";
    }
}
