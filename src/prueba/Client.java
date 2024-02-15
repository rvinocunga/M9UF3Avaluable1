package prueba;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roger
 */
public class Client {

    static Scanner teclat = new Scanner(System.in);

    public static void main(String[] args) throws UnknownHostException, IOException {
		// Creem una connexió amb el servidor
		Socket connexio = new Socket("localhost", 7879);
		System.out.println("Connexió amb el servidor establerta.");
		
		// Obrim els cananls de comunicació amb el servidor
		DataInputStream in = new DataInputStream(connexio.getInputStream());
		DataOutputStream out = new DataOutputStream(connexio.getOutputStream());
		
		// Enviar nombres al servidor
		String msg = "";
		do {
			System.out.print("NOMBRE: ");
			msg = teclat.nextLine();
			if(msg=="") msg="FINAL";
			out.writeUTF(msg);
		} while(!msg.equals("FINAL"));
		
		// Rebre resposta del servidor
		msg = in.readUTF();
		System.out.println("SUMA TOTAL: "+msg);
		
		// Tanquem la connexió amb el client
		in.close();
		out.close();
		connexio.close();
		System.out.println("Connexió tancada");
	}/*
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
                System.out.println("\nEteclatribe un numero...\n");
                teclat.next();
            }
            System.out.println("");
        } while (!sortir);

    }
*/
    private static void crearPartida() {

        //testeig
        InetAddress serverAddress;
        int serverPort = 7879;

        try {
            do {
                System.out.println("\nIntroduce el puerto para jugar: ");
                if (teclat.hasNextInt()) {
                    break;
                } else {
                    System.out.println("Introdueix un valor númeric...");
                    teclat.nextLine();
                }
            } while (true);

            String msg = "CREAR " + teclat.nextLine();

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
        try {
            //testeig
            InetAddress serverAddress;
            int serverPort = 7879;

            // mensaje que envia el cliente
            String msg = "UNIR-ME ";

            serverAddress = InetAddress.getByName("127.0.0.1");

            //enviar missatge a servidor
            DatagramSocket socket = new DatagramSocket();

            byte[] bytesOUT = msg.getBytes();
            DatagramPacket outPacket = new DatagramPacket(bytesOUT, bytesOUT.length,
                    serverAddress, serverPort);
            socket.send(outPacket);
            
            //

            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            // recibe paquete
            socket.receive(packet);
            System.out.println(new String(packet.getData()).trim());
            
        } catch (SocketException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
