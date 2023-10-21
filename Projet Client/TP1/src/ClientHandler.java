import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
        boolean isVerified = false;
        String clientUsername;
        String clientPassword;

        do {
            clientUsername = in.readUTF();
            clientPassword = in.readUTF();

            User user = new User(clientUsername, clientPassword);
            isVerified = isValidUser(user);
            if(!isVerified) {
                out.writeUTF("User doesn't exist. User: " + clientUsername + " registered.");
                out.writeUTF("not valid");
            }

        } while (!isVerified);

        out.writeUTF(clientUsername + ": you are connected");
        out.writeUTF("valid");
        return clientUsername;
    }

    private  void imageService(DataOutputStream out, DataInputStream in, String user) throws IOException {
        ImageTreatment imgTreater = new ImageTreatment();

        String imageFile = in.readUTF();
        System.out.println("[" + user + " - " + socket.getRemoteSocketAddress().toString() + " - " +
                LocalDate.now() + "@" + LocalTime.now() + "] : Image " + imageFile + " received for treatment.");

        imgTreater.treatAndResendImage(in, out);
    }

    public static boolean isValidUser(User user) {
        Gson gson = new Gson();
        List<User> users = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("users.json"));
            users = gson.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            // 'users' now contains the list of users loaded from the JSON file.
        }
        catch (FileNotFoundException e) {
            System.out.println("Users database not created yet");
        }

        for (User u : users)
            if (u.getUsername().equals(user.getUsername()) && u.getPassword().equals(user.getPassword()))
                return true;

        addUser(users, user, gson);
        return false;
    }

    private static void addUser(List<User> users, User userToAdd, Gson gson) {
        users.add(userToAdd);
        String updatedJson = gson.toJson(users);

        try {
            FileWriter fileWriter = new FileWriter("users.json");
            fileWriter.write(updatedJson);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Error writing");
        }
    }
}