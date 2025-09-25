package ECalculator;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ECalculatorServer {
    private static final int PORT = 12345;
    private static final Set<String> connectedClients = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("🖥️ E-Calculator Server démarré sur le port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                String clientIP = clientSocket.getInetAddress().getHostAddress();
                int clientPort = clientSocket.getPort();
                String clientID = clientIP + ":" + clientPort;

                connectedClients.add(clientID);
                System.out.println("✅ Client connecté: " + clientID);
                System.out.println("👥 Clients connectés: " + connectedClients);

                new Thread(new ClientHandler(clientSocket, clientID)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private String clientID;

        public ClientHandler(Socket socket, String clientID) {
            this.socket = socket;
            this.clientID = clientID;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println("Bienvenue sur E-Calculator! Envoyez une expression mathématique à évaluer.");

                String expression = in.readLine();
                System.out.println("📝 Expression reçue de " + clientID + " : " + expression);

                try {
                    // Utilisation du moteur JavaScript pour évaluer
                    ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
                    Object result = engine.eval(expression);

                    out.println("Résultat = " + result);
                    System.out.println("✅ Résultat envoyé à " + clientID + " : " + result);

                } catch (ScriptException e) {
                    out.println("❌ Expression invalide. Connexion fermée.");
                    System.out.println("⚠️ Expression invalide de " + clientID + " : " + expression);
                }

            } catch (IOException e) {
                System.out.println("⚠️ Client déconnecté: " + clientID);
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }

                connectedClients.remove(clientID);
                System.out.println("👋 Session terminée pour " + clientID);
                System.out.println("👥 Clients restants: " + connectedClients);
            }
        }
    }
}
