package group3;

import android.widget.Button;

public class MyPage_SinglePost {

    private int personIcon;
    private String tvName;
    private int ivPhoto;
    private String tvDescription;
    private Button backButton;





    public int getPersonIcon() {
        return personIcon;
    }

    public void setPersonIcon(int personIcon) {
        this.personIcon = personIcon;
    }

    public String getTvName() {
        return tvName;
    }

    public void setTvName(String tvName) {
        this.tvName = tvName;
    }

    public int getIvPhoto() {
        return ivPhoto;
    }

    public void setIvPhoto(int ivPhoto) {
        this.ivPhoto = ivPhoto;
    }

    public String getTvDescription() {
        return tvDescription;
    }

    public void setTvDescription(String tvDescription) {
        this.tvDescription = tvDescription;
    }

    public MyPage_SinglePost(int personIcon, String tvName, int ivPhoto, String tvDescription) {
        this.personIcon = personIcon;
        this.tvName = tvName;
        this.ivPhoto = ivPhoto;
        this.tvDescription = tvDescription;
    }



}
