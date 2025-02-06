package client;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class ClientUI {
    private JFrame frame; //ventana principal
    private JTextField inputField;
    private JTextArea chatArea;
    private PrintWriter output;
    private JTextField nameField;
    private Socket socket;

    public ClientUI() {
        //Configuración de la ventana principal (JFrame)
        frame = new JFrame("Adivina la Palabra");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(new Color(30, 30, 30));
        frame.setLayout(new BorderLayout());

        //Configuración del área de chat (JTextArea)
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

        //Configuración del campo de entrada de texto (JTextField) y botón de envío
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

        //Panel para la entrada de texto y botón de envío
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

        //Configuración del nombre del usuario
        JButton startButton = new JButton("Comenzar");
        startButton.setFont(font);
        startButton.setBackground(new Color(0, 204, 102));
        startButton.setForeground(Color.WHITE);
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        //Panel para el nombre y botón de inicio
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(30, 30, 30));
        topPanel.add(new JLabel("Nombre: ", SwingConstants.CENTER));

        topPanel.add(nameField);
        topPanel.add(startButton);
        
        frame.add(topPanel, BorderLayout.NORTH);

        frame.setVisible(true);

        //Si el usuario ingresa un nombre, crea un socket para conectarse al servidor 192.168.56.1 en el puerto 12345
        startButton.addActionListener(e -> {
            if (!nameField.getText().isEmpty()) {
                try {
                    socket = new Socket("192.168.56.1", 12345);
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    output = new PrintWriter(socket.getOutputStream(), true);
                    output.println(nameField.getText());
                    reproducirSonido("AdivinaLaPalabra/frontend/src/client/holaa.wav"); // Sonido al entrar al juego

                    //Se crea un hilo (Thread) que lee continuamente los mensajes del servidor y los muestra en el área de chat
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
                                reproducirSonido("AdivinaLaPalabra/frontend/src/client/agrega.wav"); // Sonido al entrar al juego
                            }
                        }
                    });
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Por favor, ingrese su nombre", "Error", JOptionPane.ERROR_MESSAGE);
                reproducirSonido("AdivinaLaPalabra/frontend/src/client/noes.wav");
            }
        });
    }

    public static void main(String[] args) {
        new ClientUI();
    }

        // Método para reproducir un sonid
    public static void reproducirSonido(String ruta) {
        try {
            File archivoSonido = new File(ruta);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivoSonido);

            // Obtener formato original
            AudioFormat formatoOriginal = audioStream.getFormat();
            AudioFormat formatoCompatible = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED, // Convertir a PCM_SIGNED
                    formatoOriginal.getSampleRate(),
                    16, // Convertir a 16 bits
                    formatoOriginal.getChannels(),
                    formatoOriginal.getChannels() * 2,
                    formatoOriginal.getSampleRate(),
                    false);

            // Convertir el audio
            AudioInputStream audioConvertido = AudioSystem.getAudioInputStream(formatoCompatible, audioStream);

            Clip clip = AudioSystem.getClip();
            clip.open(audioConvertido);
            clip.start();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}