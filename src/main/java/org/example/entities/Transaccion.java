package org.example.entities;

public class Transaccion{
    public String acreedor;
    public String deudor;
    public float monto;

    public Transaccion(String deudor, String acreedor, float monto){
        this.acreedor = acreedor;
        this.deudor = deudor;
        this.monto = monto;
    }
    @Override
    public String toString() {
        return deudor + " paga $" + monto + " a " + acreedor;
    }
}
