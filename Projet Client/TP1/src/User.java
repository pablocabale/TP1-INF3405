
/*"Users.json":[
        { "username":"John", "password":"123" },
        { "username":"Anna", "password":"1435" },
        { "username":"Peter", "password":"54758" }
        ] */
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
public class User {


    public void validateUsername(String name) {
        try {

            JSONArray users = new JSONParser().parse(new FileReader("Users.json"));

            for (Object user: users) {
                JSONObject userObject = (JSONObject) user;
                String username = (String) userObject.get("username");
                if (username != name) {
                    JSONObject newUser = new JSONObject();
                    newUser.put("username", name);
                    users.add(newUser);
                }
                else

            }
        }
    }
}
