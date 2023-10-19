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

    private static void connectionToServer() {
        // Adresse et port du serveur
        //String serverAddress = "127.0.0.1";
        //int port = 5000;

        String serverAddress;
        int port;
        Scanner inputScanner = new Scanner(System.in);

        do {
            System.out.println("Enter valid server IP adress (xxx.xxx.xxx.xxx)");
            serverAddress = inputScanner.next();
        } while (!Validation.isValidIP(serverAddress));

        do {
            System.out.println("Enter valid server port (Between 5000 and 5050)");
            port = inputScanner.nextInt();
        } while (!Validation.isValidPort(port));

        try {
            // Création d'une nouvelle connexion aves le serveur
            socket = new Socket(serverAddress, port);
        } catch (IOException e) {
            System.out.println("Connection failed, try again.");
            connectionToServer();
            return;
        }

        System.out.format("Server running on: [%s:%d]", serverAddress, port);
    }

    private static void serverCommunication() throws IOException {
        // Céatien d'un canal entrant pour recevoir les messages envoyés, par le serveur
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // création de canal d’envoi
        Scanner inputScanner = new Scanner(System.in);

        // Attente de la réception d'un message envoyé par le, server sur le canal
        String messageFromServer = in.readUTF();
        System.out.println(messageFromServer);

        verifyUser(out, in, inputScanner);

        String filename = "src/Nexys4.jpg";
        String destFileName = "treatedImage.jpg";
        ImageTreatment imgTreater = new ImageTreatment();
        imgTreater.sendFileClientSide(filename, in, out);
        imgTreater.receiveTreatedImage(in, destFileName);

        messageFromServer = in.readUTF();
        System.out.println(messageFromServer);

        in.close();
        out.close();
    }

    private static void verifyUser(DataOutputStream out, DataInputStream in, Scanner inputScanner) throws IOException {
        System.out.println("Username?");
        String username = inputScanner.next();
        out.writeUTF(username);

        System.out.println("Password?");
        String password = inputScanner.next();
        out.writeUTF(password);
        //Ici verifier user

        String messageFromServer = in.readUTF();
        System.out.println(messageFromServer);
    }
}