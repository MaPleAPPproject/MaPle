package group3.mypage;



public class Mypage_NewPost {

    private int postId,photoId;
    private  String location,content,countrycode,address,district;
    private double longitude,latitude;

    public Mypage_NewPost(int postId, int photoId, String location, String content, String countrycode, String address, String district, double longitude, double latitude) {
        this.postId = postId;
        this.photoId = photoId;
        this.location = location;
        this.content = content;
        this.countrycode = countrycode;
        this.address = address;
        this.district = district;
        this.longitude = longitude;
        this.latitude = latitude;
    }


    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
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

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


}
