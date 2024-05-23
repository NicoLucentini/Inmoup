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

    private String CASAS_URL = "https://www.inmoup.com.ar/inmuebles/casas-en-venta?favoritos=0&limit=" + searchValue +"&prevEstadoMap=&q=Mendoza&lastZoom=13&precio%5Bmin%5D=&precio%5Bmax%5D=&moneda=1&sup_cubierta%5Bmin%5D=&sup_cubierta%5Bmax%5D=&sup_total%5Bmin%5D=&sup_total%5Bmax%5D=";

    public void getCasas(){

        RestTemplate rest = new RestTemplate();

        String response = rest.getForObject(CASAS_URL, String.class);

        Integer amountFromPage = getAmount(response);
        String finalString = getPropiedadesFromResponse(response);
        finalString = cleanJsonToMakeItArray(finalString, amountFromPage);

        // Creating a File object that
        // represents the disk file
        try
        {
            PrintStream fileStream = new PrintStream(new File("Props.txt"));

            System.setOut(fileStream);

            System.out.println(finalString);
        }
        catch (Exception e){}

    }

    private Integer getAmount(String response){
        var split1 = response.split("h2");
        var lotes = split1[1].split("h2");
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(lotes[0]);
        while(m.find()) {
            return valueOf(m.group());
        }
        return -1;
    }
    private String getPropiedadesFromResponse(String response){
        var split = response.split("propiedades = ");
        var split2 = split[1].split("];");
        return split2[0] + "]";
    }
    private String cleanJsonToMakeItArray(String finalString, Integer amount){
        //Evitar esto luego

        for(int i = 0; i< amount +1;i++){
            finalString = finalString.replaceFirst("\""+i +"\":","");
        }



        finalString = finalString.substring(1, finalString.length() -1);

        finalString = "[" + finalString + "]";
        return finalString;
    }

    public void changeAmount(long value) {
        searchValue = (int)value;
    }
}
