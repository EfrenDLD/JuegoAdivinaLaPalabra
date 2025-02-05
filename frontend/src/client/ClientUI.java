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
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);  // Centra la ventana en la pantalla

        // Estilo de los componentes
        Font font = new Font("Arial", Font.PLAIN, 14);

        // Área de chat
        chatArea = new JTextArea(10, 30);
        chatArea.setEditable(false);
        chatArea.setFont(font);
        chatArea.setBackground(new Color(240, 240, 240));
        chatArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Campo de entrada de respuesta
        inputField = new JTextField(20);
        inputField.setFont(font);
        inputField.setBackground(new Color(255, 255, 255));
        inputField.setForeground(new Color(0, 0, 0));
        inputField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));

        // Botón "Enviar Respuesta"
        JButton sendButton = new JButton("Enviar Respuesta");
        sendButton.setFont(font);
        sendButton.setBackground(new Color(0, 123, 255));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Panel para el campo de respuesta y el botón "Enviar"
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Campo para el nombre del jugador
        nameField = new JTextField(20);
        nameField.setFont(font);
        nameField.setBackground(new Color(255, 255, 255));
        nameField.setForeground(new Color(0, 0, 0));
        nameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));

        // Botón "Comenzar"
        JButton startButton = new JButton("Comenzar");
        startButton.setFont(font);
        startButton.setBackground(new Color(40, 167, 69));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Panel para el nombre del jugador y el botón "Comenzar"
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(new JLabel("Ingresa tu nombre:"));
        panel.add(nameField);
        panel.add(startButton);
        frame.add(panel, BorderLayout.NORTH);

        // Mostrar la interfaz
        frame.setVisible(true);

        // Acción cuando se hace click en "Comenzar"
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
                    frame.add(inputPanel, BorderLayout.SOUTH);  // Agregar el panel de entrada con el botón
                    frame.revalidate();
                    frame.repaint();

                    // Acción cuando se presiona el botón "Enviar Respuesta"
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
            } else {
                JOptionPane.showMessageDialog(frame, "Por favor, ingresa tu nombre.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        new ClientUI();  // Iniciar la interfaz del cliente
    }
}
