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
    private int attempts = 5;   // Intentos restantes
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
            output.println("Bienvenido al juego amigo:");
            playerName = input.readLine();
    
            synchronized (clients) {
                broadcast("Jugador " + playerName + " se ha unido al juego.");
            
                if (clients.size() >= 2) {
                    String hint = gameManager.getHint();
                    broadcast("Comienza el juegooo La pista es: " + hint);
                } else {
                    output.println("Esperando a otro jugador...");
                }
            }
    
            // Bucle del juego
            while (attempts > 0) {
                String guess = input.readLine();
                if (guess == null) {
                    break; // Cliente desconectado
                }
    
                String response = gameManager.checkGuess(guess);
                attempts--; // Reducir intentos
    
                // Si el jugador acierta
                if (response.contains("¡Correcto!")) {
                    broadcast(response + " " + playerName + " ha ganado con " + attempts + " intentos restantes.");
                    return;  // Finalizar juego al acertar
                } else {
                    broadcast(response + " | " + playerName + " | Intentos restantes: " + attempts);
                }
    
                // Si el jugador agota intentos
                if (attempts == 0) {
                    broadcast("¡" + playerName + " ha perdido!");
                    checkGameOver();  // Verificar si todos los jugadores perdieron
                    return;
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
            synchronized (clients) {
                clients.remove(this);
                broadcast("Jugador " + playerName + " ha salido del juego.");
                checkGameOver(); // Revisar si el juego debe terminar
            }
        }
    }

    private void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.output.println(message);
        }
    }

    private void checkGameOver() {
        synchronized (clients) {
            boolean allLost = clients.stream().allMatch(client -> client.attempts == 0);
            if (allLost) {
                broadcast("¡Nadie adivinó la palabra! La palabra era: " + gameManager.getSecretWord());
            }
        }
    }
}
