package org.example.entities;

public class GastoPorPersona{
    public String nombre;
    public Float gasto;

    public GastoPorPersona(String nombre){
        this.nombre = nombre;
        gasto = 0f;
    }
    public void agregarGasto(float value){
        gasto += value;
    }

}