package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.InmoupProperty;
import org.example.services.InmoupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/inmoup")
public class InmoupController {

    @Autowired
    private InmoupService inmoupService;

    @GetMapping("/properties")
    public ResponseEntity<List<InmoupProperty>> getProperties(){
        var properties = inmoupService.getProperties();
        //repository.saveAll(properties);
        return ResponseEntity.ok().body(properties);
    }
    @GetMapping("/propertiesfilter")
    public ResponseEntity<List<InmoupProperty>> getPropertiesWithFilter(
            @RequestParam(required = false, name = "tipo") String tipo,
            @RequestParam(required = false, name = "ubicacion") String ubicacion,
            @RequestParam(required = false, name = "minPrice") Integer minPrice,
            @RequestParam(required = false, name = "maxPrice") Integer maxPrice,
            @RequestParam(required = false, name = "page") Integer page){
        var properties = inmoupService.getPropertiesWithFilter(tipo, ubicacion,minPrice,maxPrice, page);
        return ResponseEntity.ok().body(properties);
    }
    @GetMapping("/properties-filter")
    public ResponseEntity<ByteArrayResource> getPropertiesWithFilterJson(
            @RequestParam(required = false, name = "tipo") String tipo,
            @RequestParam(required = false, name = "ubicacion") String ubicacion,
            @RequestParam(required = false, name = "minPrice") Integer minPrice,
            @RequestParam(required = false, name = "maxPrice") Integer maxPrice,
            @RequestParam(required = false, name = "page") Integer page){
        long startTime = System.nanoTime();
        var properties = inmoupService.getPropertiesWithFilter(tipo, ubicacion,minPrice,maxPrice, page);
        try {

            String date = LocalDate.now().toString();
            String pageS = page != null ? "-page" + page : "";
            ResponseEntity<ByteArrayResource> res = convertToJson(properties, "properties" + date + pageS);
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            double durationMs = duration / 1_000_000.0;
            System.out.println("Time elapsed: " + durationMs + " ms");
            return res;
        }
        catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/compare")
    public ResponseEntity<List<InmoupProperty>> compare(@RequestParam("old-file") MultipartFile oldFile,
                                                          @RequestParam("new-file") MultipartFile newFile) throws IOException {

        return ResponseEntity.ok().body(inmoupService.compareToFiles(oldFile, newFile));
    }
    @PostMapping("/compare-json")
    public ResponseEntity<ByteArrayResource> compareJson(@RequestParam("old-file") MultipartFile oldFile,
                                                        @RequestParam("new-file") MultipartFile newFile) throws Exception {

        List<InmoupProperty> props = inmoupService.compareToFiles(oldFile, newFile);
        String oldFileName =  oldFile.getOriginalFilename().replaceAll(".json","");
        String newFileName =  newFile.getOriginalFilename().replaceAll(".json","");
        return convertToJson(props,"Eliminadas-"+oldFileName+"-"+newFileName);
    }
    @GetMapping("/load")
    public ResponseEntity<List<InmoupProperty>> loadCasas(){
        return ResponseEntity.ok().body(inmoupService.loadFile());
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
    private ResponseEntity<ByteArrayResource> convertToJson(List<InmoupProperty> properties, String fileName) throws Exception{

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(properties);

        // Convert JSON string to bytes
        byte[] jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(jsonBytes);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // Force download
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName+".json")
                .body(resource);
    }
}
