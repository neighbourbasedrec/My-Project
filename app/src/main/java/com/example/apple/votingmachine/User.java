package com.example.apple.votingmachine;

public class User {
    private String uid;
    private String name;
    private String email;
    private String device_token;
    private String image;
    private String description;

    public User(String uid, String name, String email, String token, String description){
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.device_token = token;
        this.image = "default";
        this.description = description;
    }

    public User(){

    }

    public static User newUser(String uid, String name, String email, String token, String description){
        return new User(uid, name, email, token, description);
    }

    public String getDescription(){return description; }

    public String getUid(){
        return uid;
    }

    public String getName(){
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return device_token;
    }

    public String getImage(){
        return image;
    }

}
