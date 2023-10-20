import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.reflect.TypeToken;

public class Validation {
    public static boolean isValidIP(String address) {
        String[] elems = address.split("\\.");
        if (elems.length != 4) return false;
        for ( String elem : elems) {
            try {
                int octet = Integer.parseInt(elem);
                if (octet < 0 || octet > 255) return false;
            }
            catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidPort(String portString){
        try {
            int port = Integer.parseInt(portString);
            return port >= 5000 && port <= 5050;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidUser(User user) {
        Gson gson = new Gson();
        List<User> users = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("users.json"));
            users = gson.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            // 'users' now contains the list of users loaded from the JSON file.
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }
}
