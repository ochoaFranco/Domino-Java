package modelo;

import modelo.exceptions.FichaIncorrecta;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Tablero implements Serializable {
    @Serial
    private static final long serialVersionUID = 5232608685946468912L;
    private final int MAXIMO = 8;
    private ArrayList<IFicha> fichas = new ArrayList<>();
    private IFicha extremoIzq;
    private IFicha extremoDerec;
    private final int cantFichasMaxTab = 10;
    private final int[] extremosJugados = new int[7];

    public Tablero() {

    }

    public IFicha getExtremoDerec() {
        return extremoDerec;
    }

    public IFicha getExtremoIzq() {
        return extremoIzq;
    }

    // agrega una ficha en el extremo derecho.
    public void setExtremoDerec(IFicha extremoDerec) throws FichaIncorrecta {
        if (extremoIzq != null) { // seria nulo cuando no hay fichas en el tablero.
            int tableroDer = this.extremoDerec.getDerecho();
            if (tableroDer == extremoDerec.getIzquierdo() || tableroDer == extremoDerec.getDerecho()) {
                if (extremoDerec.getIzquierdo() != tableroDer) {
                    int bkup = extremoDerec.getDerecho();
                    extremoDerec.setDerecho(extremoDerec.getIzquierdo());
                    extremoDerec.setIzquierdo(bkup);
                    extremoDerec.darVuelta(true); // marcamos la ficha para luego darla vuelta cuando repartamos nuevamente.
                }
            } else {
                throw new FichaIncorrecta();
            }
        }
        extremosIgualesDer(); // checkeo si hay extremos iguales antes de agregar la ficha.
        this.extremoDerec = extremoDerec;
        extremoDerec.setDerecho(true);
        fichas.add(extremoDerec);
        colocarVertical(extremoDerec); // chequeo si la ficha tiene que ser ubicada de manera vert.
    }

    /**
     * Permite determinar si el tablero solo tiene la primera ficha jugada.
     * @return verdadero si el tablero tiene solo la primera ficha, falso caso contrario.
     */
    public boolean tableroIniciado() {
        return (extremoIzq.getIzquierdo() == extremoDerec.getIzquierdo()) && (extremoIzq.getDerecho() == extremoDerec.getDerecho());
    }

    public ArrayList<IFicha> getFichas() {
        return fichas;
    }

    private void extremosIgualesIzq() {
        if (extremoIzq == extremoDerec) {
            fichas.remove(extremoIzq);
        }
    }

    private void extremosIgualesDer() {
        if (extremoIzq == extremoDerec) {
            fichas.remove(extremoDerec);
        }
    }

    public void resetearTablero() {
        extremoIzq = null;
        extremoDerec = null;
        resetearArrayExtremos();
    }

    // Reseteo los extremos.
    private void resetearArrayExtremos() {
        int tamanio = extremosJugados.length;
        for (int i = 0; i < tamanio; i++) {
            extremosJugados[i] = 0;
        }
    }

    // agrega una ficha en el extremo izquierdo.
    public void setExtremoIzq(IFicha extremoIzq) throws FichaIncorrecta {
        if (this.extremoIzq != null) {
            int tableroIzq = this.extremoIzq.getIzquierdo();
            if (tableroIzq == extremoIzq.getIzquierdo() || tableroIzq == extremoIzq.getDerecho()) {
                if (extremoIzq.getDerecho() != tableroIzq) {
                    int bkup = extremoIzq.getIzquierdo();
                    extremoIzq.setIzquierdo(extremoIzq.getDerecho());
                    extremoIzq.setDerecho(bkup);
                    extremoIzq.darVuelta(true); // marcamos la ficha para luego darla vuelta cuadno la printeamos.
                }
            } else {
                throw new FichaIncorrecta();
            }
        }
        extremosIgualesIzq();
        this.extremoIzq = extremoIzq;
        extremoIzq.setIzquierdo(true);
        fichas.addFirst(extremoIzq);
        colocarVertical(extremoIzq); // chequeo si la ficha tiene que ser ubicada de manera vert.
    }

    // Incrementa la cantidad de veces que un extremo ha sido jugado.
    public void incrementarExtremo(int extremo) {
        extremosJugados[extremo] += 1;
    }

    // Detecta el cierre del juego, sucede cuando un extremo se juega el maximo cantidad de veces
    // y coincide con los extremos.
    public boolean detectarCierre() {
        int tamanio = extremosJugados.length;
        int i = 0;
        boolean cierre = false;
        while (i < tamanio && !cierre) {
            if (extremosJugados[i] == MAXIMO &&
                    (i == extremoIzq.getIzquierdo() || i == extremoDerec.getDerecho()))
                cierre = true;
            i++;
        }
        return cierre;
    }

    // si se lleno el tablero, se coloca vertical.
    private void colocarVertical(IFicha ficha) {
        if (fichas.size() > cantFichasMaxTab) {
            ficha.setVertical(true);
        }
    }
}
