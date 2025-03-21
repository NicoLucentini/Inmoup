package org.example.controller;

import org.example.entities.InmoupProperty;
import org.example.helpers.InmoupHelper;
import org.example.services.EmailService;
import org.example.services.InmoupService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/email")
public class EmailController {
    private final EmailService emailService;
    private final InmoupService inmoupService;//Rustico

    public EmailController(EmailService emailService, InmoupService inmoupService) {
        this.emailService = emailService;
        this.inmoupService = inmoupService;
    }

    @GetMapping("/send")
    public void sendEmail(){
        emailService.sendEmail("lucentini.nicolas@gmail.com", "Test email", "Hello from java");
    }
    @GetMapping("/sendAttachment")
    public void sendEmailAttachment() throws Exception{

        List<InmoupProperty> props = inmoupService.getPropertiesWithFilter("casa", null,null,null,null);


        ByteArrayResource resource = InmoupHelper.convertToJson(props);
        String date = LocalDate.now().toString();

        String filename = "properties"+date+".json";

        emailService.sendEmailWithAttachment("lucentini.nicolas@gmail.com", "Propiedades Casas", "Casas con fecha: "+date, resource, filename );
    }
}
