package group3.mypage;

import android.widget.ImageView;

public class Mypage_NewPost {

    private int postId,postPhotoId;
    private  String location,content;
    private ImageView imageView;

    public Mypage_NewPost(int postId, int postPhotoId, String location, String content, ImageView imageView) {
        this.postId = postId;
        this.postPhotoId = postPhotoId;
        this.location = location;
        this.content = content;
        this.imageView = imageView;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getPostPhotoId() {
        return postPhotoId;
    }

    public void setPostPhotoId(int postPhotoId) {
        this.postPhotoId = postPhotoId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }




}
