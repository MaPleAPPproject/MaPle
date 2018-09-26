package group3.friend;

import java.io.Serializable;

public class Friend{
    private int memberid;
    private int matchid;
    private  String messageRoom;
    private int relationshipStatus;
    private int friendid;
    private static Serializable friends;

    public Friend(int memberid, int matchid, String messageRoom, int relationshipStatus, int friendid) {
        this.memberid = memberid;
        this.matchid = matchid;
        this.messageRoom = messageRoom;
        this.relationshipStatus = relationshipStatus;
        this.friendid = friendid;
    }

    public Friend(int memberid, int matchid, int relationshipStatus, int friendid) {
        this.memberid = memberid;
        this.matchid = matchid;
        this.relationshipStatus = relationshipStatus;
        this.friendid = friendid;
    }

    public Friend(int friendid) {
        this.friendid = friendid;
    }

    public Friend() {
    }

    public static Serializable getFriends() {
        return friends;
    }

    public static void setFriends(Serializable friends) {
        Friend.friends = friends;
    }

    public int getMatchid() {
        return matchid;
    }

    public void setMatchid(int matchid) {
        this.matchid = matchid;
    }

    public int getMemberid() {
        return memberid;
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

    public String getMessageRoom() {
        return messageRoom;
    }

    public void setMessageRoom(String messageRoom) {
        this.messageRoom = messageRoom;
    }
}
