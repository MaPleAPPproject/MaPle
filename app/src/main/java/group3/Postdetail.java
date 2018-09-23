package group3;

import java.io.Serializable;

public class Postdetail implements Serializable {
    private int postId;
    private int memberId;
    private String district;
    private int clickcount;
    private int collectioncount;
    private String username;
    private long lat,lon;

    public Postdetail(int postId, int memberId, String district,
                      int clickcount, int collectioncount, String username) {
        this.postId = postId;
        this.memberId = memberId;
        this.district = district;
        this.clickcount = clickcount;
        this.collectioncount = collectioncount;
        this.username = username;
    }
    public Postdetail(int postId, int memberId, String district, int collectioncount, String username,long lat,long lon) {
        this.postId = postId;
        this.memberId = memberId;
        this.district = district;
        this.collectioncount = collectioncount;
        this.username = username;
        this.lat=lat;
        this.lon=lon;
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


    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
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

    public long getLat() {
        return lat;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public long getLon() {
        return lon;
    }

    public void setLon(long lon) {
        this.lon = lon;
    }
}
