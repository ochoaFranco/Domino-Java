package vista;

import controlador.Controlador;
import modelo.IFicha;
import modelo.IJugador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuFicha extends JDialog {
    private JButton izquierdaBtn;
    private JButton derechaBtn;
    private JButton salirBtn;
    private static boolean jugar = false;

    public MenuFicha() {
        setTitle("Domino");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 200);
        setResizable(false);
        JPanel panel = Lobby.getjPanel("img/tablero.png");
        panel.setLayout(null);
        JLabel label = new JLabel("Elige la posicion");
        label.setBounds(175, 20, 400, 20);
        label.setForeground(Color.white);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label);
        // creo los botones
        izquierdaBtn = new JButton("Izquieda");
        izquierdaBtn.setBounds(140, 60, 100, 20);

        panel.add(izquierdaBtn);

        derechaBtn = new JButton("Derecha");
        derechaBtn.setBounds(250, 60, 100, 20);
        panel.add(derechaBtn);

        salirBtn = new JButton("Elegir otra ficha");
        salirBtn.setBounds(170, 120, 138, 20);
        panel.add(salirBtn);

        this.setUndecorated(true); // saco los botones de salir.
        this.setModal(true);

        // calculo la posicion en la pantalla
        this.setLocationRelativeTo(null);

        this.getContentPane().add(panel);
    }

    public void agregarListeners(VistaFicha f) {
        IFicha ficha = f.getFicha();
        // funcionalidad para elegir otra ficha.
        elegirOtraFicha(f);
        // listeners para extremo izq o derec.
        jugarFichaIzq(f);
        jugarFichaDerec(f);
    }

    private void elegirOtraFicha(VistaFicha f) {
        salirBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                f.setVisible(true); // devuelvo la ficha y cierro la ventana.
                jugar = false;
                dispose();
                VistaGrafica.decrementarClicks();
            }
        });
    }

    // se juega la ficha en el lado izquierdo.
    private void jugarFichaIzq(VistaFicha original) {
        izquierdaBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (jugar) {
                    VistaGrafica.realizarJugada("i", original);
                    jugar = false;
                    dispose();
                    VistaGrafica.decrementarClicks();
                }
            }
        });
    }

    private void jugarFichaDerec(VistaFicha f) {
        derechaBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (jugar) {
                    VistaGrafica.realizarJugada("d", f);
                    jugar = false;
                    dispose();
                    VistaGrafica.decrementarClicks();
                }
            }
        });
    }

    public static boolean isJugar() {
        return jugar;
    }

    public static void setJugar(boolean jugar) {
        MenuFicha.jugar = jugar;
    }

    public void iniciar() {
        setVisible(true);
    }
}
