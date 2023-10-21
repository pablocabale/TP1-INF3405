import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
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
        }
        catch (IOException e) {
            System.out.println("Connection with server lost. Try again later.");
        }
        finally {
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

        String portString;
        do {
            System.out.println("Enter valid server port (Between 5000 and 5050)");
            portString = inputScanner.next();
        } while (!Validation.isValidPort(portString));
        port = Integer.parseInt(portString);

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
        imageService(out, in, inputScanner);

        in.close();
        out.close();
    }

    private static void verifyUser(DataOutputStream out, DataInputStream in, Scanner inputScanner) throws IOException {
        boolean isValidUser = false;
        do {
            System.out.println("Username?");
            String username = inputScanner.next();
            out.writeUTF(username);

            System.out.println("Password?");
            String password = inputScanner.next();
            out.writeUTF(password);

            String messageFromServer = in.readUTF();
            System.out.println(messageFromServer);

            messageFromServer = in.readUTF();
            if (messageFromServer.equals("valid"))
                isValidUser = true;

        } while (!isValidUser);
    }

    private static void imageService(DataOutputStream out, DataInputStream in, Scanner inputScanner) throws IOException {
        try {
            System.out.println("Image file name?");
            // Nexys4.jpg
            String filename = inputScanner.next();

            System.out.println("Treated image file name?");
            // treatedImage.jpg
            String destFileName = inputScanner.next();

            ImageTreatment imgTreater = new ImageTreatment();
            imgTreater.sendFileClientSide(filename, in, out);
            imgTreater.receiveTreatedImage(in, destFileName);
            System.out.println("Location of new file: ./" + destFileName);

        } catch (FileNotFoundException e) {
            System.out.println("Image file name invalid, try again");
            imageService(out, in, inputScanner);
        }
    }
}