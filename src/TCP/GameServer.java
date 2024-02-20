
package TCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer implements Runnable {
    private ServerSocket serverSocket;

    public GameServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Servidor de Partida iniciat al port " + port);
    }

    @Override
    public void run() {
        try (Socket clientSocket = serverSocket.accept();
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            System.out.println("Client connectat: " + clientSocket.getInetAddress());

            // Aquí va la lògica de la partida: intercanviar missatges, actualitzar estat, etc.

        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port or listening for a connection");
            System.out.println(e.getMessage());
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("No es va poder tancar el ServerSocket: " + e.getMessage());
            }
        }
    }

    public void startGameServer() {
        new Thread(this).start();
    }
}
