package com.spartan.karanbir.pubnubdemo;

/**
 * Created by karanbir on 3/18/16.
 */
public class Message {
    private String message;
    private String username;

    public Message(String message, String username){
        this.message = message;
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }
}
