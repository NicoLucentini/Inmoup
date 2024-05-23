package org.example.controller;

import org.example.services.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    private MainService mainService;

    @GetMapping("/hola")
    public void hola(){
       String res =  mainService.hola();
       System.out.println(res);
    }

}
