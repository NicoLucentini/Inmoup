package org.example.controller;

import org.example.services.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/heap")
    public ResponseEntity<String> heap(){
        long heapSize = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        long maxHeapSize = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        String heap="Heap Size (MB): " + heapSize;
        String maxHeap="Max Heap Size (MB): " + maxHeapSize;
        System.out.println(heap);
        System.out.println(maxHeap);
        return ResponseEntity.ok(heap + " | " + maxHeap);
    }
    @GetMapping("/gc")
    public ResponseEntity<String> gc(){
        System.gc();
        return ResponseEntity.ok("Garbage collector");
    }

}
