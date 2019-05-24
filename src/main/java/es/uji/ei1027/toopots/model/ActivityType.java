package es.uji.ei1027.toopots.model;

public class ActivityType implements Comparable<ActivityType> {

    //Atributos
    private int id;
    private String name;
    private String level;
    private String description;
    private String photo;

    //Para usar en la lista de subscripciones
    private boolean subscribe;

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

    public boolean isSubscribe() {
        return subscribe;
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

    public void setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
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
                ", subscribe=" + subscribe +
                '}';
    }


    //Metodo equals para comparar con otro Activity Type y ver si son iguales
    @Override
    public boolean equals(Object other) {
        ActivityType act = (ActivityType) other;
        return id == act.getId();
    }

    //Metodo comparador (por nombre o nivel)
    public int compareTo(ActivityType altre) {
        String actualName = this.getName().toLowerCase();
        String otherName = altre.getName().toLowerCase();

        if (actualName.equals(otherName)) {
            return this.getLevel().compareTo(altre.getLevel());
        } else {
            return actualName.compareTo(otherName);
        }
    }


}
