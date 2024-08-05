package modelo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class EventoTurnoJugadores implements Serializable {
    @Serial
    private static final long serialVersionUID = -1912765850828224632L;
    private Evento evento;
    private IJugador turno;
    private List<IJugador> jugadores;

    public EventoTurnoJugadores() {
    }

    public EventoTurnoJugadores(Evento evento, IJugador turno, List<IJugador> jugadores ) {
        this.evento = evento;
        this.jugadores = jugadores;
        this.turno = turno;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public List<IJugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<IJugador> jugadores) {
        this.jugadores = jugadores;
    }

    public IJugador getTurno() {
        return turno;
    }

    public void setTurno(IJugador turno) {
        this.turno = turno;
    }
}
