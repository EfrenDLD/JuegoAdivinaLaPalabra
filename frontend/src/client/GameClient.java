
// frontend/src/client/GameClient.java
package client;
// frontend/src/client/GameClient.java
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class GameClient {
    private static final String SERVER_IP = "localhost";
    /*private static final String SERVER_IP = "172.25.3.67";*/

    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println(input.readLine());

            new Thread(() -> {
                try {
                    String response;
                    while ((response = input.readLine()) != null) {
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            while (true) {
                System.out.print("Ingresa tu respuesta: ");
                String guess = scanner.nextLine();
                output.println(guess);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}