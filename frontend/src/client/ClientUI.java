package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ClientUI {
    private JFrame frame;
    private JTextField inputField;   // Campo de entrada de respuesta
    private JTextArea chatArea;      // Área de chat para mostrar mensajes
    private PrintWriter output;      // Escritor para enviar datos al servidor
    private JTextField nameField;    // Campo de nombre
    private Socket socket;           // Socket para la conexión al servidor

    public ClientUI() {
        frame = new JFrame("Adivina la Palabra");
        inputField = new JTextField(20);  // Campo de entrada para la respuesta
        chatArea = new JTextArea(10, 30); // Área de texto para mostrar las respuestas y mensajes
        chatArea.setEditable(false);

        JButton sendButton = new JButton("Enviar Respuesta");  // Botón para enviar la respuesta

        nameField = new JTextField(20);  // Campo para el nombre del jugador
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

        // Cuando se hace click en "Comenzar", se conecta al servidor
        startButton.addActionListener(e -> {
            if (!nameField.getText().isEmpty()) {
                try {
                    socket = new Socket("172.25.3.48", 12345);  // Dirección IP y puerto del servidor
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    output = new PrintWriter(socket.getOutputStream(), true);

                    // Enviar el nombre del jugador al servidor
                    output.println(nameField.getText());

                    // Iniciar un hilo para recibir mensajes del servidor
                    new Thread(() -> {
                        try {
                            String response;
                            while ((response = input.readLine()) != null) {
                                chatArea.append(response + "\n");  // Mostrar la respuesta del servidor en el chat
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }).start();

                    // Ocultar el panel de nombre y mostrar el campo de respuesta
                    panel.setVisible(false);
                    frame.add(inputField, BorderLayout.SOUTH);  // Agregar campo de ingreso
                    frame.add(sendButton, BorderLayout.EAST);   // Agregar el botón de envío
                    frame.revalidate();
                    frame.repaint();

                    sendButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String response = inputField.getText();
                            if (!response.isEmpty()) {
                                output.println(response);  // Enviar la respuesta al servidor
                                inputField.setText("");  // Limpiar el campo de entrada después de enviar
                            }
                        }
                    });

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        new ClientUI();  // Iniciar la interfaz del cliente
    }
}
