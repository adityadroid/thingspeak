package aditya.thingspeak.models;

/**
 * Created by adi on 2/25/17.
 */
public class SubscriptionObject {
    public String channelID;
    public String fieldID;
    public String maxVal;
    public String minVal;
    public String fieldLabel;
    public SubscriptionObject(String channelID,String fieldID, String maxVal, String minVal,String fieldLabel){
        this.channelID = channelID;
        this.fieldID = fieldID;
        this.maxVal = maxVal;
        this.minVal = minVal;
        this.fieldLabel = fieldLabel;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public  SubscriptionObject(){

    }

    public String getChannelID() {
        return channelID;
    }

    public String getFieldID() {
        return fieldID;
    }

    public String getMaxVal() {
        return maxVal;
    }

    public String getMinVal() {
        return minVal;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public void setFieldID(String fieldID) {
        this.fieldID = fieldID;
    }

    public void setMaxVal(String maxVal) {
        this.maxVal = maxVal;
    }

    public void setMinVal(String minVal) {
        this.minVal = minVal;
    }

}
