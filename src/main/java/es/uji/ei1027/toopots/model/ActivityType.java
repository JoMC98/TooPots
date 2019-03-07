package es.uji.ei1027.toopots.model;

public class ActivityType {

    //Atributos
    private int id;
    private String name;
    private String level;
    private String description;

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

    //ToString
    @Override
    public String toString() {
        return "ActivityType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level='" + level + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
