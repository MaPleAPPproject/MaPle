package group3.explore;

public class UserPreference {
    private int postid;
    private int collectorid;
    private int memberid;
    private int collectcount;
    public UserPreference(int postid, int collectorid, int memberid) {
        super();
        this.postid = postid;
        this.collectorid = collectorid;
        this.memberid = memberid;
    }
    public UserPreference(int postid, int collectorid, int memberid, int collectioncount) {
        super();
        this.postid = postid;
        this.collectorid = collectorid;
        this.memberid = memberid;
        this.collectcount=collectioncount;
    }
    public int getPostid() {
        return postid;
    }
    public void setPostid(int postid) {
        this.postid = postid;
    }
    public int getCollectorid() {
        return collectorid;
    }
    public void setCollectorid(int collectorid) {
        this.collectorid = collectorid;
    }
    public int getMemberid() {
        return memberid;
    }
    public void setMemberid(int memberid) {
        this.memberid = memberid;
    }

}
