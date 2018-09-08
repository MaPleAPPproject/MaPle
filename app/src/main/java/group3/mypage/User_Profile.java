package group3.mypage;


import java.io.Serializable;

public class User_Profile implements Serializable {

    private int snapshotID;
    private String name;
    private String email;
    private String password;
    private boolean vipStatus;
    private String selfIntroduction;


    public User_Profile(int snapshotID, String name, String email, String password, boolean vipStatus, String selfIntroduction) {
        this.snapshotID = snapshotID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.vipStatus = vipStatus;
        this.selfIntroduction = selfIntroduction;
    }
    public int getSnapshotID() {
        return snapshotID;
    }

    public void setSnapshotID(int snapshotID) {
        this.snapshotID = snapshotID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isVipStatus() {
        return vipStatus;
    }

    public void setVipStatus(boolean vipStatus) {
        this.vipStatus = vipStatus;
    }

    public String getSelfIntroduction() {
        return selfIntroduction;
    }

    public void setSelfIntroduction(String selfIntroduction) {
        this.selfIntroduction = selfIntroduction;
    }





}
