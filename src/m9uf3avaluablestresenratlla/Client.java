package m9uf3avaluablestresenratlla;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Client {

    private DatagramSocket socketUDP;
    private InetAddress direccionServidor;
    private int puertoServidor;
    private BufferedReader lector;

    public Client(String servidorHost, int servidorPuerto) throws UnknownHostException, SocketException {
        this.direccionServidor = InetAddress.getByName(servidorHost);
        this.puertoServidor = servidorPuerto;
        this.socketUDP = new DatagramSocket();
        this.lector = new BufferedReader(new InputStreamReader(System.in));
    }

    public void iniciar() throws IOException {
        String entrada;
        while (true) {
            System.out.println("1. Crear una partida nova");
            System.out.println("2. Connectar-se a una partida");
            System.out.println("3. Sortir");
            System.out.print("Selecciona una opcio: ");

            entrada = lector.readLine();
            switch (entrada) {
                case "1":
                    crearPartida();
                    break;
                case "2":
                    unirseAPartida();
                    break;
                case "3":
                    System.out.println("Sortirnt del joc ...");
                    return;
                default:
                    System.out.println("Opcio nova Valida. Torna a intentar");
            }
        }
    }

    private void crearPartida() throws IOException {
        System.out.print("Introdueix el port TCP de la partida: ");
        String puerto = lector.readLine();

        String mensaje = "CREAR " + puerto;
        byte[] bytesMensaje = mensaje.getBytes();

        DatagramPacket paquete = new DatagramPacket(bytesMensaje, bytesMensaje.length, direccionServidor, puertoServidor);
        socketUDP.send(paquete);

        // Recibir respuesta del servidor central
        paquete = new DatagramPacket(new byte[1024], 1024);
        socketUDP.receive(paquete);
        String respuesta = new String(paquete.getData(), 0, paquete.getLength());

        if ("OK".equals(respuesta)) {
            System.out.println("Partida creada amb exit. Esperant oponent...");

            // Crear un ServerSocket para aceptar conexiones del jugador oponente
            try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(puerto))) {
                Socket socketCliente = serverSocket.accept();
                System.out.println("¡Un jugador s'ha conectat! Comença la partida...");

                // Cerrar la conexión con el cliente cuando la partida acabe
                socketCliente.close();
            }
        } else {
            System.out.println("No s'ha pogut crear la partida: " + respuesta);
        }
    }

    private void unirseAPartida() throws IOException {
        String mensaje = "UNIR-ME";
        byte[] bytesMensaje = mensaje.getBytes();

        DatagramPacket paquete = new DatagramPacket(bytesMensaje, bytesMensaje.length, direccionServidor, puertoServidor);
        socketUDP.send(paquete);

        // Recibir respuesta del servidor central
        paquete = new DatagramPacket(new byte[1024], 1024);
        socketUDP.receive(paquete);
        String respuesta = new String(paquete.getData(), 0, paquete.getLength());

        if (respuesta.contains("::")) {
            String[] partes = respuesta.split("::");
            String host = partes[0];
            int puerto = Integer.parseInt(partes[1]);

            System.out.println("Connectant-se a la partida a " + host + ":" + puerto + "...");

            // Conectarse al servidor de partida TCP
            try (Socket socketServidor = new Socket(host, puerto)) {
                System.out.println("Connectat a la partida. Començant la partida!");

                // Cerrar la conexión con el servidor cuando la partida acabe
                socketServidor.close();
            }
        } else {
            System.out.println("No s'ha pogut unir cap partida: " + respuesta);
        }
    }

    public static void main(String[] args) {
        try {
            Client cliente = new Client("localhost", 7879);
            cliente.iniciar();
        } catch (IOException e) {
            System.out.println("Error al iniciar el client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
