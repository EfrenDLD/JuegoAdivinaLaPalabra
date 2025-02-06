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
            output.println("Bienvenido al juego:");
            playerName = input.readLine();
    
            synchronized (clients) {
                // Notificar a todos los jugadores que se ha unido uno nuevo
                broadcast("Jugador  " + playerName + " se ha unido al juego.");
            
                // Si hay al menos dos jugadores, enviar la pista a todos
                if (clients.size() >= 2) {
                    String hint = gameManager.getHint();
                    broadcast("¡Comienza el juego! La pista es: " + hint);
                } else {
                    output.println("Esperando a otro jugador...");
                }
            }
    
            // Bucle del juego
            while (attempts > 0) {
                String guess = input.readLine();
                if (guess == null) {
                    break; // El cliente se ha desconectado
                }
    
                String response = gameManager.checkGuess(guess); // Verificar la adivinanza
                attempts--; // Reducir intentos después de cada intento
    
                // Si el jugador acierta
                if (response.contains("¡Correcto!")) {
                    broadcast(response + " " + playerName + " ha ganado con " + attempts + " intentos restantes.");
                    break;  // Detener el juego
                } else {
                    broadcast(response + " | " + playerName + " | Intentos restantes: " + attempts);
                }
    
                // Si el jugador ha agotado los intentos
                if (attempts == 0) {
                    broadcast("¡" + playerName + " ha perdido! La palabra era: " + gameManager.getSecretWord());
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
                clients.remove(this);  // Eliminar cliente de la lista de clientes
                broadcast("Jugador " + playerName + " ha salido del juego.");
            }
        }
    }

    private void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.output.println(message);  // Enviar mensaje a todos los clientes
        }
    }

    public String getPlayerName() {
        return playerName;
    }
    
}