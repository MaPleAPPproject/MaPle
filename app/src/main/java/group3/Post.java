package group3;

public class Post {
    private int imageId;
    private String ID;
// next step:Id,imageID,imagePost,describ,location
    public Post(int imageId) {
        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
