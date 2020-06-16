package blackdots.t.punnubchat.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StateModel {

    String sender,date;

    public StateModel(@JsonProperty("sender") String sender, @JsonProperty("date") String date) {
        this.sender = sender;
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
