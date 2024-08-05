package servidor;

import java.rmi.RemoteException;
import java.util.ArrayList;

import ar.edu.unlu.rmimvc.RMIMVCException;
import ar.edu.unlu.rmimvc.servidor.Servidor;
import modelo.AdministradorPartidas;
import modelo.IDomino;
import modelo.Domino;

import javax.swing.*;
import ar.edu.unlu.rmimvc.Util;

public class AppServidor {
    private static final int PORT = 8888;
    private static final String IP = "127.0.0.1";
    public static void main(String[] args) throws RemoteException {
        String nombre =  JOptionPane.showInputDialog(null, "Ingrese el nombre de la partida a recuperar (deje vacio para una nueva)", "Nombre usuario", JOptionPane.QUESTION_MESSAGE);

        IDomino juego = AdministradorPartidas.getPartidaJugador(nombre);
        Servidor servidor = new Servidor(AppServidor.IP, AppServidor.PORT);
        if (juego == null) {
            juego = Domino.getInstancia();
            SwingUtilities.invokeLater(()->JOptionPane.showMessageDialog(null, "No se encontro partida guardada, comenzara una nueva.", "Nueva partida", JOptionPane.INFORMATION_MESSAGE));
        } else
            SwingUtilities.invokeLater(()->JOptionPane.showMessageDialog(null, "Se encontro una partida guardada, se retomara desde ese punto", "Cargar partida", JOptionPane.INFORMATION_MESSAGE));

        try {
            servidor.iniciar(juego);
        } catch (RemoteException e) {
            SwingUtilities.invokeLater(()->JOptionPane.showMessageDialog(null, "Ha ocurrido un error de red !!!",
                    "Error Red", JOptionPane.ERROR_MESSAGE));
            new javax.swing.Timer(7000, evt -> System.exit(1)).start();
            e.printStackTrace();
        } catch (RMIMVCException e) {
            SwingUtilities.invokeLater(()->JOptionPane.showMessageDialog(null, "Ha ocurrido un error !!!",
                    "Error", JOptionPane.ERROR_MESSAGE));
            new javax.swing.Timer(7000, evt -> System.exit(1)).start();
            e.printStackTrace();
        }
    }
}