package cliente;

import java.rmi.RemoteException;
import java.util.ArrayList;
import javax.swing.*;

import ar.edu.unlu.rmimvc.RMIMVCException;
import ar.edu.unlu.rmimvc.Util;
import ar.edu.unlu.rmimvc.cliente.Cliente;
import controlador.Controlador;
import vista.Lobby;

public class AppCliente {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 8888;

    public static void main(String[] args) throws RemoteException {
        ArrayList<String> ips = Util.getIpDisponibles();
        String port = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione el puerto en el que escuchará peticiones el cliente", "Puerto del cliente",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                9999
        );
        Controlador controlador = new Controlador();
        Lobby vista = new Lobby(controlador);
        Cliente c = null;
        try {
            c = new Cliente(AppCliente.IP, Integer.parseInt(port), AppCliente.IP, AppCliente.PORT);
            vista.iniciar();
        } catch (NumberFormatException ex) {
            SwingUtilities.invokeLater(()->JOptionPane.showMessageDialog(null, "Ha ocurrido un error de red, revise la configuracion.!!!",
                    "Error Red", JOptionPane.ERROR_MESSAGE));
//            System.exit(1);
            ex.printStackTrace();
        }
        try {
            c.iniciar(controlador);
        } catch (RemoteException e) {
            SwingUtilities.invokeLater(()->JOptionPane.showMessageDialog(null, "Ha ocurrido un error de red, revise la configuracion.!!!",
                    "Error Red", JOptionPane.ERROR_MESSAGE));
//            System.exit(1);
            e.printStackTrace();

        } catch (RMIMVCException e) {
            SwingUtilities.invokeLater(()->JOptionPane.showMessageDialog(null, "Ha ocurrido un error !!!",
                    "Error", JOptionPane.ERROR_MESSAGE));
//            System.exit(1);
            e.printStackTrace();
        }
    }
}

