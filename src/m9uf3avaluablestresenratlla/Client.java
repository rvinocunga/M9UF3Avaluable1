package m9uf3avaluablestresenratlla;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Client {

    private DatagramSocket udpSocket;
    private InetAddress serverAddress;
    private int serverPort;
    private BufferedReader reader;

    public Client(String serverHost, int serverPort) throws UnknownHostException, SocketException {
        this.serverAddress = InetAddress.getByName(serverHost);
        this.serverPort = serverPort;
        this.udpSocket = new DatagramSocket();
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void start() throws IOException {
        String input;
        while (true) {
            System.out.println("1. Crear una partida nova");
            System.out.println("2. Connectar-se a una partida");
            System.out.println("3. Sortir");
            System.out.print("Selecciona una opció: ");

            input = reader.readLine();
            switch (input) {
                case "1":
                    createGame();
                    break;
                case "2":
                    joinGame();
                    break;
                case "3":
                    System.out.println("Sortint del joc...");
                    return;
                default:
                    System.out.println("Opció no vàlida. Torna-ho a intentar.");
            }
        }
    }

    private void createGame() throws IOException {
        System.out.print("Introdueix el port TCP per la teva partida: ");
        String port = reader.readLine();

        String message = "CREAR " + port;
        byte[] messageBytes = message.getBytes();

        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, serverAddress, serverPort);
        udpSocket.send(packet);

        // Rebre resposta del servidor central
        packet = new DatagramPacket(new byte[1024], 1024);
        udpSocket.receive(packet);
        String response = new String(packet.getData(), 0, packet.getLength());

        if ("OK".equals(response)) {
            System.out.println("Partida creada amb èxit. Esperant oponent...");

            // Crear un ServerSocket per acceptar connexions del jugador oponent
            try ( ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port))) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Un jugador s'ha connectat! Començant la partida...");

                // Aquí va la lògica de la partida...
                // Tanca la connexió amb el client quan la partida acabi
                clientSocket.close();
            }
        } else {
            System.out.println("No s'ha pogut crear la partida: " + response);
        }
    }

    private void joinGame() throws IOException {
        String message = "UNIR-ME";
        byte[] messageBytes = message.getBytes();

        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, serverAddress, serverPort);
        udpSocket.send(packet);

        // Rebre resposta del servidor central
        packet = new DatagramPacket(new byte[1024], 1024);
        udpSocket.receive(packet);
        String response = new String(packet.getData(), 0, packet.getLength());

        if (response.contains("::")) {
            String[] parts = response.split("::");
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);

            System.out.println("Connectant-se a la partida en " + host + ":" + port + "...");

            // Connectar-se al servidor de partida TCP
            try ( Socket serverSocket = new Socket(host, port)) {
                System.out.println("Connectat a la partida! Començant la partida...");

                // Aquí va la lògica del client de la partida...
                // Tanca la connexió amb el servidor quan la partida acabi
                serverSocket.close();
            }
        } else {
            System.out.println("No s'ha pogut unir a cap partida: " + response);
        }
    }

    public static void main(String[] args) {
        try {
            Client client = new Client("localhost", 7879);
            client.start();
        } catch (IOException e) {
            System.out.println("Error al iniciar el client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
