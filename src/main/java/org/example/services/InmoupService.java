package org.example.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.InmoupProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class InmoupService {

    private int searchValue = 500;
    private boolean saveAsFile = false;
    private String pages = "";


    public void changeAmount(long value) {
        searchValue = (int) value;
    }

    public List<InmoupProperty> getNews() {
        var old = loadFile();

        var news = getProperties();

        return news.stream().filter(newProp -> old.stream().noneMatch(oldProp -> oldProp.isEqual(newProp)))
                .collect(Collectors.toList());
    }

    public List<InmoupProperty> getRemoveds() {
        var old = loadFile();

        var news = getProperties();
        return old.stream().filter(oldProp -> news.stream().noneMatch(newProp -> newProp.isEqual(oldProp)))
                .collect(Collectors.toList());
    }

    public List<InmoupProperty> loadFile() {
        try {
            String text = new String(Files.readAllBytes(Paths.get("Props.json")), StandardCharsets.UTF_8);
            if (text.isEmpty() || text.isBlank()) return Arrays.asList();

            List<InmoupProperty> props = convertJsonToProperties(text);
            return props;
        } catch (Exception e) {
            System.out.println("Could not load file => Probably doesn't exist");
        }
        return Arrays.asList();
    }


    public List<InmoupProperty> getPropertiesWithFilter(String type, String location, Integer minPrice, Integer maxPrice, Integer page){

        var response = new ArrayList<InmoupProperty>();
        pages = page == null ? "" : "&page="+page;

        //VER QUE HACER CON ESTO
        if(type==null){
            response.addAll(getCasas());
            response.addAll(getDepartamentos());
        }
        else if(type.equalsIgnoreCase("casa")){
            if(page==null)
                response.addAll(searchLoop(CASAS_URL()));
            else
                response.addAll(getProperties(CASAS_URL().concat(pages)));
        }
        else if(type.equalsIgnoreCase("departamento")){
            if(page==null)
                response.addAll(searchLoop(DEPARTAMENTOS_URL()));
            else
                response.addAll(getProperties(DEPARTAMENTOS_URL().concat(pages)));
        }
        else if(type.equalsIgnoreCase("lote")){
            if(page==null)
                response.addAll(searchLoop(LOTES_URL()));
            else
                response.addAll(getProperties(LOTES_URL().concat(pages)));
        }
        else if(type.equalsIgnoreCase("finca")){
            if(page==null){
                response.addAll(searchLoop(FINCAS_URL()));
                response.addAll(searchLoop(CAMPOS_URL()));
            }
            else{
                response.addAll(getProperties(FINCAS_URL().concat(pages)));
                response.addAll(getProperties(CAMPOS_URL().concat(pages)));
            }
        }

        System.out.println("Response ready without filter and clear Total: " + response.size() );
        Set<Integer> seenIds = new HashSet<>();
        List<InmoupProperty> cleared = new ArrayList<>();
        List<InmoupProperty> props = response.stream()
                .filter(p -> location == null || p.loc_desc.equalsIgnoreCase(location))
                .filter(p -> minPrice == null || p.prp_pre_dol >= minPrice)
                .filter(p -> maxPrice == null || p.prp_pre_dol <= maxPrice)
                .collect(Collectors.toList());

        for(InmoupProperty prop : props){
            if (seenIds.add(prop.propiedad_id)) {  // `add()` returns false if id already exists
               cleared.add(prop);
            }
        }
        System.out.println("Response ready Total: " + cleared.size() );
        return  cleared;

    }
    private List<InmoupProperty> searchLoop(String url){
        var response = new ArrayList<InmoupProperty>();
        int restantes =99;
        int pag = 1;
        response.addAll(getProperties(url));
        while(restantes>0){
            pages = "&page="+pag;
            List<InmoupProperty> props = getProperties(url + pages);
            restantes = props.size();
            response.addAll(props);
            pag++;
        }
        return response;
    }
    public List<InmoupProperty> getProperties(){
        var response = new ArrayList<InmoupProperty>();
        response.addAll(getCasas());
        response.addAll(getDepartamentos());
        System.out.println("Response ready");
        return response;
    }


    public List<InmoupProperty> getProperties(String url) {

        RestTemplateBuilder rtb = new RestTemplateBuilder();
        RestTemplate r = rtb.setConnectTimeout(Duration.ofSeconds(360)).setReadTimeout(Duration.ofSeconds(360)).build();

        System.out.println("Checking url: " + url);

        String response = r.getForObject(url, String.class);

        System.out.println("Done Checking url: "+ url);

        String finalString = getPropertiesJsonFromPage(response);
        finalString = cleanJsonToMakeItArray(finalString, searchValue);

        if(saveAsFile)
            saveAsFile(finalString);

        return convertJsonToProperties(finalString);
    }

    private void saveAsFile(String props){
        try {
            //Text
            PrintStream fileStream = new PrintStream(new File("Props.json"));

            //Console
            PrintStream console = System.out;

            System.setOut(fileStream);

            System.out.println(props);

            System.setOut(console);
        } catch (Exception e) {
            System.out.println("Exception catched during save file ");
        }
    }
    private String cleanJsonToMakeItArray(String value, Integer amount) {
        String newValue = value;

        for (int i = 0; i < amount + 1; i++) {
            newValue = newValue.replaceFirst("\"" + i + "\":", "");
        }

        newValue = newValue.substring(1, newValue.length() - 1);

        newValue = "[" + newValue + "]";
        return newValue;
    }

    public List<InmoupProperty> compareToFiles(MultipartFile oldFile, MultipartFile newFile) throws IOException {
        String oldString = new String(oldFile.getBytes(), StandardCharsets.UTF_8);
        String newString = new String(newFile.getBytes(), StandardCharsets.UTF_8);
        List<InmoupProperty> oldProperties = convertJsonToProperties(oldString);
        List<InmoupProperty> newProperties = convertJsonToProperties(newString);

        return oldProperties.stream().filter(oldProp -> newProperties.stream().noneMatch(newProp -> newProp.isEqual(oldProp)))
                .collect(Collectors.toList());
    }
    public List<InmoupProperty> mergeFiles(MultipartFile oldFile, MultipartFile newFile) throws IOException {
        String oldString = new String(oldFile.getBytes(), StandardCharsets.UTF_8);
        String newString = new String(newFile.getBytes(), StandardCharsets.UTF_8);
        List<InmoupProperty> oldProperties = convertJsonToProperties(oldString);
        List<InmoupProperty> newProperties = convertJsonToProperties(newString);

        oldProperties.addAll(newProperties);
        return oldProperties;
    }

    private List<InmoupProperty> convertJsonToProperties(String values) {
        try {
            InmoupProperty[] props = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true)
                    .readValue(values, InmoupProperty[].class);

            System.out.println(props.length);

            for (InmoupProperty prop : props) {
                prop.doUrl();
                prop.doMap();
                prop.doDiasPublicada();
                prop.doLatLong();
            }

            return new LinkedList<>(Arrays.asList(props));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return Arrays.asList();
    }

    private String getPropertiesJsonFromPage(String response) {
        var split = response.split("propiedades = ");
        var split2 = split[1].split("};");
        return split2[0].concat("}");
    }


    private String CASAS_URL() {
        //return "https://www.inmoup.com.ar/inmuebles/casas-en-venta?limit=" + searchValue;
        return "https://www.inmoup.com.ar/inmuebles/casas-en-venta?q=Mendoza&limit=" + searchValue;
    }
    private String DEPARTAMENTOS_URL() {
        //return "https://www.inmoup.com.ar/inmuebles/departamentos-en-venta?limit=" + searchValue;
        return "https://www.inmoup.com.ar/inmuebles/departamentos-en-venta?q=Mendoza&limit=" + searchValue;
    }
    private String LOTES_URL(){
        //return "https://inmoup.com.ar/inmuebles/lotes-y-terrenos-en-venta?limit=" + searchValue;
        return "https://inmoup.com.ar/inmuebles/lotes-y-terrenos-en-venta?q=Mendoza&limit=" + searchValue;
    }
    private String FINCAS_URL(){
        //return "https://inmoup.com.ar/inmuebles/lotes-y-terrenos-en-venta?limit=" + searchValue;
        return "https://inmoup.com.ar/inmuebles/bodegas-y-fincas-en-venta?q=Mendoza&limit=" + searchValue;
    }
    private String CAMPOS_URL(){
        //return "https://inmoup.com.ar/inmuebles/lotes-y-terrenos-en-venta?limit=" + searchValue;
        return "https://inmoup.com.ar/inmuebles/campos-y-vinedos-en-venta?q=Mendoza&limit=" + searchValue;
    }
    private List<InmoupProperty> getCasas(){
        return getProperties(CASAS_URL() + pages);
    }
    private List<InmoupProperty> getDepartamentos(){
        return getProperties(DEPARTAMENTOS_URL() + pages);
    }
    private String precioMin(int value){
        return "&precio%5Bmin%5D=" +value;
    }
    private String precioMax(int value){
        return "&precio%5Bmax%5D=" +value;
    }
    private String moneda(int value){
        return "&moneda=2";
    }
    private String limit(int value){
        return "&limit="+value;
    }
    private String pages(int value){
        return "&page="+value;
    }



}
