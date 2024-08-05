package modelo;

import utils.Serializador;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdministradorPartidas implements Serializable {
    @Serial
    private static final long serialVersionUID = -9171934268332987443L;
    private static final List<Partida> partidas = new ArrayList<>();
    private static final Serializador serializadorPartida = new Serializador("partida.dat");

    public AdministradorPartidas() {
    }

    public static void agregarPartida(Partida partida) {
        partidas.add(partida);
        Partida p = partidas.getFirst();
        serializadorPartida.writeOneObject(p);
        serializadorPartida.addOneObject(p);
    }

    /**
     * @param nombre Nickname del jugador
     * @return juego donde se encuentra jugando, o nulo si es un jugador nuevo.
     */
    public static IDomino getPartidaJugador(String nombre) throws RemoteException {
        if (nombre.isEmpty())
            return null;
        Object[] partidas = serializadorPartida.readObjects();
        String[] jugadores;
        if (partidas != null) {
            for (int i = 0; i < partidas.length; i++) {
                Partida partida = ((Partida)partidas[i]);
               jugadores =  partida.getJugadores();
               for (int j = 0; j < jugadores.length; j++) {
                   if (jugadores[j].equalsIgnoreCase(nombre)) {
                       return partida.getDomino();
                   }
               }
            }
        }
        return null;
    }
}
