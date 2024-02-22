package Clinte;

import TCP.ServidorPartida;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    private static final String SERVER_ADDRESS = "localhost"; // Ajusta a la dirección del servidor central
    private static final int SERVER_PORT = 7879; // Puerto del servidor central UDP

    public static void main(String[] args) {
        try ( Scanner scanner = new Scanner(System.in)) {
            int opcion = 0;
            do {
                System.out.println("\nBienvenido al Tres en Raya en Red");
                System.out.println("1. Crear partida nueva");
                System.out.println("2. Unirse a partida existente");
                System.out.println("3. Salir");
                System.out.print("Elige una opción: ");
                opcion = scanner.nextInt();

                switch (opcion) {
                    case 1:
                        crearPartida(scanner);
                        break;
                    case 2:
                        unirseAPartida(scanner);
                        break;
                    case 3:
                        System.out.println("Saliendo del juego...");
                        break;
                    default:
                        System.out.println("Opción no válida.");
                        break;
                }
            } while (opcion != 3);
        } catch (Exception e) {
            System.err.println("Se produjo un error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void crearPartida(Scanner scanner) throws IOException {
        DatagramSocket socketUDP = new DatagramSocket();
        InetAddress address = InetAddress.getByName(SERVER_ADDRESS);

        System.out.println("Introduce el puerto para tu partida: ");
        int puertoPartida = scanner.nextInt();
        String mensaje = "CREAR " + puertoPartida;
        byte[] mensajeBytes = mensaje.getBytes();

        DatagramPacket paquete = new DatagramPacket(mensajeBytes, mensajeBytes.length, address, SERVER_PORT);
        socketUDP.send(paquete);

        byte[] buffer = new byte[1024];
        DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length);
        socketUDP.receive(respuesta);
        String respuestaStr = new String(respuesta.getData(), 0, respuesta.getLength());
        System.out.println("Respuesta del servidor: " + respuestaStr);

        if (respuestaStr.equals("OK")) {
            System.out.println("Esperando al oponente...");
            ServidorPartida servidorPartida = new ServidorPartida(puertoPartida);
            new Thread(servidorPartida).start();
        }

        socketUDP.close();
    }

    private static void unirseAPartida(Scanner scanner) throws IOException {
        DatagramSocket socketUDP = new DatagramSocket();
        InetAddress address = InetAddress.getByName(SERVER_ADDRESS);

        String mensaje = "UNIR-ME";
        byte[] mensajeBytes = mensaje.getBytes();

        DatagramPacket paquete = new DatagramPacket(mensajeBytes, mensajeBytes.length, address, SERVER_PORT);
        socketUDP.send(paquete);

        byte[] buffer = new byte[1024];
        DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length);
        socketUDP.receive(respuesta);
        String respuestaStr = new String(respuesta.getData(), 0, respuesta.getLength());
        System.out.println("Respuesta del servidor: " + respuestaStr);

        if (!respuestaStr.equals("ESPERA") && !respuestaStr.startsWith("ERROR")) {
            String[] partesRespuesta = respuestaStr.split("::");
            String ipPartida = partesRespuesta[0];
            int puertoPartida = Integer.parseInt(partesRespuesta[1]);
            jugarPartida(ipPartida, puertoPartida);
        } else {
            System.out.println("No se pudo unir a una partida: " + respuestaStr);
        }

        socketUDP.close();
    }

    private static void jugarPartida(String ip, int puerto) {
        try ( Socket socket = new Socket(ip, puerto);  PrintWriter out = new PrintWriter(socket.getOutputStream(), true);  BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  Scanner scanner = new Scanner(System.in)) {

            String delServidor;
            while ((delServidor = in.readLine()) != null) {
                System.out.println("Servidor dice: " + delServidor);
                if (delServidor.startsWith("GANADOR") || delServidor.equals("EMPATE")) {
                    break;
                }
                System.out.print("Tu movimiento (fila columna): ");
                String movimiento = scanner.nextLine();
                out.println("MOVIMIENTO " + movimiento);
            }
        } catch (IOException e) {
            System.err.println("No se pudo conectar al servidor de la partida: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
