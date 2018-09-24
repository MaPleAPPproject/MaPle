package group3.mypage;

import java.io.Serializable;
import java.util.ArrayList;

public class LocationList extends Object implements Serializable{
    private int MemberId;
    private int PostId;
    private String District;
    private double Lat;
    private double Lon;


    public LocationList(int MemberId,int PostId,String District,double Lat, double Lon) {
        super();
        this.MemberId = MemberId;
        this.PostId = PostId;
        this.District = District;
        this.Lat=Lat;
        this.Lon=Lon;
    }
    public int getMemberId() {
        return MemberId;
    }
    public void setMemberId(int MemberId) {
        this.MemberId = MemberId;
    }
    public int getPostId() {
        return PostId;
    }
    public void setPostId(int PostId) {
        this.PostId = PostId;
    }
    public String getDistrict() {
        return District;
    }
    public void setDistrict(String District) {
        this.District = District;
    }
    public double getLon() {
        return Lon;
    }
    public void setLon(double Lon ) {
        this.Lon = Lon;
    }
    public double getLat() {
        return Lat;
    }
    public void setLat(double Lat ) {
        this.Lat = Lat;
    }
}