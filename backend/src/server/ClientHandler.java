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
            output.println("Por favor, ingresa tu nombre: ");
            playerName = input.readLine();
            
            // Verificar si hay al menos dos jugadores conectados
            synchronized (clients) {
                if (clients.size() < 2) {
                    output.println("Esperando a que otro jugador se conecte para comenzar...");
                    while (clients.size() < 2) {
                        clients.wait();  // Esperar hasta que otro jugador se conecte
                    }
                    // Notificar al primer jugador que el segundo jugador se ha conectado
                    for (ClientHandler client : clients) {
                        if (!client.getPlayerName().equals(playerName)) {
                            client.output.println(playerName + " se acaba de conectar. ¡Pueden comenzar a jugar!");
                        }
                    }
                }
            }

            output.println("Bienvenido " + playerName + "! " + gameManager.getHint());

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
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try { 
                socket.close(); 
            } catch (IOException e) { 
                e.printStackTrace(); 
            }
            synchronized (clients) {
                clients.remove(this);  // Eliminar cliente de la lista de clientes
                if (clients.size() > 1) {
                    clients.notify();  // Notificar a los demás jugadores que ya hay 2 jugadores
                }
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
