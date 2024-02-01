package prueba;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServerCentral {

    public static void main(String[] args) throws SocketException {
        /*
        La seva funció serà posar en contacte a dos jugadors.
        Per fer-ho escoltarà peticions UDP pel port 7879. 
        Les peticions que rebrà tindran la forma:
         */
        
        //El servidor escolta...
        int port = 7879;
        DatagramSocket socket = new DatagramSocket(port);
        System.out.printf("Escoltant al port %d...", port);
        
        while (true) {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("\nEsperant un nou paquet...");
            try {
                //missatge del client
                socket.receive(packet);
                String msg = new String(packet.getData()).trim();
                String ipClient = packet.getAddress().getHostAddress();
                System.out.printf("%s --> %s\n", ipClient, msg);
                
                // se corta el mensaje (CREAR 123)
                String[] missatgeTallat = msg.split(" ");

                //
                //OPCIONS
                //
                switch (missatgeTallat[0]) {
                    case "CREAR":
                        System.out.println("\nEntra a la opción CREAR");
                        int portPerJugar = Integer.parseInt(missatgeTallat[1]);
                        System.out.println("Port per jugar: " + ipClient + ":"+ portPerJugar);
                        break;
                    case "UNIR-ME":
                        System.out.println("\nEntra a opción UNIR-ME");
                        break;
                    default:
                        throw new AssertionError();
                }

            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
