package vista;

import controlador.Controlador;
import modelo.IFicha;
import modelo.IJugador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuJuego extends JDialog {
    private static int ventanasCerradas = 0;
    private static final int totalDeVentanasCerradasEsperadas = 1;
    private static JFrame parent;
    private final Controlador controlador;
    private final JButton creadorBtn = new JButton();

    public MenuJuego(JFrame parent, Controlador controlador) {
        super(parent, "Domino", true);
        this.controlador = controlador;
        MenuJuego.parent = parent; // Ventana anterior.
        setTitle("Domino");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 200);
        setResizable(false);

        JPanel panel = Lobby.getjPanel("img/dominoes.jpg");
        panel.setLayout(null);
        // agrego un label
        JLabel label = new JLabel("Cree una partida o unase a una.");
        label.setBounds(120, 20, 400, 20);
        label.setForeground(Color.white);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label);

        // creo los botones
        boolean estabaCreado = agregarComponentesCreador(panel);
        JButton unirseBtn = new JButton("Unirse");
        // reubica el boton si la partida ya estaba creada.
        if (estabaCreado)
            unirseBtn.setBounds(180, 60, 80, 20);
        else
            unirseBtn.setBounds(290, 60, 80, 20);

        panel.add(unirseBtn);
        // calculo la posicion de la pantalla.
        this.setLocationRelativeTo(null);

        this.getContentPane().add(panel);

        // Funcionalidad para el creador.
        creadorBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!controlador.esJuegoCreado())
                    loginUsuario(controlador);
            }
        });

        // Funcionalidad para los participantes del juego.
        unirseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controlador.esJuegoCreado())
                    loginUsuario(controlador);
                else
                    SwingUtilities.invokeLater(()->
                            JOptionPane.showMessageDialog(null, "Primero se debe crear la partida!!!", "Crear Partida", JOptionPane.INFORMATION_MESSAGE));
            }
        });
    }

    private boolean agregarComponentesCreador(JPanel panel) {
        if (controlador.esJuegoCreado())
            return true;
        creadorBtn.setText("Crear");
        creadorBtn.setBounds(130, 60, 80, 20);
        panel.add(creadorBtn);
        return false;
    }

    // levanta la interfaz de login
    private void loginUsuario(Controlador controlador) {
        Login usuario = new Login((JFrame) this.getParent(), controlador);
        usuario.iniciar();
        dispose();
    }

    // cuenta la cantidad de ventanas cerradas para cerrar el resto.
    public static void incrementarVentanasCerradas() {
        MenuJuego.ventanasCerradas += 1;
        if (MenuJuego.ventanasCerradas == MenuJuego.totalDeVentanasCerradasEsperadas)
            MenuJuego.parent.dispose();
    }

    public void iniciar() {
        setVisible(true);
    }
}