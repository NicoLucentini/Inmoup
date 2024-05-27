package org.example.controller;

import org.example.services.InmoupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inmoup")
public class InmoupController {

    @Autowired
    private InmoupService inmoupService;

    @GetMapping("/casas")
    public ResponseEntity<String> getCasas(){
        return ResponseEntity.ok().body(inmoupService.getCasas());
    }
    @GetMapping("/searchAmount/{value}")
    public ResponseEntity<String> searchAmount(@PathVariable("value") long value){
        inmoupService.changeAmount(value);
        return ResponseEntity.ok().body("Changed to "+ value);
    }
}
