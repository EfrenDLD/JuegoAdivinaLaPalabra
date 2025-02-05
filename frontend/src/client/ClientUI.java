package client;

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
    private JTextField nameField;  // Campo para el nombre

    public ClientUI() {
        frame = new JFrame("Adivina la Palabra");
        inputField = new JTextField(20);
        chatArea = new JTextArea(10, 30);
        chatArea.setEditable(false);
        JButton sendButton = new JButton("Enviar");

        nameField = new JTextField(20);  // Campo para nombre del jugador
        JButton startButton = new JButton("Comenzar");

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ingresa tu nombre:"));
        panel.add(nameField);
        panel.add(startButton);

        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.add(panel, BorderLayout.NORTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        startButton.addActionListener(e -> {
            if (!nameField.getText().isEmpty()) {
                try {
                    Socket socket = new Socket("172.25.3.67", 12345);
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    output = new PrintWriter(socket.getOutputStream(), true);

                    // Enviar el nombre del jugador al servidor
                    output.println(nameField.getText());

                    new Thread(() -> {
                        try {
                            String response;
                            while ((response = input.readLine()) != null) {
                                chatArea.append(response + "\n");
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }).start();

                    panel.setVisible(false);  // Ocultar la pantalla de nombre
                    frame.add(inputField, BorderLayout.SOUTH);  // Agregar campo de ingreso
                    frame.revalidate();
                    frame.repaint();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        sendButton.addActionListener(e -> {
            output.println(inputField.getText());
            inputField.setText("");
        });
    }

    public static void main(String[] args) {
        new ClientUI();
    }
}
