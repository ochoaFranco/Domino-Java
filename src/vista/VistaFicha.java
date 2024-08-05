package vista;

import modelo.IFicha;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VistaFicha extends JLabel {
    private IFicha ficha;
    private boolean isDoble;
    private boolean eventosMouseHabilitados;
    private static IFicha fichaEnMano = null;
    private boolean primeraFicha = false;
    private int anguloRotacion = 0;

    public VistaFicha(IFicha ficha, boolean cambiarTamanio, boolean eventosMouseHabilitados, boolean primeraFicha) {
        this.ficha = ficha;
        this.isDoble = ficha.esFichaDoble();
        this.eventosMouseHabilitados = eventosMouseHabilitados;
        this.primeraFicha = primeraFicha;

        if (eventosMouseHabilitados) {
            agregarListeners(cambiarTamanio);
        }
        cargarImagen();
        setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        setPreferredSize(new Dimension(50,50));
    }

    public void setEventosMouseHabilitados(boolean eventosMouseHabilitados) {
        this.eventosMouseHabilitados = eventosMouseHabilitados;
    }

    public IFicha getFicha() {
        return ficha;
    }

    private void agregarListeners(boolean cambiarTamanio) {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (cambiarTamanio) {
                    setPreferredSize(new Dimension(60, 60)); // incrementa el tamanio
                    setLocation(getX() - 5, getY() - 5); // mueve la ficha.
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setPreferredSize(null); // resetea el tamanio cuando sale.
                setLocation(getX() + 5, getY() + 5); // resetea la posiion
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                VistaGrafica.incrementarClicks();
                // maneja cuando un jugador tiene la ficha en la mano.
                if (!(VistaGrafica.getCantClicks() > 1)) {
                    setVisible(false); // oculta la ficha.
                    VistaFicha.fichaEnMano = ficha;
                    MenuFicha menuFicha = new MenuFicha();
                    MenuFicha.setJugar(true);
                    menuFicha.agregarListeners(VistaFicha.this);
                    menuFicha.iniciar();
                }
            }
        });
    }

    private void cargarImagen() {
        String nombreArchivo;
        if (ficha.isDadaVuelta())
            nombreArchivo = "img/" + ficha.getDerecho() + "-" + ficha.getIzquierdo() + ".png";
        else
            nombreArchivo = "img/" + ficha.getIzquierdo() + "-" + ficha.getDerecho() + ".png";

        // cargar imagen.
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(nombreArchivo));
            setIcon(icon);
        } catch (NullPointerException n) {
            throw new RuntimeException();
        }
    }

    public void setAnguloRotacion(int angulo) {
        this.anguloRotacion = angulo;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.rotate(Math.toRadians(anguloRotacion), getWidth() / 2, getHeight() / 2); // rotar 90 grados.
        super.paintComponent(g2d);
        g2d.dispose();
    }
}
