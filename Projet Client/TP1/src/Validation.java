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
}
