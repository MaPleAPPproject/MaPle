package group3;

public class Friend {


    private int image;
    private String intro;
    private String name;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




    public Friend(int image, String intro, String name) {
        this.image = image;
        this.intro = intro;
        this.name = name;
    }
}
