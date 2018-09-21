package group3.friend;

import java.io.Serializable;

public class Friend {

    public static Serializable friends;
    private int matchid;
    private int memberid;
    private int relationshipStatus;
    private int friendid;
    private int messageRoom;

    /*從使用者清單拿
    private String name;
    private String intro;
    private int image;*/

    public Friend(int matchid, int memberid, int relationshipStatus, int friendid, int messageRoom) {
        this.matchid = matchid;
        this.memberid = memberid;
        this.relationshipStatus = relationshipStatus;
        this.friendid = friendid;
        this.messageRoom = messageRoom;
    }

    public int getMatchid() {
        return matchid;
    }
    public void setMatchid(int matchid) {
        this.matchid = matchid;
    }

    public int getMemberid() {
        return matchid;
    }
    public void setMemberid(int memberid) {
        this.memberid = memberid;
    }

    public int getRelationshipStatus() {
        return relationshipStatus;
    }
    public void setRelationshipStatus(int relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public int getFriendid() {
        return friendid;
    }

    public void setFriendid(int friendid) {
        this.friendid = friendid;
    }

    public int getMessageRoom() {
        return messageRoom;
    }

    public void setMessageRoom(int messageRoom) {
        this.messageRoom = messageRoom;
    }
}
