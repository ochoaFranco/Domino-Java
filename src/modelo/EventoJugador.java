package modelo;

import java.io.Serial;
import java.io.Serializable;

public class EventoJugador implements Serializable {
    @Serial
    private static final long serialVersionUID = 6246732455084063137L;
    private Evento evento;
    private IJugador jugador;

    public EventoJugador() {
    }

    public EventoJugador(Evento evento, IJugador jugador) {
        this.evento = evento;
        this.jugador = jugador;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public IJugador getJugador() {
        return jugador;
    }

    public void setJugador(IJugador jugador) {
        this.jugador = jugador;
    }
}
