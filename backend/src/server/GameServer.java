package src.server;
// backend/src/server/GameServer.java
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameServer {
    private static final int PORT = 12345;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static GameManager gameManager = new GameManager();

    public static void main(String[] args) {
        // se usa socket para esperar conexiones de clientes
        System.out.println("Servidor iniciado en el puerto " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName("0.0.0.0"));) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo jugador conectado!");
                ClientHandler clientHandler = new ClientHandler(clientSocket, clients, gameManager);
                clients.add(clientHandler);
                //para manejar cada cliente en un hilo nuevo
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}