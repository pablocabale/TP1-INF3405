import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;

public class Serveur {
    private static ServerSocket Listener; // Application Serveur

    public static void main(String[] args) throws Exception {
        connectServer();

        // Compteur incrémenté à chaque connexion d'un client au serveur
        int clientNumber = 0;

        try {
            // À chaque fois qu'un nouveau client se, connecte, on exécute la fonstion
            // run() de l'objet ClientHandler
            while (true) {
                // Important : la fonction accept() est bloquante: attend qu'un prochain client se connecte
                // Une nouvetle connection : on incémente le compteur clientNumber
                new ClientHandler(Listener.accept(), clientNumber++).start();
            }
        } finally {
            // Fermeture de la connexion
            Listener.close();
        }
    }

    static private void connectServer() {
        // Adresse et port du serveur
        //String serverAddress = "127.0.0.1";
        //int serverPort = 5000;

        String serverAddress;
        int serverPort;
        Scanner inputScanner = new Scanner(System.in);

        do {
            System.out.println("Enter valid server IP adress (xxx.xxx.xxx.xxx)");
            serverAddress = inputScanner.next();
        } while (!Validation.isValidIP(serverAddress));

        String portString;
        do {
            System.out.println("Enter valid server port (Between 5000 and 5050)");
            portString = inputScanner.next();
        } while (!Validation.isValidPort(portString));
        serverPort = Integer.parseInt(portString);

        // Création de la connexien pour communiquer ave les, clients
        try {
            Listener = new ServerSocket();
            Listener.setReuseAddress(true);
            InetAddress serverIP = InetAddress.getByName(serverAddress);

            // Association de l'adresse et du port à la connexien
            Listener.bind(new InetSocketAddress(serverIP, serverPort));
        } catch (IOException e) {
            System.out.println("Connection failed, try again.");
            connectServer();
            return;
        }

        System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
    }
}