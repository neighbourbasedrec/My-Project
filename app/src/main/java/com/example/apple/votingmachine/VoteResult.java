package com.example.apple.votingmachine;

public class VoteResult {
    String name;
    String status;
    String thumb_image;
    String comments;
    int voteUp = 0;
    int voteDown = 0;

    public VoteResult (){

    }

    public String getComments() {
        return comments;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getVoteUp() {
        return voteUp + "";
    }

    public String getVoteDown() {
        return voteDown + "";
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void addComments(String comments) {
        this.comments += comments;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String statue) {
        this.status = statue;
    }

    public void VoteDown() {
        this.voteDown += 1;
    }

    public void VoteUp() {
        this.voteUp += 1;
    }
}
