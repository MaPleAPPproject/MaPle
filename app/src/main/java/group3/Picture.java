package group3;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Picture implements Serializable {
    private int postid;
    private String comment;
    private long date;
    private String district;
    private double lat;
    private double lon;

//    public Picture(int postid, String comment, long date) {
//        super();
//        this.postid = postid;
//        this.comment = comment;
//        this.date = date;
//    }
    public Picture(int postid, String comment, long date,String district) {
        super();
        this.postid = postid;
        this.comment = comment;
        this.date = date;
        this.district=district;
    }
    public Picture(int postid, String comment, long date,String district,double lat,double lon) {
        super();
        this.postid = postid;
        this.comment = comment;
        this.date = date;
        this.district=district;
        this.lat = lat;
        this.lon = lon;
    }
    public int getPostid() {
        return postid;
    }
    public void setPostid(int postid) {
        this.postid = postid;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public long getDate() {
        return date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public String getFormatedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",
                Locale.US);
        return dateFormat.format(new Date(date));
    }
    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}


