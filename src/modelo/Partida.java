package modelo;

import java.io.Serial;
import java.io.Serializable;

public class Partida implements Serializable {
    @Serial
    private static final long serialVersionUID = 3377792848344458649L;
    private Domino domino;
    private static int ID = 0;
    private int id;
    private final String[] jugadores = {"", "", "", ""};
    private final String nombreJugadores;

    public Partida(Domino domino, String nombreJugadores) {
        this.nombreJugadores = nombreJugadores;
        this.domino = domino;
        id = Partida.ID++;
        llenarJugadoresArray();
    }

    /**
     * permite registrar los nombres de los jugadores que jugaron la partida.
     */
    private void llenarJugadoresArray() {
        String[] nombresArray = nombreJugadores.split(" ");
        for (int i = 0; i < nombresArray.length && i < jugadores.length; i++) {
            jugadores[i] = nombresArray[i];
        }
    }

    /**
     * @return Array con los jugadores de la partida.
     */
    public String[] getJugadores() {
        return jugadores;
    }

    public Domino getDomino() {
        return domino;
    }

    public void setDomino(Domino domino) {
        this.domino = domino;
    }

    public static int getID() {
        return ID;
    }

    public static void setID(int ID) {
        Partida.ID = ID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
