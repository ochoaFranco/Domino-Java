package modelo;

import java.io.Serial;
import java.io.Serializable;

public class Ficha implements IFicha, Serializable {
    @Serial
    private static final long serialVersionUID = 5880280855451753727L;
    private  int izquierdo;
    private  int derecho;
    private boolean dadaVuelta = false;
    private boolean vertical = false;
    private boolean derechoFlag = false;
    private boolean izquierdoFlag = false;


    public Ficha(int izquierdo, int derecho) {
        this.izquierdo = izquierdo;
        this.derecho = derecho;
    }

    public int getDerecho() {
        return derecho;
    }
    public int getIzquierdo() {
        return izquierdo;
    }

    public boolean esFichaDoble() {
        return izquierdo == derecho;
    }

    public boolean isDadaVuelta() {
        return dadaVuelta;
    }
    public void darVuelta(boolean darVuelta) {
        dadaVuelta = darVuelta;
    }
    public void setIzquierdo(int izquierdo) {
        this.izquierdo = izquierdo;
    }

    public void setDerecho(int derecho) {
        this.derecho = derecho;
    }

    @Override
    public boolean isVertical() {
        return vertical;
    }

    @Override
    public boolean isDerecho() {
        return this.derechoFlag;
    }

    @Override
    public boolean isIzquierdo() {
        return this.izquierdoFlag;
    }

    @Override
    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    @Override
    public void setDerecho(boolean derecho) {
        this.derechoFlag = derecho;
    }

    @Override
    public void setIzquierdo(boolean izquierdo) {
        this.izquierdoFlag = izquierdo;
    }

    @Override
    public String toString() {
        return izquierdo + " " + derecho;
    }
}
