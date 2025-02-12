package org.example.entities;

import java.util.ArrayList;

public class Gasto {

    public float monto;
    public String detalle;
    public String nombrePagador;
    public ArrayList<String> nombresPrestados = new ArrayList<>();

    public float valorPromedio(){
        return monto / (nombresPrestados.size() + 1);
    }

}
