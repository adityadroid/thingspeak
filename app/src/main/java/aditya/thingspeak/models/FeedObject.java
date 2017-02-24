package aditya.thingspeak.models;

/**
 * Created by adi on 2/24/17.
 */
public class FeedObject {
    public String feedHeading;
    public String feedContent;
    public String feedTime;

    public String getFeedContent() {
        return feedContent;
    }

    public String getFeedHeading() {
        return feedHeading;
    }

    public String getFeedTime() {
        return feedTime;
    }

    public void setFeedContent(String feedContent) {
        this.feedContent = feedContent;
    }

    public void setFeedTime(String feedTime) {
        this.feedTime = feedTime;
    }

    public void setFeedHeading(String feedHeading) {
        this.feedHeading = feedHeading;
    }

}
