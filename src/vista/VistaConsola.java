package vista;

import controlador.Controlador;
import modelo.IFicha;
import modelo.IJugador;
import modelo.exceptions.FichaIncorrecta;
import modelo.exceptions.FichaInexistente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;


public class VistaConsola implements IVista {
    private final Controlador controlador;
    private final JFrame frame;
    private final JTextArea consolaOutput;
    private final JTextField inputCMD;
    private final JButton ejecutarBtn;
    private final String nombre;
    private static boolean jugando = false;
    private int puntos;
    private int cantJugadors;

    public VistaConsola(String nombre, Controlador controlador) {
        this.nombre = nombre;
        this.controlador = controlador;
        frame = new JFrame("Domino");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        consolaOutput = new JTextArea();
        consolaOutput.setEditable(false);
        Font font = new Font("Arial", Font.PLAIN, 16);
        consolaOutput.setFont(font);

        inputCMD = new JTextField();
        ejecutarBtn = new JButton("Ejecutar");

        JPanel inputPanel = new JPanel(new BorderLayout()); // se crea un panel del tipo BorderLayout.
        inputPanel.add(inputCMD, BorderLayout.CENTER);
        inputPanel.add(ejecutarBtn, BorderLayout.EAST);

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(consolaOutput), BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);

        // alta del jugador.
        altaJugador(nombre);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controlador.desconectarJugador();
                System.exit(0);
            }
        });


        // FUNCIONALIDAD DEL BOTON
        inputCMD.addActionListener(e -> manejadorComandos());

        ejecutarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manejadorComandos();
            }
        });
    }

    private void manejadorComandos() {
        String comando =  inputCMD.getText();
        determinarComando(comando);
    }
    // inicia la vistas si es que no esta visible.
    public void iniciar() {
        if (!frame.isVisible())
            frame.setVisible(true);
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        consolaOutput.append(mensaje);
    }

    @Override
    // muestra un mensaje cuando finaliza el juego.
    public void finalizarJuego(String mensaje) {
        mostrarMensaje(mensaje);
    }

    @Override
    public void desconectar() {
        mostrarMensaje("Un jugador se ha desconectado, se guardara el juego para jugar luego.");
        SwingUtilities.invokeLater(frame::dispose);
        SwingUtilities.invokeLater(()-> System.exit(0));
    }

    @Override
    public void mostrarFichasJugador(IJugador jugador) {
        List<IFicha> fichas = controlador.getFichasJugador(jugador);
        StringBuilder ficha = new StringBuilder();
        for (IFicha f : fichas) {
            ficha.append("|").append(f.getIzquierdo()).append("|").append(f.getDerecho()).append("|").append(" ");
        }
        consolaOutput.append(ficha.toString());
        consolaOutput.append("\n");
    }

    @Override
    public void mostrarFicha(IFicha f) {
        String ficha = "|" + f.getIzquierdo() + "|" + f.getDerecho() + "|  \n";
        consolaOutput.append(ficha);
    }

    private void determinarComando(String comando) {
        comando = comando.toLowerCase();
        if (comando.equals("jugar")) {
            jugar(puntos,cantJugadors );
        } else if (comando.startsWith("ficha:")) {
            jugada(comando);
        } else if (comando.equalsIgnoreCase("robar")) {
            actualizarManoJugador();
        } else if (comando.startsWith("jugadores:")) {
            setearJugadores(comando);
        } else if (comando.equalsIgnoreCase("desconectar")) {
            mostrarMensaje("Abandonaras el juego...");
            controlador.persistirPartida();
        }
    }

    // el jugador roba una ficha del pozo y se actualiza la mano.
    private void actualizarManoJugador() {
        controlador.robarFicha();
    }

    @Override
    @SuppressWarnings("unchecked") // elimina el warning del tipo de dato
    public void mostrarTablero(Object o) {
        consolaOutput.append("TABLERO\n");
        consolaOutput.append("-------------------------------------------------------------\n");
        StringBuilder ficha = new StringBuilder();
        String espacios = generarEspacios(115); // it was 54
        ArrayList<IFicha> FichasVerticalesIzquierdas = new ArrayList<>();
        ArrayList<IFicha> fichasVerticalesDerechas = new ArrayList<>();

        buscarFichasVerticales((ArrayList<IFicha>) o, fichasVerticalesDerechas, FichasVerticalesIzquierdas);

        //Muestro las fichas verticales derechas.
        mostrarFichasVerticales(fichasVerticalesDerechas, ficha, espacios);

        // mostrar ficha horizontal.
        mostrarFichasHorizontales((ArrayList<IFicha>) o, ficha);

        // mostrar fichas verticales izquierdas.
        mostrarFichasVerticalesIzq(FichasVerticalesIzquierdas, ficha);

        consolaOutput.append(ficha.toString());
        consolaOutput.append("\n-------------------------------------------------------------\n");
    }

    private static void mostrarFichasVerticalesIzq(ArrayList<IFicha> FichasVerticalesIzquierdas, StringBuilder ficha) {
        Collections.reverse(FichasVerticalesIzquierdas);
        if (!FichasVerticalesIzquierdas.isEmpty()) {
            for (IFicha f : FichasVerticalesIzquierdas) {
                ficha.append("\n|").append(f.getDerecho()).append("|\n").append("|").append(f.getIzquierdo()).append("|").append(" ");
            }
        }
    }

    private static void mostrarFichasHorizontales(ArrayList<IFicha> o, StringBuilder ficha) {
        for (IFicha f : o) {
            if (!f.isVertical()) {
                ficha.append("|").append(f.getIzquierdo()).append("|").append(f.getDerecho()).append("|").append(" ");
            }
        }
    }

    private static void mostrarFichasVerticales(ArrayList<IFicha> fichasVerticalesDerechas, StringBuilder ficha, String espacios) {
        Collections.reverse(fichasVerticalesDerechas);
        if (!fichasVerticalesDerechas.isEmpty()) {
            for (IFicha f : fichasVerticalesDerechas) {
                ficha.append(espacios).append("|").append(f.getDerecho()).append("|\n").append(espacios + "|").append(f.getIzquierdo()).append("|").append("\n");
            }
        }
    }

    private static void buscarFichasVerticales(ArrayList<IFicha> o, ArrayList<IFicha> fichasVerticalesDerechas, ArrayList<IFicha> FichasVerticalesIzquierdas) {
        // Busco tiles verticales.
        for (IFicha f : o) {
            if (f.isVertical()) {
                if (f.isDerecho()) {
                    if (!fichasVerticalesDerechas.contains(f))
                        fichasVerticalesDerechas.add(f);
                } else {
                    if (!FichasVerticalesIzquierdas.contains(f))
                        FichasVerticalesIzquierdas.add(f);
                }
            }
        }
    }

    // establezco la cantidad maxima de jugadores.
    private void setearJugadores(String comando) {
        String[] partes = comando.split("\\s+"); // express.regular para separar por caracteres en blanco.
        int numero = 0;
        if (partes.length == 2) {
            try {
                numero = Integer.parseInt(partes[1]);
            } catch (NumberFormatException N) {
                consolaOutput.append("Debe ingresar un numero como valor para jugadores.\n");
            }
        }
        consolaOutput.append("Cantidad de jugadores establecida.\n");
    }

    public void ocultarBoton() {
        ejecutarBtn.setEnabled(false);
        inputCMD.setEditable(false);
        inputCMD.setText("");
    }
    public void mostrarBoton() {
        ejecutarBtn.setEnabled(true);
        inputCMD.setEditable(true);
    }

    @Override
    public void limpiarTablero() {

    }

    private void altaJugador(String nombre) {
        consolaOutput.append("\nBienvenido " + nombre + "!\n");
    }

    // Inicia el juego el creador.
    public void jugar(int puntos, int cantJugadores) {
        this.puntos = puntos;
        if (!VistaConsola.jugando) {
            controlador.iniciarJuego(puntos, cantJugadores);
            VistaConsola.jugando = true;
        }
    }

    // Los participantes se unen al juego.
    public void jugar() {
        if (!VistaConsola.jugando) {
            controlador.iniciarJuego();
            VistaConsola.jugando = true;
        }
    }

    // metodo que permite generar espacios.
    private String generarEspacios(int espacios) {
        return String.format("%" + espacios + "s", "");
    }

    private void jugada(String comando) {
        String[] partes = comando.split("\\s+"); // express.regular para separar por caracteres en blanco.
        if (partes.length == 4) {
            try {
                // Obtener informacion de la ficha.
                int izq = Integer.parseInt(partes[1]);
                int der = Integer.parseInt(partes[2]);
                String extremo = partes[3];
                try {
                    controlador.gestionarTurnos(izq, der, extremo);
                } catch (FichaIncorrecta f) {
                    consolaOutput.append("Ficha incorrecta!!! \n");
                }

            } catch (NumberFormatException ex) {
                consolaOutput.append("Formato de ficha invalido (Ficha I o Ficha D)\n");
            } catch (FichaInexistente e ) {
                consolaOutput.append("Ficha inexistente!\n");
            }
        } else {
            consolaOutput.append("Formato de ficha invalido (Ficha I o Ficha D)\n");
        }
    }

    @Override
    @SuppressWarnings("unchecked") // elimina el warning del tipo de dato
    public void mostrarTablaPuntos(Object o, int puntos) {
        for (IJugador f : (ArrayList<IJugador>)o) {
            consolaOutput.append("Jugador: " + f.getNombre() + " Puntos: " + f.getPuntos() + "\n");
        }
        consolaOutput.append("\n");
    }
}

