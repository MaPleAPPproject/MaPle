package group3;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Post {
        private int memberid;
        private int postid;
        private int collectioncount;
        private int clickcount;
        private long date;

        public Post(int memberid, int postid, int collectioncount, int clickcount, long date) {
            super();
            this.memberid = memberid;
            this.postid = postid;
            this.collectioncount = collectioncount;
            this.clickcount = clickcount;
            this.date = date;
        }
        public int getMemberid() {
            return memberid;
        }
        public void setMemberid(int memberid) {
            this.memberid = memberid;
        }
        public int getPostid() {
            return postid;
        }
        public void setPostid(int postid) {
            this.postid = postid;
        }
        public int getCollectioncount() {
            return collectioncount;
        }
        public void setCollectioncount(int collectcount) {
            this.collectioncount = collectcount;
        }
        public int getClickcount() {
            return clickcount;
        }
        public void setClickcount(int clickcount) {
            this.clickcount = clickcount;
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
    }
