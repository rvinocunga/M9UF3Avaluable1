
package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;
import m9uf3avaluablestresenratlla.GameSession;

// Clase principal del servidor UDP
public class UDPServer {
    private DatagramSocket socket;
    private ConcurrentLinkedQueue<GameSession> gameQueue;

    // Constructor del servidor que inicializa el socket y la cola de partidas
    public UDPServer(int port) throws SocketException {
        socket = new DatagramSocket(port);
        gameQueue = new ConcurrentLinkedQueue<>();
    }

    // Método para comenzar a escuchar peticiones
    public void listen() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (true) {
            try {
                // Recibir paquete UDP
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());

                // Manejar mensaje recibido
                handleMessage(received, packet);
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
                // Manejar excepción
            }
        }
    }

    // Método para manejar los mensajes recibidos
    private void handleMessage(String message, DatagramPacket packet) {
        String[] parts = message.split(" ");
        String messageType = parts[0];
        
        switch (messageType) {
            case "CREAR":
                handleCreate(message, packet);
                break;
            case "UNIR-ME":
                handleJoin(packet);
                break;
            default:
                // Enviar error o manejar otros mensajes
                break;
        }
    }

    // Métodos para manejar la creación y unión de partidas
    private void handleCreate(String message, DatagramPacket packet) {
        // Extraer puerto del juego del mensaje
        int gamePort = Integer.parseInt(message.split(" ")[1]);
        // Crear una nueva sesión de juego
        GameSession newSession = new GameSession(packet.getAddress(), gamePort);
        // Añadir a la cola de juegos
        gameQueue.add(newSession);
        // Enviar una respuesta de OK
        sendResponse("OK", packet.getAddress(), packet.getPort());
    }

    private void handleJoin(DatagramPacket packet) {
        GameSession session = gameQueue.poll();
        if (session != null) {
            // Formato: ip_partida::port_partida
            String response = session.getIpAddress().toString().substring(1) + "::" + session.getPort();
            sendResponse(response, packet.getAddress(), packet.getPort());
        } else {
            // No hay juegos disponibles
            sendResponse("ESPERA", packet.getAddress(), packet.getPort());
        }
    }

    // Método para enviar respuestas a los clientes
    private void sendResponse(String message, InetAddress address, int port) {
        try {
            byte[] buffer = message.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(responsePacket);
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            
        }
    }

    // Punto de entrada del programa
    public static void main(String[] args) {
        try {
            UDPServer server = new UDPServer(7879);
            server.listen();
        } catch (SocketException e) {
            System.out.println("SocketException: " + e.getMessage());
        }
    }
}
    
    
    
    

