package org.example.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.valueOf;

@Service
public class InmoupService {
    private int searchValue = 2;

    private String CASAS_URL() {
        return "https://www.inmoup.com.ar/inmuebles/casas-en-venta?favoritos=0&limit=" + searchValue +"&prevEstadoMap=&q=Mendoza&lastZoom=13&precio%5Bmin%5D=&precio%5Bmax%5D=&moneda=1&sup_cubierta%5Bmin%5D=&sup_cubierta%5Bmax%5D=&sup_total%5Bmin%5D=&sup_total%5Bmax%5D=";
    }

    public String getCasas(){

        RestTemplate rest = new RestTemplate();

        String response = rest.getForObject(CASAS_URL(), String.class);

        System.out.println("Checking url: "+ CASAS_URL()    );

        String finalString = getPropiedadesFromResponse(response);



        return finalString;

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
