package com.example.apple.votingmachine;

public class VotingRoom {

    private String hashCode;
    private String roomName;

    public VotingRoom(String hashCode, String roomName){
        this.hashCode = hashCode;
        this.roomName = roomName;
    }

    public VotingRoom(){

    }

    public String getHashCode(){
        return hashCode;
    }

    public String getRoomName() {
        return roomName;
    }

    static VotingRoom newVotingRoom(String hashCode, String roomName){
        return new VotingRoom(hashCode, roomName);
    }
}
