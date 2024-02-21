package TCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer implements Runnable {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private char[] board;
    private char jugadorActual;
    private boolean gameRunning;

    public GameServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        board = new char[9];
        for (int i = 0; i < board.length; i++) {
            board[i] = (char) (i + '1');
        }
        jugadorActual = 'X';
        gameRunning = true;
        System.out.println("Servidor de Partida iniciat al port " + port);
    }

    @Override
    public void run() {
        try {
            System.out.println("Esperant que el jugador s'uneixi...");
            clientSocket = serverSocket.accept(); // Acceppta la conexion del client
            System.out.println("Jugador connectat: " + clientSocket.getInetAddress());

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("BENVINGUT al Tres en Ratlla.");
            out.println("Ets el jugador 'O'.");
            out.println("Esperant que l'altre jugador faci el primer moviment...");

            String inputLine;
            while (gameRunning) {
                inputLine = in.readLine();
                if (inputLine != null) {
                    System.out.println("Client diu: " + inputLine);
                    processInput(inputLine);
                }
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port or listening for a connection");
            System.out.println(e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    private boolean validarHacerMovimiento(int move) {
        if (move >= 0 && move < board.length && board[move] == (char) (move + '1')) {
            board[move] = jugadorActual;
            return true;
        }
        return false;
    }

    private boolean checkForWin() {
        int[][] winConditions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // horitzontal
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // vertical
            {0, 4, 8}, {2, 4, 6} // diagonal
        };

        for (int[] condition : winConditions) {
            if (board[condition[0]] == board[condition[1]]
                    && board[condition[1]] == board[condition[2]]) {
                return true;
            }
        }

        return false;
    }

    private boolean isBoardFull() {
        for (char square : board) {
            if (square != 'X' && square != 'O') {
                return false;
            }
        }
        return true;
    }

    private void sendBoard() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < board.length; i++) {
            sb.append(board[i]);
            if ((i + 1) % 3 == 0) {
                sb.append("\n");
            } else {
                sb.append("|");
            }
        }
        out.println(sb.toString());
    }

    private void togglePlayer() {
        jugadorActual = (jugadorActual == 'X') ? 'O' : 'X';
    }

    private void processInput(String inputLine) {
        if (inputLine.startsWith("MOVIMENT ")) {
            int move = Integer.parseInt(inputLine.split(" ")[1]) - 1;
            if (validarHacerMovimiento(move)) {
                if (checkForWin()) {
                    out.println("GUANYA " + jugadorActual);
                    gameRunning = false;
                } else if (isBoardFull()) {
                    out.println("EMPAT");
                    gameRunning = false;
                } else {
                    togglePlayer();
                }
                sendBoard();
            } else {
                out.println("MOVIMENT_INVALID");
            }
        } else if (inputLine.equals("SORTIR")) {
            out.println("ADEU");
            gameRunning = false;
        }
    }

    private void cerrarRecursos() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error while closing game resources: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = 7879; 
        try {
            GameServer gameServer = new GameServer(port);
            new Thread(gameServer).start();
        } catch (IOException e) {
            System.out.println("No es pot iniciar el servidor de jocs: " + e.getMessage());
        }
    }
}
