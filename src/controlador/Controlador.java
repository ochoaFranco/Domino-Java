package controlador;

import ar.edu.unlu.rmimvc.cliente.IControladorRemoto;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;
import modelo.*;
import modelo.IJugador;
import modelo.exceptions.FichaIncorrecta;
import modelo.exceptions.FichaInexistente;
import vista.IVista;

import java.rmi.RemoteException;
import java.util.List;

public class Controlador implements IControladorRemoto {
    private IVista vista;
    private IDomino modelo;
    private int jugador;

    public Controlador() {
    }

    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T t) throws RemoteException {
        this.modelo = (IDomino) t;
    }

    public void setVista(IVista vista) {
        this.vista = vista;
    }

    public void conectarJugador(String nombre) {
        try {
            jugador = this.modelo.conectarJugador(nombre);
        } catch (RemoteException e) {
            vista.mostrarMensaje("Ha ocurrido un error.");
        }
    }

    public void setJugador(int jugador) {
        this.jugador = jugador;
    }

    // Determina si ya existe el jugador.
    public int existeJugador(String nombre) {
        try {
            return modelo.existeJugador(nombre);
        } catch (RemoteException e) {
            vista.mostrarMensaje("Ha ocurrido un error.");
            return -1;
        }
    }

    public void desconectarJugador() {
        try {
            modelo.cerrar(this, jugador);
            // reinicio el modelo si no hay mas jugadores.
            if (modelo.getJugadores().isEmpty())
                modelo.reniciarJuego();
        } catch (RemoteException e) {
            vista.mostrarMensaje("Ha ocurrido un error.");
        }
    }

    // Permite persistir las partidas.
    public void persistirPartida() {
        try {
            modelo.persistirPartida();
        } catch (RemoteException e) {
            vista.mostrarMensaje("Ha ocurrido un error.");
        }
    }

    // Recibe los puntos e inicia el juego si es el creador.
    public void iniciarJuego(int puntos, int cantJugadores) {
        try {
            int cantidadJugadores = modelo.getJugadores().size();
            modelo.setTotalJugadores(cantJugadores);
            modelo.setTotalPuntos(puntos);
            if (cantidadJugadores == cantJugadores)
                modelo.iniciarJuego();
        } catch (RemoteException e) {
            vista.mostrarMensaje("Ha ocurrido un error.");
        }
    }

    // Se conecta la juego si no es el creador.
    public void iniciarJuego() {
        try {
            if (modelo.getJugadores().size() == modelo.getCantidadJugadores())
                modelo.iniciarJuego();
        } catch (RemoteException e) {
            vista.mostrarMensaje("Ha ocurrido un error.");
        }
    }

    public void gestionarTurnos(int extremIzq, int extremDer, String extremo) throws FichaInexistente, FichaIncorrecta {
        try {
            modelo.realizarJugada(extremIzq, extremDer, extremo);
        } catch (RemoteException e) {
            vista.mostrarMensaje("Ha ocurrido un error.");
        }
    }

    public void robarFicha() {
        try {
            modelo.robarFichaPozo();
        } catch (RemoteException e) {
            vista.mostrarMensaje("Ha ocurrido un error.");
        }
    }

    // obtiene el ranking de los 5 mejores jugadores.
    public IJugador[] getRanking() {
        try {
            return modelo.getRankCincoMejores();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void cargarPartida() {
        try {
            List<IFicha> fichasTablero = modelo.getTablero().getFichas();
            vista.iniciar();
            vista.limpiarTablero();
            boolean extremosIguales = modelo.esTableroIniciado();
            if (!extremosIguales) {
                vista.mostrarTablero(fichasTablero);
            }
            else {
                vista.mostrarFicha(fichasTablero.getFirst());
            }
            if (modelo.getTurno() == jugador) {
                vista.mostrarBoton();
                vista.mostrarMensaje("Es tu turno, elige la ficha a jugar: \n");
                vista.mostrarFichasJugador(modelo.getJugadorID(modelo.getTurno()));
            } else {
                vista.mostrarFichasJugador(modelo.getJugadorID(jugador));
                IJugador jugTurno = modelo.getJugadorID(modelo.getTurno());
                vista.mostrarMensaje("Turno del jugador: " + jugTurno.getNombre() + "\n");
                vista.ocultarBoton();
            }
        } catch (RemoteException e) {
            vista.mostrarMensaje("Ha ocurrido un error.");
        }
    }

    public void pedirCargaPartida() {
        try {
            modelo.cargarPartida();
        } catch (RemoteException e) {
            vista.mostrarMensaje("Ha ocurrido un error.");
        }
    }

    // indica si el juego ya ha sido creado
    public boolean esJuegoCreado() {
        try {
            return !modelo.getJugadores().isEmpty();
        } catch (RemoteException e ) {
            vista.mostrarMensaje("Ha ocurrido un error.");
            return true;
        }
    }

    public List<IFicha> getFichasJugador(IJugador jugador) {
        return jugador.getFichas();
    }

    @Override
    public void actualizar(IObservableRemoto iObservableRemoto, Object cambios) throws RemoteException {
        if (cambios instanceof EventoFichaJugador) {
            actualizarEventoFichaJugador((EventoFichaJugador) cambios);
        } else if (cambios instanceof EventoJugador) {
            actualizarEventoJugador((EventoJugador) cambios);
        } else if (cambios instanceof EventoTurnoJugadores) {
            actualizarEventoTurnoJugadores((EventoTurnoJugadores) cambios);
        } else if (cambios instanceof EventoFichasTablero) {
            actualizarEventoFichasTablero((EventoFichasTablero) cambios);
        } else if (cambios instanceof Evento) {
            actualizarEstado((Evento) cambios);
        }
    }

    private void actualizarEstado(Evento e) {
        switch (e) {
            case JUGADOR_DESCONECTADO:
                try {
                    if (modelo.getTurno() == jugador)
                        vista.mostrarMensaje("Hasta luego!!!");
                    else
                        vista.mostrarMensaje("Se ha desconectado un jugador!!!");
                    vista.desconectar();
                } catch (RemoteException ex){
                    ex.printStackTrace();
                }
                break;
            case CARGAR_PARTIDA:
                cargarPartida();
                break;
            case CIERRE_JUEGO:
                vista.mostrarMensaje("Jugador Bloqueado");
        }
    }

    // maneja el caso en el que se actualice un evento ficha jugador.
    private void actualizarEventoFichaJugador(EventoFichaJugador cambios) throws RemoteException {
        if ((cambios.getEvento()) == Evento.INICIAR_JUEGO) {
            vista.mostrarFicha(cambios.getFicha());
            vista.iniciar();
            if (modelo.getTurno() == jugador) {
                vista.mostrarBoton();
                vista.mostrarMensaje("Es tu turno, elige una ficha para jugar: \n");
            } else {
                int jugadorTurno = modelo.getTurno();
                IJugador j = modelo.getJugadorID(modelo.getJugadorID(jugadorTurno).getId());
                vista.mostrarMensaje("Turno del jugador: " + j.getNombre() + "\n");
                vista.ocultarBoton();
            }
            IJugador jug = modelo.getJugadorID(jugador);
            vista.mostrarFichasJugador(jug);
        }
    }

    // Actualiza el tablero.
    private void actualizarEventoFichasTablero(EventoFichasTablero cambios) throws RemoteException {
        if (cambios.getEvento() == Evento.ACTUALIZAR_TABLERO) {
            List<IFicha> fichasTablero = cambios.getFichasTablero();
            vista.mostrarTablero(fichasTablero);
            if (modelo.getTurno() == jugador) {
                vista.mostrarBoton();
                vista.mostrarMensaje("Es tu turno, elige la ficha a jugar: \n");
                vista.mostrarFichasJugador(modelo.getJugadorID(jugador));
            } else {
                IJugador jugTurno = modelo.getJugadorID(modelo.getTurno());
                vista.mostrarMensaje("Turno del jugador: " + jugTurno.getNombre() + "\n");
                vista.ocultarBoton();
            }
        }
    }

    // Actualiza el cambio de ronda.
    private void actualizarEventoTurnoJugadores(EventoTurnoJugadores cambios) {
        if ((cambios.getEvento()) == Evento.CAMBIO_RONDA) {
            IJugador ganadorRonda = cambios.getTurno();
            List<IJugador> jugadores = cambios.getJugadores();
            vista.mostrarMensaje("Jugador que domino la ronda: " + ganadorRonda.getNombre() + "\n");
            int puntos;
            try {
                puntos = modelo.getLIMITEPUNTOS();
                vista.mostrarTablaPuntos(jugadores, puntos);
                vista.limpiarTablero();
                vista.mostrarMensaje("Comenzara una nueva ronda..\n");
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Actualiza el caso del fin del juego.
    private void actualizarEventoJugador(EventoJugador cambios) throws RemoteException {
        switch (cambios.getEvento()) {
            case CAMBIO_FICHAS_JUGADOR:
                if (cambios.getJugador().getId() == jugador) {
                    IJugador j = modelo.getJugadorID(jugador);
                    vista.mostrarMensaje("Fichas jugador: " + j.getNombre());
                    vista.mostrarFichasJugador(j);
                }
                break;
            case PASAR_TURNO:
                vista.mostrarMensaje("El pozo no tiene mas fichas.\n");
                if (jugador == cambios.getJugador().getId()) {
                    vista.mostrarBoton();
                    vista.mostrarMensaje("Es tu turno, elige una ficha: \n");
                    vista.mostrarFichasJugador(modelo.getJugadorID(jugador));
                } else {
                    vista.mostrarMensaje("Turno del jugador: " + cambios.getJugador().getNombre());
                    vista.ocultarBoton();
                }
                break;
            case FIN_DEL_JUEGO:
                IJugador jug = modelo.getJugadorID(jugador);
                IJugador ganador = cambios.getJugador();
                vista.limpiarTablero();
                if (ganador.getId() == jugador) {
                    vista.finalizarJuego("Has ganado el juego con " + ganador.getPuntos() + " puntos gracias por jugar al domino!");
                } else {
                    vista.finalizarJuego("El jugador: " + ganador.getNombre() + " ha ganado el juego con " + ganador.getPuntos() + " puntos gracias por jugar al domino!");
                }
                break;
        }
    }
}
