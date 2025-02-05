// frontend/src/client/ClientUI.java
package client;

// frontend/src/client/ClientUI.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ClientUI {
    private JFrame frame;
    private JTextField inputField;
    private JTextArea chatArea;
    private PrintWriter output;

    public ClientUI() {
        frame = new JFrame("Adivina la Palabra");
        inputField = new JTextField(20);
        chatArea = new JTextArea(10, 30);
        chatArea.setEditable(false);
        JButton sendButton = new JButton("Enviar");

        JPanel panel = new JPanel();
        panel.add(inputField);
        panel.add(sendButton);

        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        sendButton.addActionListener(e -> {
            output.println(inputField.getText());
            inputField.setText("");
        });

        try {
            Socket socket = new Socket("172.25.3.67", 12345);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                try {
                    String response;
                    while ((response = input.readLine()) != null) {
                        chatArea.append(response + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ClientUI();
    }
}
