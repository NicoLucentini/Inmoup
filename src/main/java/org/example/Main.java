package org.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@SpringBootApplication
public class Main {

    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
        long heapSize = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        long maxHeapSize = Runtime.getRuntime().maxMemory() / (1024 * 1024);

        System.out.println("Heap Size (MB): " + heapSize);
        System.out.println("Max Heap Size (MB): " + maxHeapSize);
    }

}