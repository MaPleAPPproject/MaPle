package group3;

import java.io.Serializable;

public class Postdetail implements Serializable {
    private int postId;
    private int memberId;
    private long posteddate;
    private String district;
    private String comment;
    private int clickcount;
    private int collectioncount;
    private String username;

    public Postdetail(int postId, int memberId, long posteddate, String district,
                      String comment, int clickcount, int collectioncount, String username) {
        this.postId = postId;
        this.memberId = memberId;
        this.posteddate = posteddate;
        this.district = district;
        this.comment = comment;
        this.clickcount = clickcount;
        this.collectioncount = collectioncount;
        this.username = username;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public long getPosteddate() {
        return posteddate;
    }

    public void setPosteddate(long posteddate) {
        this.posteddate = posteddate;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getClickcount() {
        return clickcount;
    }

    public void setClickcount(int clickcount) {
        this.clickcount = clickcount;
    }

    public int getCollectioncount() {
        return collectioncount;
    }

    public void setCollectioncount(int collectioncount) {
        this.collectioncount = collectioncount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
