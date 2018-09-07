package group3;

import android.widget.Button;

public class SinglePost {
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

    public Button getBackButton() {
        return backButton;
    }

    public void setBackButton(Button backButton) {
        this.backButton = backButton;
    }

    public SinglePost(int personIcon, String tvName, int ivPhoto, String tvDescription, Button backButton) {
        this.personIcon = personIcon;
        this.tvName = tvName;
        this.ivPhoto = ivPhoto;
        this.tvDescription = tvDescription;
        this.backButton = backButton;
    }
}
