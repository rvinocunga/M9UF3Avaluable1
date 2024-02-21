
package TCP;

import java.io.*;
import java.net.Socket;

public class GameClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void enviarMensaje(String msg) throws IOException {
        out.println(msg);
    }

    public String recibirMensaje() throws IOException {
        return in.readLine();
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error tancant la connexió del client: " + e.getMessage());
        }
    }
}
