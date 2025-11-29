package com.nunespaz.boletofacil2.infrastructure.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class StatusController {

    @GetMapping
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Boleto Fácil 2 em execução");
    }
}
