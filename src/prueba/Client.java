package prueba;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roger
 */
public class Client {

    public static void main(String[] args) throws SocketException, IOException {
        Scanner teclat = new Scanner(System.in);
        boolean sortir = false;

        do {
            System.out.println("1. Crear una partida nova "
                    + "\n2. Conectar-se a una partida "
                    + "\n3. Sortir");
            System.out.print("> ");

            if (teclat.hasNextInt()) {
                int opcio = teclat.nextInt();
                switch (opcio) {
                    case 1:
                        crearPartida();
                        break;
                    case 2:
                        System.out.println("Conectando...");
                        unirsePartida();
                        break;
                    case 3:
                        System.out.println("Cerrando...");
                        sortir = true;
                        break;
                    default:
                        System.out.println("\nInserta un número válido...\n");
                }
            } else {
                System.out.println("\nEscribe un numero...\n");
                teclat.next();
            }
            System.out.println("");
        } while (!sortir);

    }

    private static void crearPartida() {
        Scanner sc = new Scanner(System.in);

        //testeig
        InetAddress serverAddress;
        int serverPort = 7879;

        try {
            do {
                System.out.println("\nIntroduce el puerto para jugar: ");
                if (sc.hasNextInt()) {
                    break;
                } else {
                    System.out.println("Introdueix un valor númeric...");
                    sc.nextLine();
                }
            } while (true);

            String msg = "CREAR " + sc.nextLine();

            serverAddress = InetAddress.getByName("127.0.0.1");

            //enviar missatge a servidor
            DatagramSocket socket = new DatagramSocket();

            byte[] bytesOUT = msg.getBytes();
            DatagramPacket outPacket = new DatagramPacket(bytesOUT, bytesOUT.length,
                    serverAddress, serverPort);
            socket.send(outPacket);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void unirsePartida() {

    }
}
