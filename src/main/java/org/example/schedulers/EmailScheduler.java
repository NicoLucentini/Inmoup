package org.example.schedulers;

import org.example.entities.InmoupProperty;
import org.example.helpers.InmoupHelper;
import org.example.services.EmailService;
import org.example.services.InmoupService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class EmailScheduler {

    private final EmailService emailService;
    private final InmoupService inmoupService;
    public EmailScheduler(EmailService emailService, InmoupService inmoupService) {
        this.emailService = emailService;
        this.inmoupService = inmoupService;
    }

    @Scheduled(cron = "0 0 18 * * FRI")  // Runs every Friday at 9:00 AM
    public void sendWeeklyEmail() throws Exception{

        List<InmoupProperty> props = inmoupService.getPropertiesWithFilter("casa", null,null,null,null);


        ByteArrayResource resource = InmoupHelper.convertToJson(props);
        String date = LocalDate.now().toString();

        String filename = "properties"+date+".json";

        emailService.sendEmailWithAttachment("lucentini.nicolas@gmail.com", "Propiedades Casas", "Casas con fecha: "+date, resource, filename );

    }
}