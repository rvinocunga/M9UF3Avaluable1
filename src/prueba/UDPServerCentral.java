package prueba;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author roger
 */
public class UDPServerCentral {
    static int port = 7879;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(port);
        while (true) {
            // Esperem que es connecti un client
            System.out.println("Esperant nou client..." + port);
            Socket connexio = server.accept();
            System.out.println("Client " + connexio.getInetAddress().getHostAddress() + " connectat.");
            new ComunicacioClient(connexio).start();
        }
    }

    /*
    public static void main(String[] args) throws SocketException, InterruptedException {

        int port = 7879;
        DatagramSocket socket = new DatagramSocket(port);

        List ipPartidesEnCola = new ArrayList();

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
                        // emmagatzema en una List les IPs en cua per a jugar
                        ipPartidesEnCola.add(ipClient + "::" + portPerJugar);
                        System.out.println("Port per jugar: " + ipClient + ":" + portPerJugar);
                        break;
                    case "UNIR-ME":
                        System.out.println("\nEntra a opción UNIR-ME...");
                        byte[] bytesOUT;
                        DatagramPacket outPacket;

                        do {
                            msg = "Buscando partida...";
                            System.out.println(msg);

                            bytesOUT = msg.getBytes();
                            System.out.println("El cliente: " + packet.getSocketAddress().toString() + " está esperando...");

                            outPacket = new DatagramPacket(bytesOUT, bytesOUT.length, packet.getSocketAddress());
                            socket.send(outPacket);

                            // mientras no haya nada en la List, se ejecutarà hasta que haya algo cada 5 segundos
                            Thread.sleep(5 * 1000);
                        } while (ipPartidesEnCola.isEmpty());

                        msg = "Se ha encontrado una partida!";
                        System.out.println(msg);
                        bytesOUT = msg.getBytes();
                        outPacket = new DatagramPacket(bytesOUT, bytesOUT.length, packet.getSocketAddress());
                        socket.send(outPacket);

                        break;
                    default:
                        throw new AssertionError();
                }

            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }*/

    private static class ComunicacioClient extends Thread {

        private Socket connexio;

        public ComunicacioClient(Socket connexio) {
            this.connexio = connexio;
        }

        @Override
        public void run() {
            String addr = this.connexio.getInetAddress().getHostAddress() + ":" + String.valueOf(this.connexio.getPort());
            try {
                // Obrim els cananls de comunicació amb el client
                DataInputStream in = new DataInputStream(connexio.getInputStream());
                DataOutputStream out = new DataOutputStream(connexio.getOutputStream());

                // Repetir l'intercanvi de dades mentre el client estigui connectat.
                int suma = 0;

                String msg = in.readUTF();
                while (!msg.equals("FINAL")) {
                    try {
                        suma += Integer.parseInt(msg);
                        System.out.println(addr + " --> Nombre rebut: " + msg);
                    } catch (NumberFormatException e) {
                        System.out.println(addr + " --> Nombre mal format: " + msg);
                    }

                    msg = in.readUTF();
                }

                System.out.println(addr + " --> Total suma: " + suma);
                out.writeUTF(String.valueOf(suma));

                // Tanquem la connexió amb el client
                in.close();
                out.close();
                connexio.close();
                System.out.println(addr + " --> Connexió tancada");
            } catch (IOException e) {
                System.out.println(addr + " --> Client desconnectat.");
            }
        }

    }
}
