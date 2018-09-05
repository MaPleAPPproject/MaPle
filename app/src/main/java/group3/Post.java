package group3;

import java.io.Serializable;

public class Post implements Serializable {
    private int imageId;
    private String personid;
    private String selfintro;
    private String location;
    private String comment;
    public Post(int imageId, String personid, String selfintro, String location, String comment) {
        super();
        this.imageId = imageId;
        this.personid = personid;
        this.selfintro = selfintro;
        this.location = location;
        this.comment = comment;
    }

    public Post(int imageId) {
        this.imageId = imageId;
    }

    public String getPersonid() {
        return personid;
    }

    public void setPersonid(String personid) {
        this.personid = personid;
    }

    public String getSelfintro() {
        return selfintro;
    }

    public void setSelfintro(String selfintro) {
        this.selfintro = selfintro;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
