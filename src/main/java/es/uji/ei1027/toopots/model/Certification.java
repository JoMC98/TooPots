package es.uji.ei1027.toopots.model;

public class Certification {

    //Atributos

    private int id;
    private String certificate;
    private String doc;
    private int idInstructor;

    //Contruvtor

    public Certification() {

    }

    //Getters


    public int getId() {
        return id;
    }

    public String getCertificate() {
        return certificate;
    }

    public String getDoc() {
        return doc;
    }

    public int getIdInstructor() {
        return idInstructor;
    }

    //Setters


    public void setId(int id) {
        this.id = id;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public void setIdInstructor(int idInstructor) {
        this.idInstructor = idInstructor;
    }


    //toString

    @Override
    public String toString() {
        return "Certification{" +
                "id=" + id +
                ", certificate='" + certificate + '\'' +
                ", doc='" + doc + '\'' +
                ", idInstructor=" + idInstructor +
                '}';
    }

}
