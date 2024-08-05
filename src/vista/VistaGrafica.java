package vista;

import controlador.Controlador;
import modelo.IFicha;
import modelo.IJugador;
import modelo.exceptions.FichaIncorrecta;
import modelo.exceptions.FichaInexistente;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VistaGrafica extends JFrame implements IVista, MouseListener {
    private final String nombre;
    private static Controlador controlador;
    private final JPanel panel;
    private static IFicha primeraFicha;
    private static int cantClicks = 0;
    private final ComponenteJugadorMano jugadorManoComponente;
    private final ComponenteTablero componenteTablero;
    private final JButton robarBtn;
    private final JButton desconectarseBtn = new JButton("Desconectarse");
    private final JLabel mensaje = new JLabel();
    private final JLabel lblPuntos = new JLabel();
    private JPanel ptsPanel = new JPanel();
    private final List<MouseListener> mouseListenersGuardados = new ArrayList<>();
    private final JLabel[] lblJugadores = new JLabel[4];

    public VistaGrafica(String nombre, Controlador controlador) {
        VistaGrafica.controlador = controlador;
        setTitle("Domino");
        this.nombre = nombre;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 650);
        setResizable(false);

        // desconecta al jugador cuando cierra la ventana.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controlador.desconectarJugador();
                System.exit(0);
            }
        });

        // creo un panel junto con su background
        panel = Lobby.getjPanel("img/tablero.png");
        panel.setLayout(null);

        // Agrego el label de los puntos.
        ptsPanel.add(lblPuntos);

        // Agrego las caracteristicas del panel de puntos.
        ptsPanel.setLayout(new BoxLayout(ptsPanel, BoxLayout.Y_AXIS));
        ptsPanel.setOpaque(false);
        ptsPanel.setBackground(Color.black);
        ptsPanel.setBounds(0,200,450,200);

        // Inicializo los labels de los jugadores.
        inicializarLblsJugadores();

        // agrego el tablero.
        componenteTablero = new ComponenteTablero();
        componenteTablero.setBounds(0,0, 1200, 650);
        panel.add(componenteTablero);

        // agrego robarBtn
        robarBtn = new JButton("Robar");
        robarBtn.setBounds(1000, 500, 100, 20);
        panel.add(robarBtn);

        // Agrego desconectarseBtn
        desconectarseBtn.setBounds(1060, 20, 120, 20);
        panel.add(desconectarseBtn);

        // agrego la seccion de las fichas del jugador.
        jugadorManoComponente = new ComponenteJugadorMano();
        jugadorManoComponente.setBounds(0, 450, 800, 200);
        panel.add(jugadorManoComponente);
        panel.add(ptsPanel);

        // tamanio pantalla
        setLocationRelativeTo(null);
        this.getContentPane().add(panel);
        this.addMouseListener(this);
        // Interaccion con el robarBtn.
        robarBtn.addActionListener(actionEvent -> actualizarManoJugador());
        desconectarseBtn.addActionListener(actionEvent -> controlador.persistirPartida());
    }

    // Inicializo los Jlabels y los agrego al panel.
    private void inicializarLblsJugadores() {
        int tamanio = lblJugadores.length;
        for (int i = 0; i < tamanio; i++) {
            lblJugadores[i] = new JLabel();
            ptsPanel.add(lblJugadores[i]);
        }
    }

    // se roba una ficha.
    private void actualizarManoJugador() {
        controlador.robarFicha();
    }

    public static int getCantClicks() {
        return cantClicks;
    }
    public static void incrementarClicks() {
        VistaGrafica.cantClicks += 1;
    }
    public static void decrementarClicks() {
        VistaGrafica.cantClicks -=1;
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        if ( mensaje != null  && mensaje.equalsIgnoreCase("Jugador Bloqueado")) {
          SwingUtilities.invokeLater(()->JOptionPane.showMessageDialog(null, "Jugador Bloqueado!!!", "Bloqueado", JOptionPane.INFORMATION_MESSAGE));

        } else {
            assert mensaje != null;
            if (mensaje.startsWith("Jugador que domino la ronda:")) {
                SwingUtilities.invokeLater(()->JOptionPane.showMessageDialog(null, mensaje, "Ganador", JOptionPane.INFORMATION_MESSAGE));
            } else {
                int x = 30;
                int y = 400;
                mostrarMensaje(mensaje, x, y);
            }
        }
    }

    public void mostrarMensaje(String msj, int x, int y) {
        mensaje.setText(msj);
        mensaje.setForeground(Color.black);
        mensaje.setFont(new Font("Arial", Font.BOLD, 18));
        mensaje.setBounds(x, y, 800, 40);
        panel.add(mensaje);
        panel.revalidate();
        panel.repaint();
    }

    // funcionalidad encargada de mostrar las fichas del jugador.
    @Override
    public void mostrarFichasJugador(IJugador jugador)  {
        jugadorManoComponente.removeAll();
        List<IFicha> fichas = controlador.getFichasJugador(jugador);

        for (IFicha ficha: fichas) {
            VistaFicha fichaComponente = new VistaFicha(ficha, true, true, false);
            jugadorManoComponente.agregarFichaJugador(fichaComponente);
        }
        panel.revalidate();
        panel.repaint();
    }

    public static void realizarJugada(String extremo, VistaFicha vFicha) {
        IFicha ficha = vFicha.getFicha();
        try {
            VistaGrafica.controlador.gestionarTurnos(ficha.getIzquierdo(), ficha.getDerecho(), extremo);
        } catch (FichaIncorrecta f) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Ficha Incorrecta!!!", "Error", JOptionPane.ERROR_MESSAGE));
            vFicha.setVisible(true);
            VistaGrafica.decrementarClicks();
        } catch (FichaInexistente i) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Espera tu turno!!!", "Error", JOptionPane.ERROR_MESSAGE));
            vFicha.setVisible(true);
            VistaGrafica.decrementarClicks();
        }
    }

//     muestra la primera ficha.
    @Override
    public void mostrarFicha(IFicha ficha) {
        componenteTablero.limpiarFicha();
        VistaGrafica.primeraFicha = ficha;
        VistaFicha f = new VistaFicha(ficha, false, false, true);
        rotarFicha(ficha, f, false, false, false, false, false);
        componenteTablero.agregarFicha(f);
        componenteTablero.revalidate();
        componenteTablero.repaint();
    }

    @Override
    public void mostrarTablero(Object o) {
        limpiarTablero();
        List<IFicha> fichas = (ArrayList<IFicha>) o;
        List<IFicha> fichasVerticales = buscarFichasVerticales(fichas);
        Collections.reverse(fichasVerticales);

        // itero sobre las fichas derechas para agregarlas al tablero.
        for (IFicha f : fichas) {
            VistaFicha vFicha;
            boolean rotar;
            // agrega fichas centrales.
            boolean rotarHorizontAbaj = componenteTablero.rotarHorizontalesAbajo();
            if (!(f.isVertical() && f.isIzquierdo()) && !rotarHorizontAbaj) {
                vFicha = new VistaFicha(f, false, false, false);
                rotar = f.isVertical();
                rotarFicha(f, vFicha, rotar, false, false, false, false);
                componenteTablero.agregarFicha(vFicha);

                // agrega fichas derechas panel horizontal inferior.
            } else if (f.isVertical() && f.isDerecho()) {
                vFicha = new VistaFicha(f, false, false, false);
                rotar = f.isVertical();
                boolean rotarVerticalAbajo = componenteTablero.rotarVerticalesAbajo();
                boolean rotarHorizontalAbajo = componenteTablero.rotarHorizontalesAbajo();
                boolean rotarHorizontalAbajoDer = componenteTablero.rotarHorizontalAbajoDerecha();
                rotarFicha(f, vFicha, rotar, false, rotarHorizontalAbajo,  rotarVerticalAbajo, rotarHorizontalAbajoDer);
                componenteTablero.agregarFicha(vFicha);
            }
        }

        // itero sobre las fichas verticales izquierdas para agregarlas al tablero.
        for (IFicha f : fichasVerticales) {
            VistaFicha vistaFicha = new VistaFicha(f, false, false, false);
            rotarFicha(f, vistaFicha, true, componenteTablero.rotarHorizontalesArriba(),
                    false, false, false);
            componenteTablero.agregarFicha(vistaFicha);
        }
        componenteTablero.revalidate();
        componenteTablero.repaint();
    }

    // Dado una listas de fichas retorna una nueva lista con fichas verticales.
    private List<IFicha> buscarFichasVerticales(List<IFicha> fichas) {
        List<IFicha> fichasVerticales = new ArrayList<>();
        for (IFicha f: fichas) {
            if (f.isVertical() && f.isIzquierdo())
                fichasVerticales.add(f);
        }
        return fichasVerticales;
    }

    // dado una ficha, la rota y la muestra en las coordenadas indicadas.
    private static void rotarFicha(IFicha f,
                                   VistaFicha vistaFicha,
                                   boolean rotar,
                                   boolean rotarHorizontales,
                                   boolean rotarHorizAbajo,
                                   boolean rotarVerticalAbajo,
                                   boolean rotarHorizontalAbajoDer) {
        if (!f.esFichaDoble()) {
            if (!rotar) {
                // roto la ficha dependiendo si esta dada vuelta o no.
                vistaFicha.setAnguloRotacion(f.isDadaVuelta() ? 90 : -90);
            } else {
                if (!rotarHorizAbajo) {
                    // si se debe rotar, se jira la ficha 180 grados.
                    if (f.isDadaVuelta() && !rotarHorizontales)
                        vistaFicha.setAnguloRotacion(180);
                     else if (rotarHorizontales && !f.isDadaVuelta())
                        vistaFicha.setAnguloRotacion(90);
                    else if (f.isDadaVuelta() && rotarHorizontales)
                        vistaFicha.setAnguloRotacion(-90);
                    // rota fichas del panel inferior horizontal
                } else if (!rotarVerticalAbajo) {
                    if (f.isDadaVuelta())
                        vistaFicha.setAnguloRotacion(-90);
                    else
                        vistaFicha.setAnguloRotacion(90);
                    // rotar ficha vertical abajo derecha.
                } else if (!rotarHorizontalAbajoDer) {
                    if (f.isDadaVuelta())
                        vistaFicha.setAnguloRotacion(-180);
                    else
                        vistaFicha.setAnguloRotacion(360);
                    // rota fichas del panel inferior horizontal derecho
                } else {
                    if (f.isDadaVuelta())
                        vistaFicha.setAnguloRotacion(90);
                    else
                        vistaFicha.setAnguloRotacion(-90);
                }
            }
        } else if (rotar) {
            if (!rotarHorizontales && !rotarHorizAbajo)
                vistaFicha.setAnguloRotacion(f.isDadaVuelta() ? 90 : -90);
            else
                vistaFicha.setAnguloRotacion(360);
        }
    }

    public void jugar(int puntos, int cantJugadores) {
        controlador.iniciarJuego(puntos, cantJugadores);
    }

    public void jugar() {
        controlador.iniciarJuego();
    }

    // inicia la vista si es que no lo esta.
    @Override
    public void iniciar() {
        if (!isVisible())
            setVisible(true);
    }

    @Override
    public void mostrarTablaPuntos(Object o, int puntos) {
        List<IJugador> jugadores = (ArrayList<IJugador>) o;
        int yOffset = 290;
        int xOffset = 0;
        int i = 0;
        Border bordeNegro = BorderFactory.createLineBorder(Color.black, 2);
        // Agrego el titulo de los puntos.
        lblPuntos.setBounds(xOffset, 220, 200, 200);
        lblPuntos.setText("JUEGO A " + puntos);
        lblPuntos.setFont(new Font("Arial", Font.BOLD, 22));
        lblPuntos.setOpaque(true);
        lblPuntos.setBackground(new Color(214, 138, 89));
        lblPuntos.setForeground(Color.black);
        lblPuntos.setBorder(bordeNegro);
        // Agrego la informacion de cada jugador.
        for (IJugador j: jugadores) {
            lblJugadores[i].setBounds(xOffset, yOffset, 200, 200);
            lblJugadores[i].setFont(new Font("Arial", Font.BOLD, 18));
            lblJugadores[i].setForeground(Color.black);
            lblJugadores[i].setText(j.getNombre() + " " + j.getPuntos());
            lblJugadores[i].setOpaque(true);
            lblJugadores[i].setBackground(new Color(214, 138, 89));
            lblJugadores[i].setBorder(bordeNegro);
            yOffset += 30;
            i++;
        }
        panel.revalidate();
        panel.repaint();
    }

    @Override
    public void ocultarBoton() {
        robarBtn.setVisible(false);
        habilitarComponentes(jugadorManoComponente, false);
    }
    @Override
    public void mostrarBoton() {
        robarBtn.setVisible(true);
        habilitarComponentes(jugadorManoComponente, true);
    }
    /**
     *
     * Habilita y deshabilita los listeners de las distintas fichas para impedir que se jueguen las fichas.
     * @return void
     */
    private void habilitarComponentes(Container container, boolean habilitar) {
        Component[] componentes = container.getComponents();
        for (Component c: componentes) {
            if (c instanceof VistaFicha){
                if (habilitar) {
                    // habilito nuevamente los listeners
                    for (MouseListener listener: mouseListenersGuardados) {
                        c.addMouseListener(listener);
                    }
                    mouseListenersGuardados.clear();
                } else {
                    // deshabilito los listeners
                    MouseListener[] listeners = c.getMouseListeners();
                    for (MouseListener listener : listeners) {
                        c.removeMouseListener(listener);
                        mouseListenersGuardados.add(listener);
                    }
                }
            }
        }
    }

    @Override
    public void limpiarTablero() {
        componenteTablero.limpiarFicha();
    }

    // cambia la pantalla actual para mostrar el fin del juego.
    @Override
    public void finalizarJuego(String mensaje) {
        this.remove(panel);
        JPanel nuevoPanel = Lobby.getjPanel("img/dominoes.jpg");
        nuevoPanel.setLayout(null);
        JLabel lbl = new JLabel(mensaje);
        // set texto attributos.
        lbl.setForeground(Color.white);
        lbl.setFont(new Font("Arial", Font.BOLD, 24));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT); // alinear el texto al centro.
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setVerticalAlignment(SwingConstants.CENTER);
        lbl.setBounds(0, 60, 1000, 100);
        nuevoPanel.setBounds(0,0,1200, 650);
        nuevoPanel.add(lbl);
        this.add(nuevoPanel);
        revalidate();
        repaint();
    }

    @Override
    public void desconectar() {
        SwingUtilities.invokeLater(()-> JOptionPane.showMessageDialog(null, "Se ha desconectado un jugador, se guardara la partida para continuar luego!!!",
                        "Jugador desconectado", JOptionPane.INFORMATION_MESSAGE));
        SwingUtilities.invokeLater(this::dispose);
        SwingUtilities.invokeLater(()-> System.exit(0));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
    }
}
