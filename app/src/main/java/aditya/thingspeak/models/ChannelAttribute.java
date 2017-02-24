package aditya.thingspeak.models;

/**
 * Created by adi on 2/24/17.
 */
public class ChannelAttribute {
    public String key;
    public String value;

    public ChannelAttribute(){

    }
    public ChannelAttribute(String key, String value){
        this.key = key;
        this.value = value;
    }
    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
