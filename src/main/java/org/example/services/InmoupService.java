package org.example.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.InmoupProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class InmoupService {

    private int searchValue = 100;
    boolean saveAsFile = false;
    String pages = "";


    public void changeAmount(long value) {
        searchValue = (int) value;
    }

    public List<InmoupProperty> getNews() {
        var old = loadCasas();

        var news = getProperties();

        return news.stream().filter(newProp -> old.stream().noneMatch(oldProp -> oldProp.isEqual(newProp)))
                .collect(Collectors.toList());
    }

    public List<InmoupProperty> getRemoveds() {
        var old = loadCasas();

        var news = getProperties();
        return old.stream().filter(oldProp -> news.stream().noneMatch(newProp -> newProp.isEqual(oldProp)))
                .collect(Collectors.toList());
    }

    public List<InmoupProperty> loadCasas() {
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

        if(type==null){
            response.addAll(getCasas());
            response.addAll(getDepartamentos());
        }
        else if(type.equalsIgnoreCase("casa")){
            var url = CASAS_URL()
                    .concat(minPrice!= null ? precioMin(minPrice):"")
                    .concat(maxPrice!=null ? precioMax(maxPrice) : "");
            response.addAll(getCasas());
        }
        else if(type.equalsIgnoreCase("departamento")){
            response.addAll(getDepartamentos());
        }
        else if(type.equalsIgnoreCase("lote")){
            response.addAll(getProperties(LOTES_URL()));
        }
        System.out.println("Response ready");
        return response.stream()
                .filter(p -> type == null || p.tip_desc.equalsIgnoreCase(type))
                .filter(p -> location == null || p.loc_desc.equalsIgnoreCase(location))
                .filter(p -> minPrice == null || p.prp_pre_dol >= minPrice)
                .filter(p -> maxPrice == null || p.prp_pre_dol <= maxPrice)
                .collect(Collectors.toList());

    }

    public List<InmoupProperty> getProperties(){
        var response = new ArrayList<InmoupProperty>();
        response.addAll(getCasas());
        response.addAll(getDepartamentos());
        System.out.println("Response ready");
        return response;
    }
    public List<InmoupProperty> getProperties(String url) {

        RestTemplate rest = new RestTemplate();

        System.out.println("Checking url: " + url);

        String response = rest.getForObject(url, String.class);

        System.out.println("Done Checking url: ");


        String finalString = getPropertiesJsonFromPage(response);
        finalString = cleanJsonToMakeItArray(finalString, searchValue);

        if(saveAsFile)
            saveAsFile(finalString);

        List<InmoupProperty> props = convertJsonToProperties(finalString);
        return props;

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
        return "https://www.inmoup.com.ar/inmuebles/casas-en-venta?favoritos=0&limit=" + searchValue;
    }
    private String DEPARTAMENTOS_URL() {
        return "https://www.inmoup.com.ar/inmuebles/departamentos-en-venta?favoritos=0&limit=" + searchValue;
    }
    private String LOTES_URL(){
        return "https://inmoup.com.ar/inmuebles/lotes-y-terrenos-en-venta?provincia=Mendoza&limit=" + searchValue;
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
