package ECalculator;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ECalculatorClient extends JFrame {
    private JTextField inputField;
    private JTextArea outputArea;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    public ECalculatorClient(String host, int port) {
        setTitle("E-Calculator Client");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        inputField = new JTextField();
        outputArea = new JTextArea();
        outputArea.setEditable(false);

        JButton sendButton = new JButton("Envoyer");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        add(new JScrollPane(outputArea), BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Lire le message de bienvenue
            outputArea.append(in.readLine() + "\n");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Impossible de se connecter au serveur", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        sendButton.addActionListener(e -> sendExpression());
        inputField.addActionListener(e -> sendExpression());
    }

    private void sendExpression() {
        String expression = inputField.getText();
        if (expression.isEmpty())
            return;

        out.println(expression);
        try {
            String response = in.readLine();
            if (response == null) {
                outputArea.append("❌ Le serveur a fermé la connexion.\n");
                socket.close();
                dispose();
                return;
            }
            outputArea.append(">> " + expression + "\n");
            outputArea.append(response + "\n");

            if (response.contains("❌")) {
                socket.close();
                dispose();
            }

        } catch (IOException e) {
            outputArea.append("⚠️ Erreur de communication avec le serveur.\n");
        }

        inputField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ECalculatorClient client = new ECalculatorClient("localhost", 12345);
            client.setVisible(true);
        });
    }
}
