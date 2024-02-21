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
    private ConcurrentLinkedQueue<GameSession> colaDeJuegos;

    // Constructor del servidor que inicializa el socket y la cola de partidas
    public UDPServer(int puerto) throws SocketException {
        socket = new DatagramSocket(puerto);
        colaDeJuegos = new ConcurrentLinkedQueue<>();
    }

    // Método para comenzar a escuchar peticiones
    public void escuchar() {
        byte[] buffer = new byte[1024];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);

        while (true) {
            try {
                // Recibir paquete UDP
                socket.receive(paquete);
                String recibido = new String(paquete.getData(), 0, paquete.getLength());

                // Manejar mensaje recibido
                manejarMensaje(recibido, paquete);
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
                // Manejar excepción
            }
        }
    }

    // Método para manejar los mensajes recibidos
    private void manejarMensaje(String mensaje, DatagramPacket paquete) {
        String[] partes = mensaje.split(" ");
        String tipoDeMensaje = partes[0];
        
        switch (tipoDeMensaje) {
            case "CREAR":
                manejarCrear(mensaje, paquete);
                break;
            case "UNIR-ME":
                manejarUnirse(paquete);
                break;
            default:
                // Enviar error o manejar otros mensajes
                break;
        }
    }

    // Métodos para manejar la creación y unión de partidas
    private void manejarCrear(String mensaje, DatagramPacket paquete) {
        // Extraer puerto del juego del mensaje
        int puertoDelJuego = Integer.parseInt(mensaje.split(" ")[1]);
        // Crear una nueva sesión de juego
        GameSession nuevaSesion = new GameSession(paquete.getAddress(), puertoDelJuego);
        // Añadir a la cola de juegos
        colaDeJuegos.add(nuevaSesion);
        // Enviar una respuesta de OK
        enviarRespuesta("OK", paquete.getAddress(), paquete.getPort());
    }

    private void manejarUnirse(DatagramPacket paquete) {
        GameSession sesion = colaDeJuegos.poll();
        if (sesion != null) {
            // Formato: ip_partida::port_partida
            String respuesta = sesion.getIpAddress().toString().substring(1) + "::" + sesion.getPort();
            enviarRespuesta(respuesta, paquete.getAddress(), paquete.getPort());
        } else {
            // No hay juegos disponibles
            enviarRespuesta("ESPERA", paquete.getAddress(), paquete.getPort());
        }
    }

    // Método para enviar respuestas a los clientes
    private void enviarRespuesta(String mensaje, InetAddress direccion, int puerto) {
        try {
            byte[] buffer = mensaje.getBytes();
            DatagramPacket paqueteRespuesta = new DatagramPacket(buffer, buffer.length, direccion, puerto);
            socket.send(paqueteRespuesta);
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    // Punto de entrada del programa
    public static void main(String[] args) {
        try {
            UDPServer servidor = new UDPServer(7879);
            servidor.escuchar();
        } catch (SocketException e) {
            System.out.println("SocketException: " + e.getMessage());
        }
    }
}
