package org.example.controller;

import org.example.InmoupPropertyRepository;
import org.example.entities.InmoupProperty;
import org.example.services.InmoupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inmoup")
public class InmoupController {

    @Autowired
    private InmoupService inmoupService;
    @Autowired
    private InmoupPropertyRepository repository;

    @GetMapping("/properties")
    public ResponseEntity<List<InmoupProperty>> getProperties(){
        var properties = inmoupService.getProperties();
        repository.saveAll(properties);
        return ResponseEntity.ok().body(properties);
    }
    @GetMapping("/load")
    public ResponseEntity<List<InmoupProperty>> loadCasas(){
        return ResponseEntity.ok().body(inmoupService.loadCasas());
    }
    @GetMapping("/news")
    public ResponseEntity<List<InmoupProperty>> newCasas(){
        return ResponseEntity.ok().body(inmoupService.getNews());
    }
    @GetMapping("/removeds")
    public ResponseEntity<List<InmoupProperty>> removedsCasas(){
        return ResponseEntity.ok().body(inmoupService.getRemoveds());
    }
    @GetMapping("/searchAmount/{value}")
    public ResponseEntity<String> searchAmount(@PathVariable("value") long value){
        inmoupService.changeAmount(value);
        return ResponseEntity.ok().body("Changed to "+ value);
    }
}
