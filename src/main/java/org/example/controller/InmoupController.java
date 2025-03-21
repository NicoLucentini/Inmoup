package org.example.controller;

import org.example.entities.InmoupProperty;
import org.example.helpers.InmoupHelper;
import org.example.services.InmoupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

            ByteArrayResource resource = InmoupHelper.convertToJson(properties);
            String date = LocalDate.now().toString();
            String pageS = page != null ? "-page" + page : "";

            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            double durationMs = duration / 1_000_000.0;
            System.out.println("Time elapsed: " + durationMs + " ms");


            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM) // Force download
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+"properties" + date + pageS+".json")
                    .body(resource);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/merge")
    public ResponseEntity<List<InmoupProperty>> merge(@RequestParam("old-file") MultipartFile oldFile,
                                                          @RequestParam("new-file") MultipartFile newFile) throws IOException {

        return ResponseEntity.ok().body(inmoupService.mergeFiles(oldFile, newFile));
    }
    @PostMapping("/merge-json")
    public ResponseEntity<ByteArrayResource> mergeJson(@RequestParam("old-file") MultipartFile oldFile,
                                                        @RequestParam("new-file") MultipartFile newFile) throws Exception {

        List<InmoupProperty> props = inmoupService.mergeFiles(oldFile, newFile);
        String oldFileName =  oldFile.getOriginalFilename().replaceAll(".json","");
        String newFileName =  newFile.getOriginalFilename().replaceAll(".json","");

        ByteArrayResource resource = InmoupHelper.convertToJson(props);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // Force download
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+"Eliminadas-"+oldFileName+"-"+newFileName+".json")
                .body(resource);
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

        ByteArrayResource resource = InmoupHelper.convertToJson(props);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // Force download
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+"Eliminadas-"+oldFileName+"-"+newFileName+".json")
                .body(resource);
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

}
