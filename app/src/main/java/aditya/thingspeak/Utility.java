package aditya.thingspeak;

/**
 * Created by adi on 2/23/17.
 */
public class Utility {
    public static String encodeEmail(String email){
        return email.replace(".","%2E");
    }
    public static String decodeEmail(String email){
        return email.replace("%2E",".");
    }
}
