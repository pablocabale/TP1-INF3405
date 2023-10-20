import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;

public class ClientHandler extends Thread { // pour traiter la demande de chaque client sur un socket particulier
    private Socket socket;
    private int clientNumber;


    public ClientHandler(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        System.out.println("New connection with client#" + clientNumber + " at" + socket);
    }

    public void run() { // Création de thread qui envoi un message à un client
        try {
            communicateWithClient();
        }
        catch (IOException e) {
            System.out.println("Error handling client# " + clientNumber + ": " + e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Couldn't close a socket, what's going on?");
            }
            System.out.println("Connection with client# " + clientNumber + " closed");
        }
    }

    private void communicateWithClient() throws IOException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // création de canal d’envoi
        out.writeUTF("\nHello from server - you are client#" + clientNumber); // envoi de message

        DataInputStream in = new DataInputStream(socket.getInputStream()); // recevoir messages

        String clientUsername = verifyUser(out, in);
        imageService(out, in, clientUsername);

        in.close();
        out.close();
    }

    private String verifyUser(DataOutputStream out, DataInputStream in) throws IOException {
        String clientUsername = in.readUTF();
        String clientPassword = in.readUTF();

        System.out.println(clientUsername);
        System.out.println(clientPassword);

        out.writeUTF(clientUsername + ": you are connected");
        return clientUsername;
        //Ici ajouter verification de user et mdp
    }

    private  void imageService(DataOutputStream out, DataInputStream in, String user) throws IOException {
        String imageFile = in.readUTF();
        System.out.println("[" + user + " - " + socket.getRemoteSocketAddress().toString() + " - " + LocalDate.now() +
                "@" + LocalTime.now() + "] : Image " + imageFile + " received for treatment.");

        ImageTreatment imgTreater = new ImageTreatment();
        try {
            imgTreater.treatAndResendImage(in, out);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}