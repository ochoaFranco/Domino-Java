package modelo;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;

public class AdministradorJugadores implements Serializable {
    @Serial
    private static final long serialVersionUID = -5496765246632781179L;
    List<IJugador> jugadores = new ArrayList<>();
    public AdministradorJugadores(List<IJugador> jugadores) {
        this.jugadores = jugadores;
    }

    /**
     * @param nombre nombre del jugador a buscar.
     * @return El ID del jugador encontrado, -1 si no existe.
     */
    public int existeJugador(String nombre) {
        int resultado = -1;
        int i = 0;
        boolean parar = false;
        while (i < jugadores.size() && !parar) {
            if (jugadores.get(i).getNombre().equalsIgnoreCase(nombre)) {
                resultado = jugadores.get(i).getId();
                parar = true;
            }
            i++;
        }

        return resultado;
    }

    // Junta las fichas de los jugadores y las agrega al  pozo.
    public void juntarFichasJugadores(Pozo pozo) {
        for (IJugador j : jugadores) {
            for (IFicha f : j.getFichas()) {
                IFicha ficha;
                if (f.isDadaVuelta()) {
                    ficha = new Ficha(f.getDerecho(), f.getIzquierdo());
                } else {
                    ficha = new Ficha(f.getIzquierdo(), f.getDerecho());
                }
                ficha.darVuelta(false);
                pozo.agregarFicha(ficha);
            }
            j.getFichas().clear(); // vacio la mano del jugador.
        }
    }

    // agrega el ganador al ranking si es que tiene los puntos para agregarse.
    public void agregarjugadorRanking(IJugador[] rankCincoMejores, int turno) {
        IJugador jugador = buscarJugadorPorID(turno);
        if (jugador == null)
            return;
        boolean agregado = false;
        int i = rankCincoMejores.length - 1;
        int tamanio = 0;
        int posicion = posicionLibre(rankCincoMejores);
        if (posicion != -1)
            rankCincoMejores[posicion] = jugador;
        else {
            while (i > tamanio && !agregado) {
                if (jugador.getPuntos() > rankCincoMejores[i].getPuntos()) {
                    rankCincoMejores[i] = jugador;
                    agregado = true;
                }
                i--;
            }
        }
        // ordena el array.
        Arrays.sort(rankCincoMejores, Comparator.nullsLast(Comparator.comparingInt(IJugador::getPuntos).reversed()));
    }

    // Busca una posicion libre en el array.
    private int posicionLibre(IJugador[] jugadores) {
        for (int i = 0; i < jugadores.length; i++) {
            if (jugadores[i] == null)
                return i;
        }
        return -1;
    }

    // dada una ID busca el jugador y lo retorna.
    public IJugador buscarJugadorPorID(int id) {
        for (IJugador j : jugadores) {
            if (j.getId() == id)
                return j;
        }
        return null;
    }

    // determina si el jugador no tiene mas fichas.
    public boolean jugadorJugoTodasSusFichas(IJugador jugador) {
        return jugador.getFichas().isEmpty();
    }

    public void determinarJugadorMano(List<IJugador> jugadores) throws RemoteException {
        List<IJugador> jugadoresConFichasDobles = new ArrayList<>();
        IJugador jugadorFichaSimpleMasAlta = determinarJugadorFichaSimpleMayor(jugadores, jugadoresConFichasDobles);
        IJugador jugMano;
        // seteo el jugador mano y la primera ficha a poner en el tablero.
        if (!jugadoresConFichasDobles.isEmpty()) {
            jugMano = setJugadorMano(jugadoresConFichasDobles);
            Domino.setPrimeraFicha(jugMano.fichaDobleMayor());
        } else {
            try {
                jugadorFichaSimpleMasAlta.setMano(true);
                Domino.setPrimeraFicha(jugadorFichaSimpleMasAlta.fichaSimpleMasAlta());
                Domino.setJugadorMano(jugadorFichaSimpleMasAlta);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        List<IFicha> fichasJugador = Domino.getJugadorMano().getFichas();
        fichasJugador.remove(Domino.getPrimeraFicha());
    }

    /**
     * Permite devolver el nombre de los jugadores de la partida.
     * @param jugadores Lista de jugadores del domino.
     * @return String string de nombres de los jugadores.
     */
    public String nombreJugadores(List<IJugador> jugadores) {
        StringBuilder nombre = new StringBuilder();
        for (IJugador j: jugadores) {
            nombre.append(j.getNombre()).append(" ");
        }
        return nombre.toString();
    }

    /**
     * @param jugadores Jugadores de la partida.
     * @param jugadoresConFichasDobles se guardan los jugadores con fichas dobles.
     * @return IJugador jugador con la ficha simple mayor.
     */
    private IJugador determinarJugadorFichaSimpleMayor(List<IJugador> jugadores, List<IJugador> jugadoresConFichasDobles) {
        int fichaSimpleAlta = -1;
        IJugador jugadorFichaSimpleMasAlta = null;
        for (IJugador j : jugadores) {
            if (j.tengoDobles()) {
                jugadoresConFichasDobles.add(j);
            }
            // determino ficha simple más alta.
            if (j.fichaSimpleMasAlta().getIzquierdo() > fichaSimpleAlta) {
                fichaSimpleAlta = j.fichaSimpleMasAlta().getIzquierdo();
                jugadorFichaSimpleMasAlta = j;
            } else if (j.fichaSimpleMasAlta().getDerecho() > fichaSimpleAlta) {
                fichaSimpleAlta = j.fichaSimpleMasAlta().getIzquierdo();
                jugadorFichaSimpleMasAlta = j;
            }
        }
        return jugadorFichaSimpleMasAlta;
    }

    private IJugador setJugadorMano(List<IJugador> jugadoresConFichasDobles) throws RemoteException {
        IJugador jugMano;
        jugMano = jugadorfichaDobleMasAlta(jugadoresConFichasDobles);
        jugMano.setMano(true);
        Domino.setJugadorMano(jugMano);
        return jugMano;
    }

    private IJugador jugadorfichaDobleMasAlta(List<IJugador> jugadores) {
        IJugador jFichaDobleMasAlta = null;
        int fichaValor = -1;
        for (IJugador j : jugadores) {
            if (j.fichaDobleMayor().getIzquierdo() > fichaValor) {
                fichaValor = j.fichaDobleMayor().getIzquierdo();
                jFichaDobleMasAlta = j;
            }
        }
        return jFichaDobleMasAlta;
    }
}
