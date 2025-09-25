package Africa;

import java.io.*;
import java.net.*;

public class AfricaCapitalClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;

        try (Socket socket = new Socket(host, port);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println(" Connecté au serveur " + socket.getRemoteSocketAddress());

            // Lire le message de bienvenue
            System.out.println(in.readLine());

            String userInput;
            while ((userInput = console.readLine()) != null) {
                out.println(userInput); // envoyer au serveur
                String response = in.readLine();
                if (response == null)
                    break;
                System.out.println("🖥️ Réponse du serveur: " + response);

                if (response.contains("Bye")) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
