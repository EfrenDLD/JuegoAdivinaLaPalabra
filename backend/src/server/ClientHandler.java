package src.server;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private List<ClientHandler> clients;
    private GameManager gameManager;
    private int attempts = 5;
    private String playerName;  // Nombre del jugador

    public ClientHandler(Socket socket, List<ClientHandler> clients, GameManager gameManager) {
        this.socket = socket;
        this.clients = clients;
        this.gameManager = gameManager;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Solicitar el nombre del jugador
            output.println("Por favor, ingresa tu nombre: ");
            playerName = input.readLine();
            output.println("Bienvenido " + playerName + "! " + gameManager.getHint());

            while (attempts > 0) {
                String guess = input.readLine();
                if (guess == null) {
                    break; // El cliente se ha desconectado
                }
                String response = gameManager.checkGuess(guess);
                attempts--;
                if (response.contains("¡Correcto!")) {
                    broadcast(response + " " + playerName + " ha ganado!");
                    break;
                } else {
                    broadcast(response + " | Intentos restantes: " + attempts);
                }
                if (attempts == 0) {
                    broadcast("¡Has perdido! La palabra era: " + gameManager.getSecretWord());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { 
                socket.close(); 
            } catch (IOException e) { 
                e.printStackTrace(); 
            }
            clients.remove(this);
        }
    }

    private void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.output.println(message);
        }
    }

    public String getPlayerName() {
        return playerName;
    }
}
