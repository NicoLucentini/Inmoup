package org.example.controller;

import org.example.services.InmoupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InmoupController {

    @Autowired
    private InmoupService inmoupService;

    @GetMapping("/casas")
    public void getCasas(){
        inmoupService.getCasas();
    }
    @GetMapping("/searchAmount/{value}")
    public void searchAmount(@PathVariable("value") long value){
        inmoupService.changeAmount(value);
    }
}
