package Africa;

import java.io.*;
import java.net.*;
import java.util.*;

// Classe principale du serveur
public class AfricaCapitalServer {
    private static final int PORT = 12345;

    // Dictionnaire (capitales africaines)
    private static final Map<String, String> capitals = new HashMap<>();
    static {
        capitals.put("rdc", "Kinshasa");
        capitals.put("angola", "Luanda");
        capitals.put("cameroun", "Yaoundé");
        capitals.put("kenya", "Nairobi");
        capitals.put("tanzanie", "Dodoma");
        capitals.put("maroc", "Rabat");
        capitals.put("egypte", "Le Caire");
        capitals.put("afrique du sud", "Pretoria");
        capitals.put("nigeria", "Abuja");
        capitals.put("ghana", "Accra");
        capitals.put("ethiopie", "Addis-Abeba");
        capitals.put("sénégal", "Dakar");
        capitals.put("algérie", "Alger");
        capitals.put("tunisie", "Tunis");
        capitals.put("libye", "Tripoli");
        capitals.put("madagascar", "Antananarivo");
        capitals.put("mozambique", "Maputo");
        capitals.put("zimbabwe", "Harare");
        capitals.put("burkina faso", "Ouagadougou");
        capitals.put("mali", "Bamako");
        capitals.put("niger", "Niamey");
        capitals.put("tchad", "N'Djamena");
        capitals.put("soudan", "Khartoum");
        capitals.put("somalie", "Mogadiscio");
        capitals.put("uganda", "Kampala");
        capitals.put("rwanda", "Kigali");
        capitals.put("burundi", "Gitega");
        capitals.put("zambie", "Lusaka");
        capitals.put("botswana", "Gaborone");
        capitals.put("namibie", "Windhoek");
        capitals.put("eswatini", "Mbabane");
        capitals.put("lesotho", "Maseru");
        capitals.put("côte d'ivoire", "Yamoussoukro");
        capitals.put("libéria", "Monrovia");
        capitals.put("sierra leone", "Freetown");
        capitals.put("guinée", "Conakry");
        capitals.put("guinée bissau", "Bissau");
        capitals.put("cap vert", "Praia");
        capitals.put("gambie", "Banjul");
        capitals.put("togo", "Lomé");
        capitals.put("bénin", "Porto-Novo");
        capitals.put("cameroun", "Yaoundé");
        capitals.put("république centrafricaine", "Bangui");
        capitals.put("république du congo", "Brazzaville");
        capitals.put("djibouti", "Djibouti");
        capitals.put("érythrée", "Asmara");
        capitals.put("guinée équatoriale", "Malabo");
        capitals.put("sao tomé et principe", "São Tomé");
        capitals.put("comores", "Moroni");
        capitals.put("seychelles", "Victoria");
        capitals.put("maurice", "Port Louis");
        capitals.put("gabon", "Libreville");
        capitals.put("angola", "Luanda");
        capitals.put("soudan du sud", "Djouba");
        capitals.put("libéria", "Monrovia");
        capitals.put("côte d'ivoire", "Yamoussoukro");
        capitals.put("république démocratique du congo", "Kinshasa");

    }

    // Liste des clients connectés
    private static final Set<String> connectedClients = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("🌍 AfricaCapital Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientAddress = clientSocket.getRemoteSocketAddress().toString();
                connectedClients.add(clientAddress);

                System.out.println(" Client connected: " + clientAddress);
                System.out.println("👥 Clients connectés: " + connectedClients);

                // Thread pour gérer le client
                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Classe qui gère un client
    static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            String clientAddress = clientSocket.getRemoteSocketAddress().toString();
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                out.println("Bienvenue sur AfricaCapital! Pose une question ex: 'Quelle est la capitale de la RDC ?'");

                String question;
                while ((question = in.readLine()) != null) {
                    System.out.println("❓ Question de " + clientAddress + " : " + question);

                    // Normaliser la question
                    String lower = question.toLowerCase();

                    boolean found = false;
                    for (Map.Entry<String, String> entry : capitals.entrySet()) {
                        if (lower.contains(entry.getKey())) {
                            out.println(
                                    " La capitale de " + entry.getKey().toUpperCase() + " est " + entry.getValue());
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        out.println("❌ Désolé, '" + question + "' n'est pas une capitale africaine connue. Bye!");
                        break; // on termine la session
                    }
                }

            } catch (IOException e) {
                System.out.println("⚠️ Client déconnecté: " + clientAddress);
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException ignored) {
                }

                connectedClients.remove(clientAddress);
                System.out.println("👋 Session terminée pour " + clientAddress);
                System.out.println("👥 Clients restants: " + connectedClients);
            }
        }
    }
}
