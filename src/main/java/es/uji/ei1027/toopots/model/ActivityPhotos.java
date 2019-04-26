package es.uji.ei1027.toopots.model;

public class ActivityPhotos {
    private int idActivity;
    private int photoNumber;
    private String photo;

    //Constructor
    public ActivityPhotos() {
    }

    //Getters
    public int getIdActivity() {
        return idActivity;
    }

    public int getPhotoNumber() {
        return photoNumber;
    }

    public String getPhoto() {
        return photo;
    }

    //Setters
    public void setIdActivity(int idActivity) {
        this.idActivity = idActivity;
    }

    public void setPhotoNumber(int photoNumber) {
        this.photoNumber = photoNumber;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    //ToString

    @Override
    public String toString() {
        return "ActivityPhotos{" +
                "idActivity=" + idActivity +
                ", photoNumber=" + photoNumber +
                ", photo='" + photo + '\'' +
                '}';
    }
}