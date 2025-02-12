package org.example.entities;

import java.util.*;

public class Grupo {
    public String nombre;
    public ArrayList<String> participantes = new ArrayList<>();
    public ArrayList<Gasto> gastos = new ArrayList<>();


    public ArrayList<GastoPorPersona> liquidarGastos(){
        ArrayList<GastoPorPersona> gastosPorPersona = new ArrayList<>();

        for(String participante : participantes ){
            gastosPorPersona.add(new GastoPorPersona(participante));
        }

       for(Gasto gasto : gastos){
           gastosPorPersona.stream().filter(x-> x.nombre == gasto.nombrePagador).findFirst().get().agregarGasto(gasto.monto - gasto.valorPromedio());

           for(String nombrePrestado : gasto.nombresPrestados){

               gastosPorPersona.stream().filter(x-> x.nombre == nombrePrestado)
                       .findFirst()
                       .get()
                       .agregarGasto(-gasto.valorPromedio());
           }
       }

       return gastosPorPersona;
    }

    public ArrayList<Transaccion> calcularTransacciones(ArrayList<GastoPorPersona> gastos){

        ArrayList<Map.Entry<String, Float>> deudores = new ArrayList<>();

        ArrayList<Map.Entry<String, Float>> acreedores = new ArrayList<>();

        // 2. Crear listas de deudores (-) y acreedores (+)

        for (GastoPorPersona gasto : gastos) {
            if (gasto.gasto < 0)
                deudores.add(new AbstractMap.SimpleEntry<>(gasto.nombre, gasto.gasto));
            else if (gasto.gasto > 0)
                acreedores.add(new AbstractMap.SimpleEntry<>(gasto.nombre, gasto.gasto));
        }

        // Ordenar deudores (de menor a mayor) y acreedores (de mayor a menor)
        deudores.sort((a, b) -> Float.compare(a.getValue(), b.getValue()));
        acreedores.sort((a, b) -> Float.compare(b.getValue(), a.getValue()));

        // 3. Procesar transacciones
        ArrayList<Transaccion> transacciones = new ArrayList<>();
        int i = 0, j = 0;

        while (i < deudores.size() && j < acreedores.size()) {
            String deudor = deudores.get(i).getKey();
            String acreedor = acreedores.get(j).getKey();
            float deuda = -deudores.get(i).getValue();
            float credito = acreedores.get(j).getValue();
            float pago = Math.min(deuda, credito);

            // Registrar transacción
            transacciones.add(new Transaccion(deudor, acreedor, pago));

            // Actualizar saldos
            deudores.get(i).setValue(deudores.get(i).getValue() + pago);
            acreedores.get(j).setValue(acreedores.get(j).getValue() - pago);

            // Avanzar si el saldo llega a 0
            if (deudores.get(i).getValue() == 0) i++;
            if (acreedores.get(j).getValue() == 0) j++;
        }

        // 4. Imprimir resultado
        System.out.println("Transacciones mínimas necesarias:");
        for (Transaccion t : transacciones) {
            System.out.println(t);
        }

        return  transacciones;
    }

}

