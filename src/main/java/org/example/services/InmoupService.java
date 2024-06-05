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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.valueOf;

@Service
public class InmoupService {
    private int searchValue = 100;

    private String CASAS_URL() {
        return "https://www.inmoup.com.ar/inmuebles/casas-en-venta?favoritos=0&limit=" + searchValue + "&prevEstadoMap=&q=Mendoza&lastZoom=13&precio%5Bmin%5D=&precio%5Bmax%5D=&moneda=1&sup_cubierta%5Bmin%5D=&sup_cubierta%5Bmax%5D=&sup_total%5Bmin%5D=&sup_total%5Bmax%5D=";
    }
    private String DEPARTAMENTOS_URL() {
        return "https://www.inmoup.com.ar/inmuebles/departamentos-en-venta?favoritos=0&limit=" + searchValue + "&prevEstadoMap=&q=Mendoza&lastZoom=13&precio%5Bmin%5D=&precio%5Bmax%5D=&moneda=1&sup_cubierta%5Bmin%5D=&sup_cubierta%5Bmax%5D=&sup_total%5Bmin%5D=&sup_total%5Bmax%5D=";
    }

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


    public List<InmoupProperty> getProperties(){
        var casas = getCasas();
        var dptos = getDepartamentos();

        var response =casas;
        response.addAll(dptos);
        return response;
    }
    public List<InmoupProperty> getProperties(String url) {

        RestTemplate rest = new RestTemplate();

        String response = rest.getForObject(url, String.class);

        System.out.println("Checking url: " + url);

        String finalString = getPropertiesJsonFromPage(response);
        finalString = cleanJsonToMakeItArray(finalString, searchValue);

        try {
            //Text
            PrintStream fileStream = new PrintStream(new File("Props.json"));

            //Console
            PrintStream console = System.out;

            System.setOut(fileStream);
            System.out.println(finalString);

            System.setOut(console);
        } catch (Exception e) {
        }

        List<InmoupProperty> props = convertJsonToProperties(finalString);
        return props;

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

            for (InmoupProperty prop : props) prop.doUrl();

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

    private List<InmoupProperty> getCasas(){
        return getProperties(CASAS_URL());
    }
    private List<InmoupProperty> getDepartamentos(){
        return getProperties(DEPARTAMENTOS_URL());
    }


}
