package aditya.thingspeak;

/**
 * Created by adi on 2/23/17.
 */
public class ChannelObject {
    public String channelDesc;
    public String channelName;
    public String val1;
    public String val2;
    public String val3;
    public String val4;
    public ChannelObject(String channelName,String channelDesc,String val1, String val2, String val3, String val4){
        this.channelDesc= channelDesc;
        this.channelName = channelName;
        this.val1 = val1;
        this.val2= val2;
        this.val3= val3;
        this.val4= val4;
    }

    public String getChannelDesc() {
        return channelDesc;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getVal1() {
        return val1;
    }

    public String getVal2() {
        return val2;
    }

    public String getVal3() {
        return val3;
    }

    public String getVal4() {
        return val4;
    }

    public void setChannelDesc(String channelDesc) {
        this.channelDesc = channelDesc;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setVal1(String val1) {
        this.val1 = val1;
    }

    public void setVal2(String val2) {
        this.val2 = val2;
    }

    public void setVal3(String val3) {
        this.val3 = val3;
    }

    public void setVal4(String val4) {
        this.val4 = val4;
    }
}
