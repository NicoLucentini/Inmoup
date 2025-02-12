package org.example;


import org.example.entities.Gasto;
import org.example.entities.GastoPorPersona;
import org.example.entities.Grupo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@SpringBootApplication
public class Main {

    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);

        Grupo grupo = new Grupo();
        grupo.nombre = "Prueba 1";
        grupo.participantes = new ArrayList<>();
        grupo.participantes.add("Nicolas");
        grupo.participantes.add("Nicolas1");
        grupo.participantes.add("Nicolas2");

        grupo.gastos = new ArrayList<>();

        Gasto gasto1 = new Gasto();
        gasto1.nombresPrestados = new ArrayList<>();
        gasto1.nombresPrestados.add("Nicolas2");
        gasto1.nombresPrestados.add("Nicolas1");
        gasto1.detalle = "Coomida";
        gasto1.monto = 1200;
        gasto1.nombrePagador = "Nicolas";

        Gasto gasto2 = new Gasto();
        gasto2.nombresPrestados = new ArrayList<>();
        gasto2.nombresPrestados.add("Nicolas");
        gasto2.nombresPrestados.add("Nicolas1");
        gasto2.detalle = "Bebidas";
        gasto2.monto = 600;
        gasto2.nombrePagador = "Nicolas2";

        Gasto gasto3 = new Gasto();
        gasto3.nombresPrestados = new ArrayList<>();
        gasto3.nombresPrestados.add("Nicolas2");
        gasto3.nombresPrestados.add("Nicolas");
        gasto3.detalle = "Nafta";
        gasto3.monto = 2100;
        gasto3.nombrePagador = "Nicolas1";

        grupo.gastos.add(gasto1);
        grupo.gastos.add(gasto2);
        grupo.gastos.add(gasto3);

        ArrayList<GastoPorPersona> gastos = grupo.liquidarGastos();

        for(GastoPorPersona gasto : gastos){
            System.out.println("Total persona " + gasto.nombre + ", valor " + gasto.gasto);

        }
    }

}