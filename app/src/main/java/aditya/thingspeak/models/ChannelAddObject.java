package aditya.thingspeak.models;

/**
 * Created by adi on 2/24/17.
 */
public class ChannelAddObject {
    public String channelID;
    public String channelURL;
    public String channelPushID;
    public ChannelAddObject(){

    }
    public ChannelAddObject(String channelID,String channelURL,String channelPushID){

        this.channelID = channelID;
        this.channelURL= channelURL;
        this.channelPushID = channelPushID;
    }

    public String getChannelPushID() {
        return channelPushID;
    }

    public void setChannelPushID(String channelPushID) {
        this.channelPushID = channelPushID;
    }

    public String getChannelID() {
        return channelID;
    }

    public String getChannelURL() {
        return channelURL;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public void setChannelURL(String channelURL) {
        this.channelURL = channelURL;
    }
}

