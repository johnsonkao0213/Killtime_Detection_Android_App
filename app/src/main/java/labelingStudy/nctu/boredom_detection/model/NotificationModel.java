package labelingStudy.nctu.boredom_detection.model;

import java.util.Map;

public class NotificationModel {
    String ID = "";
    String CreatedTime = "";
    String Content = "";
    String Type = "";
    String ResponseTime = "";

    int isExpired = -1; //-1 : no response, 1: is expired, 0: is not expired.

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCreatedTime() {
        return CreatedTime;
    }

    public void setCreatedTime(String createdTime) {
        CreatedTime = createdTime;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getResponseTime() {
        return ResponseTime;
    }

    public void setResponseTime(String responseTime) {
        ResponseTime = responseTime;
    }

    public int getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(int isExpired) {
        this.isExpired = isExpired;
    }

    public NotificationModel(Map<String, Object> map) {
        this.ID = map.get("ID").toString();
        this.CreatedTime = map.get("CreatedTime").toString();
        this.Content = map.get("Content").toString();
        this.Type = map.get("Type").toString();
    }
}
