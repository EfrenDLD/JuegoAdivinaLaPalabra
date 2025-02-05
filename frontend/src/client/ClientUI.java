package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import javax.swing.*;

public class ClientUI {
    private JFrame frame;
    private JTextField inputField;
    private JTextArea chatArea;
    private PrintWriter output;
    private JTextField nameField;
    private Socket socket;

    public ClientUI() {
        frame = new JFrame("Adivina la Palabra");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(new Color(30, 30, 30));
        frame.setLayout(new BorderLayout());

        Font font = new Font("Verdana", Font.PLAIN, 14);

        chatArea = new JTextArea(12, 40);
        chatArea.setEditable(false);
        chatArea.setFont(font);
        chatArea.setBackground(new Color(50, 50, 50));
        chatArea.setForeground(Color.WHITE);
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        chatArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        inputField = new JTextField(20);
        inputField.setFont(font);
        inputField.setBackground(new Color(70, 70, 70));
        inputField.setForeground(Color.WHITE);
        inputField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton sendButton = new JButton("Enviar");
        sendButton.setFont(font);
        sendButton.setBackground(new Color(0, 153, 255));
        sendButton.setForeground(Color.WHITE);
        sendButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(30, 30, 30));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        nameField = new JTextField(15);
        nameField.setFont(font);
        nameField.setBackground(new Color(70, 70, 70));
        nameField.setForeground(Color.WHITE);
        nameField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton startButton = new JButton("Comenzar");
        startButton.setFont(font);
        startButton.setBackground(new Color(0, 204, 102));
        startButton.setForeground(Color.WHITE);
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(30, 30, 30));
        topPanel.add(new JLabel("Nombre: ", SwingConstants.CENTER));
        topPanel.add(nameField);
        topPanel.add(startButton);
        
        frame.add(topPanel, BorderLayout.NORTH);

        frame.setVisible(true);

        startButton.addActionListener(e -> {
            if (!nameField.getText().isEmpty()) {
                try {
                    socket = new Socket("172.25.3.48", 12345);
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    output = new PrintWriter(socket.getOutputStream(), true);
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

                    topPanel.setVisible(false);
                    frame.add(inputPanel, BorderLayout.SOUTH);
                    frame.revalidate();
                    frame.repaint();

                    sendButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String response = inputField.getText();
                            if (!response.isEmpty()) {
                                output.println(response);
                                inputField.setText("");
                            }
                        }
                    });
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Ingresa un nombre para comenzar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        new ClientUI();
    }
}