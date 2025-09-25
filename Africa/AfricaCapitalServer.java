package Africa;

import java.io.*;
import java.net.*;
import java.util.*;

// Serveur multi-clients pour AfricaCapital
public class AfricaCapitalServer {
    private static final int PORT = 12345;

    // Dictionnaire des pays africains et leurs capitales
    private static final Map<String, String> capitals = new HashMap<>();
    static {

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
        capitals.put("côte d'ivoire", "Yamoussoukro");
        capitals.put("burkina faso", "Ouagadougou");
        capitals.put("mali", "Bamako");
        capitals.put("uganda", "Kampala");
        capitals.put("mozambique", "Maputo");
        capitals.put("zimbabwe", "Harare");
        capitals.put("namibie", "Windhoek");
        capitals.put("botswana", "Gaborone");
        capitals.put("libéria", "Monrovia");
        capitals.put("sierra leone", "Freetown");
        capitals.put("guinée", "Conakry");
        capitals.put("madagascar", "Antananarivo");
        capitals.put("république centrafricaine", "Bangui");
        capitals.put("république du congo", "Brazzaville");
        capitals.put("gabon", "Libreville");
        capitals.put("togo", "Lomé");
        capitals.put("bénin", "Porto-Novo");
        capitals.put("rwanda", "Kigali");
        capitals.put("burundi", "Gitega");
        capitals.put("soudan", "Khartoum");
        capitals.put("soudan du sud", "Djouba");
        capitals.put("libye", "Tripoli");
        capitals.put("somalie", "Mogadiscio");
        capitals.put("djibouti", "Djibouti");
        capitals.put("érythrée", "Asmara");
        capitals.put("gambie", "Banjul");
        capitals.put("cap-vert", "Praia");
        capitals.put("guinée bissau", "Bissau");
        capitals.put("éthiopie", "Addis-Abeba");
        capitals.put("comores", "Moroni");
        capitals.put("sao tomé et principe", "São Tomé");
        capitals.put("seychelles", "Victoria");
        capitals.put("malawi", "Lilongwe");
        capitals.put("zambie", "Lusaka");
        capitals.put("eswatini", "Mbabane");
        capitals.put("lesotho", "Maseru");
        capitals.put("mauritanie", "Nouakchott");
        capitals.put("tchad", "N'Djamena");
        capitals.put("république démocratique du congo", "Kinshasa");

    }

    // Liste synchronisée des clients connectés
    private static final Set<String> connectedClients = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("🌍 AfricaCapital Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                // Récupérer IP et port du client
                String clientIP = clientSocket.getInetAddress().getHostAddress();
                int clientPort = clientSocket.getPort();
                String clientID = clientIP + ":" + clientPort;

                connectedClients.add(clientID);

                System.out.println(" Client connecté: " + clientID);
                System.out.println("👥 Clients connectés: " + connectedClients);

                // Lancer un thread pour ce client
                new Thread(new ClientHandler(clientSocket, clientID)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Classe qui gère un client
    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private String clientID;

        public ClientHandler(Socket socket, String clientID) {
            this.clientSocket = socket;
            this.clientID = clientID;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                out.println("Bienvenue sur AfricaCapital! Pose une question ex: 'Quelle est la capitale de la RDC ?'");

                String question;
                while ((question = in.readLine()) != null) {
                    System.out.println("❓ Question de " + clientID + " : " + question);

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
                        break; // fin de la session
                    }
                }

            } catch (IOException e) {
                System.out.println("⚠️ Problème avec " + clientID);
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException ignored) {
                }

                connectedClients.remove(clientID);
                System.out.println("👋 Session terminée pour " + clientID);
                System.out.println("👥 Clients restants: " + connectedClients);
            }
        }
    }
}
