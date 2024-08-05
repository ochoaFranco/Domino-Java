package vista;

import controlador.Controlador;
import modelo.IFicha;
import modelo.IJugador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Login  extends JDialog {
    // Puntos minimos y maximos aceptados.
    private static final int PUNTOS_MINIMO = 10;
    private static final int PUNTOS_MAXIMO = 400;
    private static final int JUGADORES_MINIMO= 2;
    private static final int JUGADORES_MAXIMO= 4;
    private final Controlador controlador;
    private JComboBox<String> interfazComboBox;
    private final JTextField txtfieldNombre = new JTextField();
    private final JTextField txtfieldPuntos = new JTextField();
    private final JTextField txtfielCantJugadores = new JTextField();
    private final JFrame parent;
    private static boolean isJuegoIniciado;

    public Login(JFrame parent, Controlador controlador) {
        super(parent, "Login", false);
        this.controlador = controlador;
        isJuegoIniciado = false;
        // seteo la ventana anterior para poder cerrarla.
        this.parent = parent;
        // seteando atributos
        setTitle("Domino");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 200);
        setResizable(false);

        // se crea un panel
        JPanel panel = Lobby.getjPanel("img/dominoes.jpg");
        panel.setLayout(null);

        // atributos de los labels.
        agregarLabels(panel);
        // agrego los textfields.
        txtfieldNombre.setBounds(180, 8, 100, 20);
        panel.add(txtfieldNombre);
        // agrego los componentes si es creador.
        agregarComponentesCreador(panel);
        // Agrego un menu de opciones.
        agregarMenuOpciones(panel);
        // agrego los botones.
        JButton okayBtn = new JButton("Ok");
        okayBtn.setBounds(180, 130, 100, 20);
        panel.add(okayBtn);

        // calculo tamanio pantalla.
        this.setLocationRelativeTo(null);
        this.getContentPane().add(panel);

        // Agrego la funcionalidad del boton.
        okayBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean esJuegoCreado = controlador.esJuegoCreado();
                int idJugador = controlador.existeJugador(txtfieldNombre.getText());
                if (txtfieldNombre.getText().isEmpty()) {
                    mostrarMensajeErrorCampoVacio();
                    return;
                } else if (idJugador != -1) {
                    controlador.setJugador(idJugador);
                    String opSeleccionada = (String) interfazComboBox.getSelectedItem();
                    elegirVista(opSeleccionada,txtfieldNombre.getText());
                    controlador.pedirCargaPartida();
                    MenuJuego.incrementarVentanasCerradas();
                    dispose();
                    return;
                }

                if (!esJuegoCreado) {
                    try {
                        int puntos = Integer.parseInt(txtfieldPuntos.getText());
                        int cantJugadores = Integer.parseInt(txtfielCantJugadores.getText());
                        if (esPuntosInvalidos(puntos)) {
                            mostrarMensajeErrorPuntos();
                            return;
                        }
                        if (esCantidadJugadoresInvalido(cantJugadores)) {
                            mostrarMensajeErrorCantJugadores();
                            return;
                        }
                        okayBtnPresionado();
                    } catch (NumberFormatException ex) {
                        mostrarMensajeErrorInput();
                    }
                }
                else
                    okayBtnPresionado();
            }
        });

        // agrego un listener al textfield y combo box
        txtfieldNombre.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    okayBtn.doClick();
            }
        });

        txtfielCantJugadores.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    okayBtn.doClick();
            }
        });

        interfazComboBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    okayBtn.doClick();
                }
            }
        });
    }

    /**
     * Valida que los puntos se encuentren dentro de los puntos minimos y maximos.
     * @param puntos son los puntos ingresados por el usuario.
     * @return True si no estan dentro de los parametros, False caso contrario.
     */
    private boolean esPuntosInvalidos(int puntos) {
        return puntos < PUNTOS_MINIMO || puntos > PUNTOS_MAXIMO;
    }

    /**
     * Valida que la cantidad de jugadores se encuentren dentro de los jugadores minimos y maximos.
     * @param cantJugadores son la cantidad de jugadores ingresados por el usuario.
     * @return True si no estan dentro de los parametros, False caso contrario.
     */
    private boolean esCantidadJugadoresInvalido(int cantJugadores) {
        return cantJugadores < JUGADORES_MINIMO || cantJugadores > JUGADORES_MAXIMO;
    }

    private void mostrarMensajeErrorPuntos() {
        SwingUtilities.invokeLater(()->JOptionPane.showMessageDialog(null, "Los puntos no pueden ser menores a 10 ni mayores a 400 !!!",
                "Error", JOptionPane.ERROR_MESSAGE));
    }

    private void mostrarMensajeErrorCantJugadores() {
        SwingUtilities.invokeLater(()->JOptionPane.showMessageDialog(null, "Como minimo debe haber 2 jugadores y como maximo 4 !!!",
                "Error", JOptionPane.ERROR_MESSAGE));
    }

    private void mostrarMensajeErrorInput() {
        SwingUtilities.invokeLater(()->
                JOptionPane.showMessageDialog(null, "Recuerde que los puntos y cantidad de jugadores son valores numericos !!!",
                        "Error", JOptionPane.ERROR_MESSAGE));
    }

    private void mostrarMensajeErrorCampoVacio() {
        SwingUtilities.invokeLater(()->
                SwingUtilities.invokeLater(()->
                        JOptionPane.showMessageDialog(null, "No puede haber campos vacios !!!", "Error", JOptionPane.ERROR_MESSAGE))
        );
    }

    public void iniciar() {
        this.setVisible(true);
    }

    /**
     * Permite cargar una partida guardada del jugador.
     * @param idJugador Jugador al cual se le cargara la partida.
     * @param opSeleccionada Vista que se le cargara (consola/grafica)
     * @param nombre Nombre del jugador.
     */
    public void cargarPartidaJugador(int idJugador, Object opSeleccionada, String nombre) {
        controlador.setJugador(idJugador);

        String opSeleccionada1 = (String) opSeleccionada;

        elegirVista(opSeleccionada1, nombre);
    }

    // Agrega los componentes unicamente si es creador.
    private void agregarComponentesCreador(JPanel panel) {
        if (controlador.esJuegoCreado())
            return;
        // agrego los text fields
        txtfieldPuntos.setBounds(180, 68, 100, 20);
        txtfielCantJugadores.setBounds(180, 94, 100, 20);

        // Atributos para el label de los puntos
        JLabel lblPuntos = new JLabel("Puntos");
        lblPuntos.setBounds(80, 68, 100, 20);
        lblPuntos.setForeground(Color.white);
        lblPuntos.setFont(new Font("Arial", Font.BOLD, 16));

        // Atributos para el label de los jugadores.
        JLabel lblJugadores = new JLabel("Jugadores");
        lblJugadores.setBounds(80, 94, 100, 20);
        lblJugadores.setForeground(Color.white);
        lblJugadores.setFont(new Font("Arial", Font.BOLD, 16));

        // Atributos para el label de los jugadores (constraints)
        JLabel lblJugadoresConstraints = new JLabel("(rango 2-4)");
        lblJugadoresConstraints.setBounds(290, 94, 100, 20);
        lblJugadoresConstraints.setForeground(Color.white);
        lblJugadoresConstraints.setFont(new Font("Arial", Font.BOLD, 16));

        panel.add(txtfieldPuntos);
        panel.add(txtfielCantJugadores);
        panel.add(lblPuntos);
        panel.add(lblJugadores);
        panel.add(lblJugadoresConstraints);
    }

    private void agregarMenuOpciones(JPanel panel) {
        String[] interfazOpciones = {"Grafica", "Consola"};
        interfazComboBox = new JComboBox<>(interfazOpciones);
        interfazComboBox.setBounds(180, 38, 100, 20);
        panel.add(interfazComboBox);
    }

    // agrega todos los labels en la pantalla
    private void agregarLabels(JPanel panel) {
        // Atributos para el label del nombre
        JLabel lblNombre = new JLabel("Nombre");
        lblNombre.setBounds(80, 8, 100, 20);
        lblNombre.setForeground(Color.white);
        lblNombre.setFont(new Font("Arial", Font.BOLD, 16));

        // Atributos para el label de la interfaz
        JLabel lblConsola = new JLabel("Interfaz");
        lblConsola.setBounds(80, 38, 100, 20);
        lblConsola.setForeground(Color.white);
        lblConsola.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lblNombre);
        panel.add(lblConsola);
    }

    // Permite elegir un tipo de vista.
    private IVista elegirVista(String opSeleccionada, String usuario) {
        IVista vista;
        if (opSeleccionada.equalsIgnoreCase("Consola")) {
            vista = new VistaConsola(usuario, controlador);
            controlador.setVista(vista);
        } else {
            vista = new VistaGrafica(usuario, controlador);
            controlador.setVista(vista);
            vista.ocultarBoton();
        }
        return vista;
    }

    // levanta la vista correspondiente y cierra las anteriores.
    private void okayBtnPresionado() {
        String usuario = txtfieldNombre.getText();
        String opSeleccionada = (String) interfazComboBox.getSelectedItem();
        // comprobamos la seleccion
        assert opSeleccionada != null;
        IVista vista = elegirVista(opSeleccionada,usuario);

        if (vista instanceof VistaConsola)
            vista.ocultarBoton();

        // EJecuto el juego y levanto las ventanas.
        if (!isJuegoIniciado) {
            if (!controlador.esJuegoCreado()) {
                int puntos = Integer.parseInt(txtfieldPuntos.getText()); // ya se encuentra validado.
                int cantJugadores = Integer.parseInt(txtfielCantJugadores.getText()); // ya se encuentra validado.
                // conecto el usuario creador de la sala al juego.
                controlador.conectarJugador(usuario);
                if (vista instanceof VistaGrafica) {
                    ((VistaGrafica) vista).jugar(puntos, cantJugadores);
                } else
                    ((VistaConsola) vista).jugar(puntos, cantJugadores);
            } else {
                controlador.conectarJugador(usuario);
                if (vista instanceof VistaGrafica) {
                    ((VistaGrafica) vista).jugar();
                } else
                    ((VistaConsola) vista).jugar();
            }
            Login.isJuegoIniciado = true;
        }
        SwingUtilities.invokeLater(()->JOptionPane.showMessageDialog(null, "Esperando que otros jugadores se unan," +
                " el juego comenzara pronto...", "Esperando jugadores", JOptionPane.INFORMATION_MESSAGE));
        dispose();
        MenuJuego.incrementarVentanasCerradas(); // cierra las ventanas anteriores.
    }
}
