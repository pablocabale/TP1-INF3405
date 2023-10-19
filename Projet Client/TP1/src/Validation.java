public class Validation {
    public boolean validateIPAddress(String address) {
        String[] elems = address.split("\\.");
        if (elems.length != 4) return false;
        for ( String elem : elems) {
            int octet = Integer.parseInt(elem);
            if (octet < 0 || octet > 255) return false;
        }
        return true;
    }

    public boolean validatePort(Integer port){
        return port >= 5000 && port <= 5050;
    }
}
