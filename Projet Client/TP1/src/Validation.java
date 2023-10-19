public class Validation {
    public static boolean isValidIP(String address) {
        String[] elems = address.split("\\.");
        if (elems.length != 4) return false;
        for ( String elem : elems) {
            int octet = Integer.parseInt(elem);
            if (octet < 0 || octet > 255) return false;
        }
        return true;
    }

    public static boolean isValidPort(Integer port){
        return port >= 5000 && port <= 5050;
    }
}
