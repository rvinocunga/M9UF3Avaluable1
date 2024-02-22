package TCP;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServidorPartida implements Runnable {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private Scanner in;
    private char[][] tablero;
    private char jugadorActual;
    private boolean juegoActivo;

    public ServidorPartida(int puerto) throws IOException {
        serverSocket = new ServerSocket(puerto);
        tablero = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tablero[i][j] = '-';
            }
        }
        jugadorActual = 'X'; // 'X' siempre inicia
        juegoActivo = true;
    }

    @Override
    public void run() {
        try {
            System.out.println("Esperando al jugador en el puerto " + serverSocket.getLocalPort());
            clientSocket = serverSocket.accept();
            System.out.println("Jugador conectado: " + clientSocket.getInetAddress());

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new Scanner(clientSocket.getInputStream());

            enviarTablero();

            String inputLine;
            while (juegoActivo && (inputLine = in.nextLine()) != null) {
                procesarMovimiento(inputLine);
                if (juegoActivo) {
                    enviarTablero();
                }
            }
        } catch (IOException e) {
            System.err.println("Exception caught when trying to listen on port "
                    + serverSocket.getLocalPort() + " or listening for a connection");
            System.err.println(e.getMessage());
        } finally {
            cerrarConexion();
        }
    }

    private void procesarMovimiento(String mensaje) {
        if (mensaje.startsWith("MOVIMIENTO")) {
            int fila = Character.getNumericValue(mensaje.charAt(9));
            int columna = Character.getNumericValue(mensaje.charAt(11));
            if (movimientoValido(fila, columna)) {
                tablero[fila][columna] = jugadorActual;
                if (hayGanador()) {
                    juegoActivo = false;
                    out.println("GANADOR " + jugadorActual);
                } else if (esEmpate()) {
                    juegoActivo = false;
                    out.println("EMPATE");
                } else {
                    alternarJugador();
                }
            } else {
                out.println("MOVIMIENTO INVALIDO");
            }
        }
    }

    private boolean movimientoValido(int fila, int columna) {
        return fila >= 0 && fila < 3 && columna >= 0 && columna < 3 && tablero[fila][columna] == '-';
    }

    private boolean hayGanador() {
        // Comprobar filas
        for (int i = 0; i < 3; i++) {
            if (tablero[i][0] == jugadorActual
                    && tablero[i][1] == jugadorActual
                    && tablero[i][2] == jugadorActual) {
                return true;
            }
        }

        // Comprobar columnas
        for (int j = 0; j < 3; j++) {
            if (tablero[0][j] == jugadorActual
                    && tablero[1][j] == jugadorActual
                    && tablero[2][j] == jugadorActual) {
                return true;
            }
        }

        // Comprobar diagonales
        if (tablero[0][0] == jugadorActual
                && tablero[1][1] == jugadorActual
                && tablero[2][2] == jugadorActual) {
            return true;
        }
        if (tablero[0][2] == jugadorActual
                && tablero[1][1] == jugadorActual
                && tablero[2][0] == jugadorActual) {
            return true;
        }

        // Si no se cumple ninguna de las condiciones anteriores, no hay ganador aún
        return false;
    }

    private boolean esEmpate() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tablero[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    private void alternarJugador() {
        jugadorActual = (jugadorActual == 'X') ? 'O' : 'X';
    }

    private void enviarTablero() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append(" ").append(tablero[i][j]).append(" ");
                if (j < 2) {
                    sb.append("|");
                }
            }
            if (i < 2) {
                sb.append("\n---+---+---\n");
            }
        }
        sb.append("\n");
        out.println(sb.toString());
        out.flush();
    }

    private void cerrarConexion() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Uso: java ServidorPartida <puerto>");
            return;
        }
        int puerto = Integer.parseInt(args[0]);
        try {
            ServidorPartida servidor = new ServidorPartida(puerto);
            new Thread(servidor).start();
        } catch (IOException e) {
            System.err.println("No se pudo abrir el puerto: " + puerto);
            e.printStackTrace();
        }
    }
}
