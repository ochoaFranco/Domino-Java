package modelo;

import ar.edu.unlu.rmimvc.observer.ObservableRemoto;
import modelo.exceptions.FichaIncorrecta;
import modelo.exceptions.FichaInexistente;
import ar.edu.unlu.rmimvc.observer.IObservadorRemoto;
import utils.Serializador;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;

public class Domino extends ObservableRemoto implements IDomino, Serializable {
    @Serial
    private static final long serialVersionUID = 4058236233430914018L;
    private List<IJugador> jugadores;
    private List<IFicha> fichas;
    private int LIMITEPUNTOS;
    private int turno;
    private Pozo pozo;
    private static IFicha primeraFicha;
    private static IJugador jugadorMano = null;
    private Queue<Integer> colaTurnos = new LinkedList<>();
    private static IDomino instancia;
    private int cantidadJugadores = 0;
    private final int RANKING = 5;
    private final IJugador[] rankCincoMejores = new IJugador[RANKING];
    private static Serializador serializadorRanking = new Serializador("ranking.dat");
    private AdministradorJugadores adminJugadores;
    private Tablero tablero;

    public static IDomino getInstancia() throws RemoteException {
        if (instancia == null) {
            instancia = new Domino();
        }
        return instancia;
    }

    private Domino() throws RemoteException {
        jugadores = new ArrayList<>();
        adminJugadores = new AdministradorJugadores(jugadores);
        fichas = new ArrayList<>();
        inicializarFichas();
        Collections.shuffle(fichas); // mezcla las fichas.
        pozo = new Pozo(fichas);
        tablero = new Tablero();
    }

    public static IFicha getPrimeraFicha() throws RemoteException {
        return primeraFicha;
    }

    public static void setPrimeraFicha(IFicha primeraFicha) {
        Domino.primeraFicha = primeraFicha;
    }

    @Override
    public int getTurno() throws RemoteException {
        return turno;
    }

    public static IJugador getJugadorMano() throws RemoteException {
        return Domino.jugadorMano;
    }

    public static void setJugadorMano(IJugador jugMano) throws RemoteException {
        Domino.jugadorMano = jugMano;
    }

    @Override
    public void setTotalJugadores(int cantidadJugadores) throws RemoteException {
        this.cantidadJugadores = cantidadJugadores;
    }
    @Override
    public void desconectarJugador(int idJugador) throws RemoteException {
        IJugador jugador = getJugadorID(idJugador);
        jugadores.remove(jugador);
        colaTurnos.remove(idJugador);
    }

    @Override
    public int conectarJugador(String nombre) throws RemoteException {
        IJugador jugador = new Jugador(nombre);
        jugadores.add(jugador);
        colaTurnos.offer(jugador.getId());
        return jugador.getId();
    }

    @Override
    // Comprueba si existe el jugador.
    public int existeJugador(String nombre) throws RemoteException {
        return adminJugadores.existeJugador(nombre);
    }

    @Override
    public List<IJugador> getJugadores() throws RemoteException {
        return jugadores;
    }

    @Override
    public int getLIMITEPUNTOS() throws RemoteException {
        return LIMITEPUNTOS;
    }

    @Override
    public void cerrar(IObservadorRemoto controlador, int usuarioId) throws RemoteException {
        this.removerObservador(controlador);
        this.desconectarJugador(usuarioId);
    }

    /** Inicializa un conjunto de fichas para el juego
     * desde (0, 0) hasta (6, 6)*/
    @Override
    public void inicializarFichas() throws RemoteException {
        for (int i = 0; i <= 6; i++) {
            for (int j = i; j <= 6; j++) {
                Ficha ficha = new Ficha(i, j);
                fichas.add(ficha);
            }
        }
    }

    @Override
    public boolean esTableroIniciado() throws RemoteException {
        return tablero.tableroIniciado();
    }

    // Persiste la partida cuando se desconecta un jugador.
    @Override
    public void persistirPartida() throws RemoteException {
        String nombres = adminJugadores.nombreJugadores(jugadores);
        AdministradorPartidas.agregarPartida(new Partida(this, nombres));
        notificarObservadores(Evento.JUGADOR_DESCONECTADO);

    }

    @Override
    public void cargarPartida() throws RemoteException {
        notificarObservadores(Evento.CARGAR_PARTIDA);
    }

    @Override
    public void setTotalPuntos(int puntos) throws RemoteException{
        LIMITEPUNTOS = puntos;
    }

    // se utiliza para reiniciar las rondas sin necesidad de resetear los puntos.
    @Override
    public void iniciarJuego() throws RemoteException {
        repartir();
        determinarJugadorMano();
        determinarJugadorTurno();
        EventoFichaJugador eventoFichaJugador = new EventoFichaJugador(Evento.INICIAR_JUEGO, primeraFicha, jugadorMano);
        notificarObservadores(eventoFichaJugador);
    }

    @Override
    public Pozo getPozo() throws RemoteException {
        return pozo;
    }

    @Override
    public void setPozo(Pozo pozo) throws RemoteException {
        this.pozo = pozo;
    }

    @Override
    public Tablero getTablero() throws RemoteException {
        return tablero;
    }

    @Override
    public void setTablero(Tablero tablero) throws RemoteException {
        this.tablero = tablero;
    }

    public int getCantidadJugadores() throws RemoteException {
        return cantidadJugadores;
    }

    // Reinicia el juego cuando todos los jugadores se desconectaron.
    @Override
    public void reniciarJuego() throws RemoteException {
        jugadores = new ArrayList<>();
        fichas = new ArrayList<>();
        cantidadJugadores = 0;
        LIMITEPUNTOS = 0;
        inicializarFichas();
        Collections.shuffle(fichas); // mezcla las fichas.
        pozo = new Pozo(fichas);
        tablero.resetearTablero(); // limpio las fichas del tablero.
        tablero.getFichas().clear();
    }

    @Override
    public IJugador getJugadorID(int id) throws RemoteException {
        return adminJugadores.buscarJugadorPorID(id);
    }

    // Logica principal del juego.
    @Override
    public void realizarJugada(int extremIzq, int extremDerec, String extremo) throws FichaInexistente, FichaIncorrecta, RemoteException {
        assert colaTurnos.peek() != null;
        IFicha ficha = buscarFicha(extremIzq, extremDerec, getJugadorID(colaTurnos.peek()));
        if (ficha == null) throw new FichaInexistente();
        IJugador jugador = getJugadorID(colaTurnos.peek());
        jugador.colocarFicha(ficha, extremo, tablero);
        // Aumentamos la cantidad de veces que se jugo el extremo
        tablero.incrementarExtremo(extremIzq);
        tablero.incrementarExtremo(extremDerec);
        jugador = getJugadorID(colaTurnos.poll()); // desencolo al jugador del primer turno.
        colaTurnos.offer(jugador.getId()); // lo vuelvo a encolar al final.
        ArrayList<IFicha> fichastablero = tablero.getFichas();
        // clase compuesta.
        EventoFichasTablero evFichastablero = new EventoFichasTablero(Evento.ACTUALIZAR_TABLERO, fichastablero);
        // dermino si el jugador jugo todas sus fichas.
        if (adminJugadores.jugadorJugoTodasSusFichas(adminJugadores.buscarJugadorPorID(turno))) {
            contarPuntosJugadores();
            determinarSiJugadorGano();
        } else if (tablero.detectarCierre()) {
            notificarObservadores(Evento.CIERRE_JUEGO);
            casoCierre();
        } else {
            determinarJugadorTurno();
            notificarObservadores(evFichastablero);
        }
    }

    // Determina quien es el jugador del siguiente turno.
    @Override
    public void determinarJugadorTurno() throws RemoteException {
        turno = colaTurnos.peek();
    }

    // robo fichas del pozo y actualizo la mano.
    @Override
    public void robarFichaPozo() throws RemoteException {
        IJugador jugador = adminJugadores.buscarJugadorPorID(turno);
        IFicha ficha = pozo.sacarFicha();
        if (ficha == null) {
            pasarTurno();
        } else {
            EventoJugador eventoJugador = new EventoJugador(Evento.CAMBIO_FICHAS_JUGADOR, jugador);
            jugador.recibirFicha(ficha);
            notificarObservadores(eventoJugador);
        }
    }

    // reparte las fichas a todos los jugadores.
    private void repartir() {
        for (IJugador j : jugadores) {
            for (int i = 0; i < 7; i++) {
                IFicha ficha = pozo.sacarFicha();
                if (ficha != null) {
                    j.recibirFicha(ficha);
                }
            }
        }
    }

    private void determinarJugadorMano() throws RemoteException {
        adminJugadores.determinarJugadorMano(jugadores);
        // agrego al tablero las fichas.
        try {

            seteartablero(primeraFicha);
        } catch (FichaIncorrecta f) {
            throw new RuntimeException("ficha incorrecta!!!");
        }
        moverJugFinalTurno();
    }

    private void seteartablero(IFicha ficha) throws FichaIncorrecta {
        tablero.setExtremoDerec(ficha);
        tablero.setExtremoIzq(ficha);
    }

    // mueve el jugdor al final del turno en el caso de que ya haya tirado.
    private void moverJugFinalTurno() throws RemoteException {
        if (getJugadorID(colaTurnos.peek()) == jugadorMano) {
            IJugador jugador = getJugadorID(colaTurnos.poll());
            colaTurnos.offer(jugador.getId());
        }
    }

    // cuento los puntos de las fichas de todos lo jugadores.
    private void contarPuntosJugadores() {
        int puntosTotal = 0;
        for (IJugador j : jugadores) {
            puntosTotal += j.contarPuntosFicha();
        }
        adminJugadores.buscarJugadorPorID(turno).sumarPuntos(puntosTotal);
    }

    // establece el ganador de la partida.
    private void detectarJugadorGanadorCierre() {
        IJugador ganador = null;
        int puntos = 10000;
        int temp = 0;
        for (IJugador j : jugadores) {
            for (IFicha f : j.getFichas()) {
                temp += f.getIzquierdo() + f.getDerecho();
            }
            if (temp < puntos) {
                puntos = temp;
                ganador = j;
            }
        }
        turno = ganador.getId(); // marcamos al ganador como el jugador del turno para dsp contar los puntos.
    }

    // Notifica a los observadores si gano el jugador, caso contrario reinicia la ronda.
    private void determinarSiJugadorGano() throws RemoteException {
        if (adminJugadores.buscarJugadorPorID(turno).getPuntos() >= LIMITEPUNTOS) {
            EventoJugador eventoJugador = new EventoJugador(Evento.FIN_DEL_JUEGO, getJugadorID(turno));
            adminJugadores.agregarjugadorRanking(rankCincoMejores, turno);
            persistirJugador();
            notificarObservadores(eventoJugador);
        } else {
            EventoTurnoJugadores eventoTurnoJugadores = new EventoTurnoJugadores(Evento.CAMBIO_RONDA, adminJugadores.buscarJugadorPorID(turno), jugadores);
            notificarObservadores(eventoTurnoJugadores); // jugador que domino la ronda mas todos los jugadores.
            reiniciarRonda();
        }
    }

    // Persiste los jugadores para luego mostrar un ranking.
    private void persistirJugador() {
        // escribo en el header del archivo.
        if (rankCincoMejores[0] != null) {
            serializadorRanking.writeOneObject(rankCincoMejores[0]);
            int i = 1;
            boolean parar = false;
            while (i < rankCincoMejores.length && !parar) {
                if (rankCincoMejores[i] == null)
                    parar = true;
                else
                    serializadorRanking.addOneObject(rankCincoMejores[i]);
                i++;
            }
        }
    }

    // lee el archivo retornando los 5 mejores del ranking.
    public IJugador[] getRankCincoMejores() throws RemoteException {
        Object[] recuperado = serializadorRanking.readObjects();
        if (recuperado != null) {
            for (int i = 0; i < recuperado.length; i++) {
                rankCincoMejores[i] = (IJugador) recuperado[i];
            }
        }
        return rankCincoMejores;
    }

    private void reiniciarRonda() throws RemoteException {
        juntarFichastablero();
        adminJugadores.juntarFichasJugadores(pozo);
        Collections.shuffle(pozo.getFichas());
        tablero.resetearTablero(); // limpio las fichas del tablero.
        iniciarJuego();
    }

    // Junta las fichas del tablero y las agrega al pozo.
    private void juntarFichastablero() {
        List<IFicha> tableroFichas = tablero.getFichas();
        for (IFicha f : tableroFichas) {
            IFicha ficha;
            if (f.isDadaVuelta()) {
                ficha = new Ficha(f.getDerecho(), f.getIzquierdo());
            } else {
                ficha = new Ficha(f.getIzquierdo(), f.getDerecho());
            }
            ficha.darVuelta(false);
            pozo.agregarFicha(ficha);
        }
        tablero.getFichas().clear(); // saco las fichas del tablero.
    }

    // Detecta si los jugadores no pueden jugar porque están bloqueados.
    private void casoCierre() throws RemoteException {
        detectarJugadorGanadorCierre();
        contarPuntosJugadores();
        determinarSiJugadorGano();
    }

    // Busca la ficha a tirar dentro del poll de fichas del jugador.
    private IFicha buscarFicha(int extremIzq, int extemDer, IJugador jug) {
        List<IFicha> fichas = jug.getFichas();
        IFicha ficha = null;
        for (IFicha f : fichas) {
            if (f.getIzquierdo() == extremIzq && f.getDerecho() == extemDer) {
                ficha = f;
                break;
            }
        }
        return ficha;
    }

    // paso el turno, desencolandolo del frente y encolandolo en el final.
    private void pasarTurno() throws RemoteException {
        IJugador jugador = getJugadorID(colaTurnos.poll());
        colaTurnos.offer(jugador.getId());
        determinarJugadorTurno(); // actualizo el turno
        EventoJugador eventoJugador = new EventoJugador(Evento.PASAR_TURNO, getJugadorID(turno));
        notificarObservadores(eventoJugador);
    }
}