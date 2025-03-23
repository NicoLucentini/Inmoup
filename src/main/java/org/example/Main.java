package org.example;


import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@SpringBootApplication
@EnableScheduling
public class Main  {


    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
        long heapSize = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        long maxHeapSize = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        Dotenv dotenv = Dotenv.load();

        // Get the MAIL_PASSWORD from the environment variables
        String mailPassword = dotenv.get("MAIL_PASSWORD");
        // Use the password (for example, print it or configure email service)
        System.out.println("MAIL_PASSWORD: " + mailPassword);

        System.out.println("Heap Size (MB): " + heapSize);
        System.out.println("Max Heap Size (MB): " + maxHeapSize);
    }

}