package org.example.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.InmoupProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.valueOf;

@Service
public class InmoupService {
    private int searchValue = 10000;

    private String CASAS_URL() {
        return "https://www.inmoup.com.ar/inmuebles/casas-en-venta?favoritos=0&limit=" + searchValue +"&prevEstadoMap=&q=Mendoza&lastZoom=13&precio%5Bmin%5D=&precio%5Bmax%5D=&moneda=1&sup_cubierta%5Bmin%5D=&sup_cubierta%5Bmax%5D=&sup_total%5Bmin%5D=&sup_total%5Bmax%5D=";
    }

    public List<InmoupProperty> getCasas(){

        RestTemplate rest = new RestTemplate();

        String response = rest.getForObject(CASAS_URL(), String.class);

        System.out.println("Checking url: "+ CASAS_URL()    );

        String finalString = getPropiedadesFromResponse(response);
        finalString = cleanJsonToMakeItArray(finalString, searchValue);

        List<InmoupProperty> props = convertResponseToObject(finalString);
        return props;

    }
    private String cleanJsonToMakeItArray(String value, Integer amount){
        String newValue = value;

        for(int i = 0; i< amount +1;i++){
            newValue = newValue.replaceFirst("\""+i +"\":","");
        }

        newValue = newValue.substring(1, newValue.length() -1);

        newValue = "[" + newValue + "]";
        return newValue;
    }

    private List<InmoupProperty> convertResponseToObject(String values){
        try {
            InmoupProperty[] props = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY,true)
                    .readValue(values, InmoupProperty[].class);
            System.out.println(props.length);
            for(int i = 0; i< props.length;i++)
                props[i].doUrl();
            return Arrays.asList(props);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        return Arrays.asList();
    }

    private String getPropiedadesFromResponse(String response){
        var split = response.split("propiedades = ");
        var split2 = split[1].split("};");
        return split2[0].concat("}").split("]")[0];
    }

    public void changeAmount(long value) {
        searchValue = (int)value;
        System.out.println(searchValue);
    }
}
