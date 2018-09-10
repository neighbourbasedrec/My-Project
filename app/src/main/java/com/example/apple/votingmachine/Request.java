package com.example.apple.votingmachine;

import android.support.v7.app.AppCompatActivity;

public class Request{

    private String room_name;
    private String receiver_name;
    private String sender_name;


    public Request(){

    }

    public Request(String room_name, String receiver_name, String sender_name){

        this.room_name = room_name;
        this.receiver_name = receiver_name;
        this.sender_name = sender_name;

    }

    public String getRoom_name() {
        return room_name;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public String getSender_name() {
        return sender_name;
    }

    public static Request newRequest(String room_name,String receiver_name, String sender_name){
        return new Request(room_name,receiver_name, sender_name);
    }

}
