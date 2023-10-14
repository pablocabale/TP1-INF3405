import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

// Application client
public class Client {
    private static Socket socket;

    public static void main(String[] args) throws Exception {
        try {
            connectionToServer();
            serverCommunication();
        } finally {
            // fermeture de La connexion avec le serveur
            System.out.println("Connection with server lost");
            socket.close();
        }
    }

    private static void connectionToServer() throws IOException {
        // Adresse et port du serveur
        String serverAddress = "127.0.0.1";
        int port = 5000;

        // Création d'une nouvelle connexion aves le serveur
        socket = new Socket(serverAddress, port);

        System.out.format("Serveur lancé sur [%s:%d]", serverAddress, port);
    }

    private static void serverCommunication() throws IOException {
        // Céatien d'un canal entrant pour recevoir les messages envoyés, par le serveur
        DataInputStream in = new DataInputStream(socket.getInputStream());

        // Attente de la réception d'un message envoyé par le, server sur le canal
        String helloMessageFromServer = in.readUTF();
        System.out.println(helloMessageFromServer);

        DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // création de canal d’envoi
        Scanner inputScanner = new Scanner(System.in);

        int portInput = 0;
        while (portInput <= 5000) {
            System.out.println("Enter IP adress");
            portInput = inputScanner.nextInt();
            out.writeUTF("testest"); // envoi de message
        }
    }
}