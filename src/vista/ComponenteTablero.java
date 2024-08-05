package vista;
import modelo.IFicha;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ComponenteTablero extends JPanel {
    private final List<JPanel> paneles = new ArrayList<>();
    private final int MAX_VERTICALES = 2;
    private final int MAX_VERTICALES_DER = 3;
    private int offset = 0;
    private int offsetDerecha = 0;
    private boolean agregado = false;
    // flags para agregar fichas en los paneles de abajo.
    private boolean agregadoHorizontalAbajo = false;
    private boolean turnoFichaVerticalAbajo = false;
    private boolean turnoFichaHorizontalAbajo = false;
    // paneles del tablero.
    private final JPanel PCentral = new JPanel();
    private final JPanel PVerticalIzq = new JPanel();
    private final JPanel  PVerticalIzq2 = new JPanel();
    private final JPanel PHorizontalAbajo1 = new JPanel();
    private final JPanel PHorizontalAbajo2 = new JPanel();
    private final JPanel PVerticalDer = new JPanel();
    private final JPanel PVerticalAbajo = new JPanel();
    private final JPanel PHorizontalAbajoDerecho = new JPanel();
    private final JPanel PHorizontalArriba = new JPanel();;

    public ComponenteTablero() {
        // caracteristicas del contenedor.
        setLayout(null);
        setSize(1200,300);
        setOpaque(false);
        agregarPaneles();

        // Caracteristicas panel central.
        panelCentral();

        // Caracteristicas panel vertical derecho
        panelVerticalDer();

        // Caracteristicas panel vertical izquierdo
        panelVerticalIzq();

        // Caracteristicas panel vertical izquierdo
        panelVerticalIzq2();

        // Caracteristicas panel horizontal arriba.
        panelHorizontalArriba();

        // Caracteristicas panel horizontal abajo.
        panelHorizontalAbajo1();

        panelHorizontalAbajo2();

        // Caracteristicas panel vertical abajo.
        panelVerticalAbajo();

        panelHorizontalAbajoDerecho();
    }

    // agrega todos los paneles a una lista.
    private void agregarPaneles() {
        paneles.add(PCentral);
        paneles.add(PVerticalIzq);
        paneles.add(PVerticalIzq2);
        paneles.add(PHorizontalAbajo1);
        paneles.add(PHorizontalAbajo2);
        paneles.add(PVerticalDer);
        paneles.add(PVerticalAbajo);
        paneles.add(PHorizontalAbajoDerecho);
        paneles.add(PHorizontalArriba);
    }
    // // Se agrega el panel central.
    private void panelCentral() {
        PCentral.setSize(650, 100);
        PCentral.setBounds(100, 100, 650, 100);
        PCentral.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 15));
        PCentral.setOpaque(false);
        PCentral.setBackground(Color.pink);
        add(PCentral);
    }
    // Se agrega el panel vertical derecho.
    private void panelVerticalDer() {
        PVerticalDer.setSize(100, 300);
        PVerticalDer.setBounds(600, 166, 100, 150);
        PVerticalDer.setBackground(Color.BLACK);
        PVerticalDer.setLayout(new BoxLayout(PVerticalDer, BoxLayout.Y_AXIS));
        PVerticalDer.setOpaque(false);
        add(PVerticalDer);
    }
    // Se agrega el panel horizontal de arriba
    private void panelHorizontalArriba() {
        PHorizontalArriba.setSize(100, 300);
        PHorizontalArriba.setBounds(147, 0, 1200, 120);
        PHorizontalArriba.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 15));
        PHorizontalArriba.setOpaque(false);
        PHorizontalArriba.setBackground(Color.BLUE);
        add(PHorizontalArriba);
    }

    // Se agregan los paneles verticales de la izquierda.
    private void panelVerticalIzq() {
        PVerticalIzq.setLayout(new BoxLayout(PVerticalIzq, BoxLayout.Y_AXIS));
        PVerticalIzq.setSize(50, 50);
        PVerticalIzq.setBounds(100, 65, 50, 50);
        PVerticalIzq.setOpaque(false);
        PVerticalIzq.setBackground(Color.BLACK);
        add(PVerticalIzq);
    }

    private void panelVerticalIzq2() {
        PVerticalIzq2.setLayout(new BoxLayout(PVerticalIzq2, BoxLayout.Y_AXIS));
        PVerticalIzq2.setSize(50, 50);
        PVerticalIzq2.setBounds(100, 12, 50, 50);
        PVerticalIzq2.setOpaque(false);
        PVerticalIzq2.setBackground(Color.white);
        add(PVerticalIzq2);
    }

    private void panelVerticalAbajo() {
        PVerticalAbajo.setLayout(new BoxLayout(PVerticalAbajo, BoxLayout.Y_AXIS));
        PVerticalAbajo.setSize(50, 50);
        PVerticalAbajo.setBounds(500, 320, 50, 50);
        PVerticalAbajo.setOpaque(false);
        PVerticalAbajo.setBackground(Color.PINK);
        add(PVerticalAbajo);
    }

    private void panelHorizontalAbajo1() {
        PHorizontalAbajo1.setLayout(new BoxLayout(PHorizontalAbajo1, BoxLayout.X_AXIS));
        PHorizontalAbajo1.setSize(50, 50);
        PHorizontalAbajo1.setBounds(550, 270, 50, 50);
        PHorizontalAbajo1.setOpaque(false);
        PHorizontalAbajo1.setBackground(Color.MAGENTA);
        add(PHorizontalAbajo1);
    }

    private void panelHorizontalAbajo2() {
        PHorizontalAbajo2.setLayout(new BoxLayout(PHorizontalAbajo2, BoxLayout.X_AXIS));
        PHorizontalAbajo2.setSize(50, 50);
        PHorizontalAbajo2.setBounds(500, 270, 50, 50);
        PHorizontalAbajo2.setOpaque(false);
        PHorizontalAbajo2.setBackground(Color.yellow);
        add(PHorizontalAbajo2);
    }

    private void panelHorizontalAbajoDerecho() {
        PHorizontalAbajoDerecho.setSize(100, 300);
        PHorizontalAbajoDerecho.setBounds(540, 312, 1000, 120);
        PHorizontalAbajoDerecho.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 15));
        PHorizontalAbajoDerecho.setOpaque(false);
        PHorizontalAbajoDerecho.setBackground(Color.BLUE);
        add(PHorizontalAbajoDerecho);
    }

    // Dada una ficha, la agrega a la posicion correspondiente del tablero.
    public void agregarFicha(VistaFicha ficha) {
        IFicha f = ficha.getFicha();
        if (!f.isVertical()) {
            agregarFichasCentrales(ficha);
        } else {
            if (f.isDerecho()) {
                if (offsetDerecha < MAX_VERTICALES_DER)
                    agregarFichasVertDerechas(ficha);
                else
                    agregarFichasHorizontalesAbajo(ficha);
            } else {
                if (offset < MAX_VERTICALES)
                    agregarFichasVerticalesIzquierdas(ficha);
                else
                    agregarFichasHorizontalesArriba(ficha);
            }
        }
    }

    // Permite rotar correctamente las fichas del panel superior horizontal.
    public boolean rotarHorizontalesArriba() {
        return PVerticalIzq2.getComponentCount() > 0 && offset >=2;
    }

   // Permite rotar correctamente las fichas del panel inferior horizontal.
    public boolean rotarHorizontalesAbajo() {
        return offsetDerecha >= MAX_VERTICALES_DER && PVerticalDer.getComponentCount() == MAX_VERTICALES_DER;
    }

    // Permite rotar correctamente la ficha vertical inferior.
    public boolean rotarVerticalesAbajo() {
        return turnoFichaVerticalAbajo;
    }

    // Permite rotar correctamente la ficha horizontal inferior derecha.
    public boolean rotarHorizontalAbajoDerecha() {
        return turnoFichaHorizontalAbajo;
    }

    // agrega las fichas horizontales arriba.
    private void agregarFichasHorizontalesArriba(VistaFicha ficha) {
        PHorizontalArriba.add(ficha);
        revalidate();
        repaint();
    }

    // agrega las fichas horizontales abajo a la derecha.
    private void agregarFichasHorizontalesAbajo(VistaFicha ficha) {
        if (!agregadoHorizontalAbajo) {
            PHorizontalAbajo1.add(ficha);
            agregadoHorizontalAbajo = true;
        } else if (!turnoFichaVerticalAbajo) {
            PHorizontalAbajo2.add(ficha);
            turnoFichaVerticalAbajo = true;
        } else if (!turnoFichaHorizontalAbajo){
            PVerticalAbajo.add(ficha);
            turnoFichaHorizontalAbajo = true;
        } else {
            PHorizontalAbajoDerecho.add(ficha);
        }
        revalidate();
        repaint();
    }

    // agrega las fichas centrales.
    private void agregarFichasCentrales(VistaFicha ficha) {
        PCentral.add(ficha);
        revalidate();
        repaint();
    }

    // agrega las fichas verticales derechas.
    private void agregarFichasVertDerechas(VistaFicha ficha) {
        PVerticalDer.add(ficha);
        offsetDerecha += 1;
        revalidate();
        repaint();
    }
    // agrega las fichas verticales izquierdas.
    private void agregarFichasVerticalesIzquierdas(VistaFicha ficha) {
        if (!agregado) {
            PVerticalIzq.add(ficha, 0);
            agregado = true;
        } else
            PVerticalIzq2.add(ficha, 0);
        revalidate();
        repaint();
        offset += 1;
    }

    // Resetea todos los flags y limpia los jpanels para evitar fichas duplicadas.
    public void limpiarFicha() {
        offset = 0; // reseteo offset.
        agregado = false;
        agregadoHorizontalAbajo = false;
        turnoFichaVerticalAbajo = false;
        turnoFichaHorizontalAbajo = false;
        offsetDerecha = 0;
        // elimino los componentes de los paneles.
        for (JPanel jp : paneles)
                jp.removeAll();
        revalidate();
        repaint();
    }
}
