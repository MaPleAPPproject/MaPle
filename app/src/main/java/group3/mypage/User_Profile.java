package group3.mypage;


import java.io.Serializable;

public class User_Profile implements Serializable {



    private int memberID;
    private String userName;
    private String email;
    private String password;
    private int vipStatus;
    private String selfIntroduction;


    public User_Profile(int memberID, String userName, String email, String password, int vipStatus, String selfIntroduction) {
        this.memberID = memberID;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.vipStatus = vipStatus;
        this.selfIntroduction = selfIntroduction;
    }
    public User_Profile(String email, int vipStatus, String selfIntroduction) {
        super();
        this.email = email;
        this.vipStatus = vipStatus;
        this.selfIntroduction = selfIntroduction;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public int getVipStatus() {
        return vipStatus;
    }

    public void setVipStatus(int vipStatus) {
        this.vipStatus = vipStatus;
    }

    public String getSelfIntroduction() {
        return selfIntroduction;
    }

    public void setSelfIntroduction(String selfIntroduction) {
        this.selfIntroduction = selfIntroduction;
    }
}