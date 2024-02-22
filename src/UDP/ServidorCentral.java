
package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorCentral {
    private DatagramSocket socket;
    private ConcurrentLinkedQueue<String> colaPartidas;
    private ConcurrentHashMap<String, String> partidasActivas;
    private ExecutorService executorService;

    public ServidorCentral(int port) throws IOException {
        socket = new DatagramSocket(port);
        colaPartidas = new ConcurrentLinkedQueue<>();
        partidasActivas = new ConcurrentHashMap<>();
        executorService = Executors.newCachedThreadPool();
    }

    public void escuchar() {
        System.out.println("Servidor Central UDP escuchando en el puerto " + socket.getLocalPort());

        while (true) {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                executorService.submit(() -> procesarSolicitud(packet));
            } catch (IOException e) {
                System.err.println("Error al recibir el paquete UDP: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void procesarSolicitud(DatagramPacket packet) {
        try {
            String mensaje = new String(packet.getData(), 0, packet.getLength());
            String[] partes = mensaje.split(" ");
            String comando = partes[0].toUpperCase();
            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            switch (comando) {
                case "CREAR":
                    if (partes.length > 1) {
                        String portJuego = partes[1];
                        String partidaInfo = address.getHostAddress() + "::" + portJuego;
                        if (!partidasActivas.containsKey(partidaInfo)) {
                            colaPartidas.add(partidaInfo);
                            partidasActivas.put(partidaInfo, "ESPERA");
                            enviarMensaje("OK", address, port);
                        } else {
                            enviarMensaje("ERROR Partida ya registrada con esa IP y puerto", address, port);
                        }
                    } else {
                        enviarMensaje("ERROR Comando CREAR inválido", address, port);
                    }
                    break;
                case "UNIR-ME":
                    String partida = colaPartidas.poll();
                    if (partida != null) {
                        partidasActivas.remove(partida);
                        enviarMensaje(partida, address, port);
                    } else {
                        enviarMensaje("ERROR No hay partidas disponibles", address, port);
                    }
                    break;
                default:
                    enviarMensaje("ERROR Comando no reconocido", address, port);
                    break;
            }
        } catch (IOException e) {
            System.err.println("Error al procesar la solicitud: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void enviarMensaje(String mensaje, InetAddress address, int port) throws IOException {
        byte[] buffer = mensaje.getBytes();
        DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length, address, port);
        socket.send(respuesta);
    }

    public static void main(String[] args) {
        try {
            int puerto = 7879;
            ServidorCentral servidor = new ServidorCentral(puerto);
            servidor.escuchar();
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor central: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
