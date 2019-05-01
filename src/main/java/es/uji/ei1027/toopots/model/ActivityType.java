package es.uji.ei1027.toopots.model;

public class ActivityType implements Comparable<ActivityType> {

    //Atributos
    private int id;
    private String name;
    private String level;
    private String description;
    private String photo;

    //Constructor
    public ActivityType() {
    }

    //Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    public String getPhoto() {
        return photo;
    }

    //Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    //ToString

    @Override
    public String toString() {
        return "ActivityType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level='" + level + '\'' +
                ", description='" + description + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        ActivityType act = (ActivityType) other;
        return id == act.getId();
    }

    public int compareTo(ActivityType altre) {
        if (this.getName().equals(altre.getName())) {
            return this.getLevel().compareTo(altre.getLevel());
        } else {
            return this.getName().compareTo(altre.getName());
        }
    }

}
