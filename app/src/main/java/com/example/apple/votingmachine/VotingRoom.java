package com.example.apple.votingmachine;

public class VotingRoom {

    private String hashCode;
    private String roomName;

    //due date
    private String dueDate;

    //ticket per number
    private String voteTicket;

    public VotingRoom(String hashCode, String roomName, String dueDate, String voteTicket){
        this.hashCode = hashCode;
        this.roomName = roomName;
        this.dueDate = dueDate;
        this.voteTicket = voteTicket;
    }

    public VotingRoom(){

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

    public String getVoteTicket() {
        return voteTicket;
    }

    static VotingRoom newVotingRoom(String hashCode, String roomName, String dueDate, String voteTicket){
        return new VotingRoom(hashCode, roomName, dueDate, voteTicket);
    }
}
