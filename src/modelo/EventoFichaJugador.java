package modelo;

import java.io.Serial;
import java.io.Serializable;

public class EventoFichaJugador implements Serializable {
    @Serial
    private static final long serialVersionUID = -6795695403245609016L;
    private Evento evento;
    private IJugador jugador;
    private IFicha ficha;

    public EventoFichaJugador() {
    }

    public EventoFichaJugador(Evento evento, IFicha ficha, IJugador jugador) {
        this.evento = evento;
        this.ficha = ficha;
        this.jugador = jugador;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public IFicha getFicha() {
        return ficha;
    }

    public void setFicha(IFicha ficha) {
        this.ficha = ficha;
    }

    public IJugador getJugador() {
        return jugador;
    }

    public void setJugador(IJugador jugador) {
        this.jugador = jugador;
    }
}
