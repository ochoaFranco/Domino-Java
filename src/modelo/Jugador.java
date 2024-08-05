package modelo;

import modelo.exceptions.FichaIncorrecta;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Jugador implements IJugador, Serializable {
    @Serial
    private static final long serialVersionUID = 1492707030087004443L;

    private String nombre;
    private final List<IFicha> fichas;
    private boolean mano = false;
    private int puntos = 0;
    private static int ID = 0;
    private int id;
    
    public Jugador(String nombre) {
        this.nombre = nombre;
        fichas = new ArrayList<>();
        id = Jugador.ID++;
    }
    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public int getPuntos() {
        return puntos;
    }

    public void recibirFicha(IFicha ficha) {
        fichas.add(ficha);
    }

    @Override
    public int contarPuntosFicha() {
        int puntosFicha = 0;
        for (IFicha f: fichas) {
            puntosFicha += f.getIzquierdo() + f.getDerecho();
        }
        return puntosFicha;
    }

    public void sumarPuntos(int puntos) {
        this.puntos += puntos;
    }

    public boolean esMano() {
        return mano;
    }

    public IFicha getUltimaFicha() {
        return fichas.get(fichas.size() - 1);
    }
    @Override
    public List<IFicha> getFichas() {
        return fichas;
    }

    public void colocarFicha(IFicha ficha, String extremo, Tablero tablero) throws FichaIncorrecta {
        if (extremo.equalsIgnoreCase("i")) {
            tablero.setExtremoIzq(ficha);
        } else if (extremo.equalsIgnoreCase("d")) {
            tablero.setExtremoDerec(ficha);
        }
        fichas.remove(ficha);
    }

    public IFicha fichaDobleMayor() {
        IFicha dobleMayor = new Ficha(-1, -1);
        for (IFicha f : fichas) {
            if (f.esFichaDoble() && (f.getIzquierdo() > dobleMayor.getIzquierdo() && f.getDerecho() > dobleMayor.getDerecho())) {
                dobleMayor = f;
            }
        }
        return dobleMayor;
    }

    public IFicha fichaSimpleMasAlta() {
        IFicha fichaComunMasAlta = new Ficha(-1, -1);
        for (IFicha f : fichas) {
            if (f.getIzquierdo() > fichaComunMasAlta.getIzquierdo() ||  f.getDerecho() > fichaComunMasAlta.getDerecho()) {
                fichaComunMasAlta = f;
        }
    }
    return fichaComunMasAlta;
}

    public boolean tengoDobles() {
        boolean algunDoble = false;
        int i = 0;
        while (i < fichas.size() && !algunDoble) {
            if (fichas.get(i).esFichaDoble()) {
                algunDoble = true;
            }
            i++;
        }
        return algunDoble;
    }

    @Override
    public String toString() {
        return "Jugador{" +
                "fichas=" + fichas +
                ", nombre='" + nombre + '\'' +
                ", mano=" + mano +
                ", puntos=" + puntos +
                ", id=" + id +
                '}';
    }

    public void setMano(boolean mano) {
        this.mano = mano;
    }

    public boolean getMano() {
        return mano;
    }
}
