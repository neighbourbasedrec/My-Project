package com.example.apple.votingmachine;

public class VotingRoom {

    private String hashCode;
    private String roomName;
    private String startDate;

    //due date
    private String dueDate;

    //ticket per number
    private String description;

    public VotingRoom(String hashCode, String roomName, String dueDate, String description, String startDate){
        this.hashCode = hashCode;
        this.roomName = roomName;
        this.dueDate = dueDate;
        this.description = description;
        this.startDate = startDate;
    }

    public VotingRoom(){

    }

    public String getStartDate() {
        return startDate;
    }
    public String getHashCode(){
        return hashCode;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getDescription() {
        return description;
    }

    static VotingRoom newVotingRoom(String hashCode, String roomName, String dueDate, String voteTicket, String startDate){
        return new VotingRoom(hashCode, roomName, dueDate, voteTicket, startDate);
    }
}
