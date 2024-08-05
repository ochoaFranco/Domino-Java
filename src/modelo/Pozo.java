package modelo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Pozo implements Serializable {
    @Serial
    private static final long serialVersionUID = -1668574124496387187L;
    private List<IFicha> fichas;

    public Pozo(List<IFicha> fichas) {
        this.fichas = fichas;
    }
    
    public IFicha sacarFicha() {
        if (fichas.isEmpty()) return null;
        return fichas.removeFirst();
    }

    public void agregarFicha(IFicha ficha) {
        fichas.add(ficha);
    }

    public List<IFicha> getFichas() {
        return fichas;
    }
}
