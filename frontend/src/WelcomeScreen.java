import client.ClientUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class WelcomeScreen {
    private JFrame frame;
    
    public WelcomeScreen() {
        frame = new JFrame("Bienvenido al juego");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(30, 30, 30));

        // Título
        JLabel titleLabel = new JLabel("Bienvenido al juego de", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        frame.add(titleLabel, BorderLayout.NORTH);

        // Imagen de referencia
        ImageIcon icon = new ImageIcon("AdivinaLaPalabra/frontend/src/images/logo.png");
        JLabel imageLabel = new JLabel(icon);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(imageLabel, BorderLayout.CENTER);

        // Botón de continuar
        JButton continueButton = new JButton("Continuar");
        continueButton.setFont(new Font("Verdana", Font.BOLD, 16));
        continueButton.setBackground(new Color(0, 153, 255));
        continueButton.setForeground(Color.WHITE);
        continueButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        continueButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Cerrar pantalla de bienvenida
                new ClientUI(); // Abrir pantalla de ingreso del nombre
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(30, 30, 30));
        buttonPanel.add(continueButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new WelcomeScreen();
    }
}
