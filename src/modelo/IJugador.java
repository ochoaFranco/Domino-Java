package modelo;

import modelo.Ficha;
import modelo.exceptions.FichaIncorrecta;

import java.util.ArrayList;
import java.util.List;

public interface IJugador {
    int getId();
    String getNombre();
    IFicha getUltimaFicha();
    List<IFicha> getFichas();
    boolean tengoDobles();
    public IFicha fichaSimpleMasAlta();
    IFicha fichaDobleMayor();
    void setMano(boolean mano);
    boolean getMano();
    void colocarFicha(IFicha ficha, String extremo, Tablero tablero) throws FichaIncorrecta;
    void recibirFicha(IFicha ficha);
    int contarPuntosFicha();
    void sumarPuntos(int puntos);
    int getPuntos();
}
