package es.uji.ei1027.toopots.model;

public class CertificationNames {
    //Atributos
    private String certificate1;
    private String certificate2;
    private String certificate3;
    private String certificate4;

    //Constructor
    public CertificationNames() {
    }

    //Getter
    public String getCertificate1() {
        return certificate1;
    }

    public String getCertificate2() {
        return certificate2;
    }

    public String getCertificate3() {
        return certificate3;
    }

    public String getCertificate4() {
        return certificate4;
    }

    //Setter
    public void setCertificate1(String certificate1) {
        this.certificate1 = certificate1;
    }

    public void setCertificate2(String certificate2) {
        this.certificate2 = certificate2;
    }

    public void setCertificate3(String certificate3) {
        this.certificate3 = certificate3;
    }

    public void setCertificate4(String certificate4) {
        this.certificate4 = certificate4;
    }

    //ToString

    @Override
    public String toString() {
        return "CertificationNames{" +
                "certificate1='" + certificate1 + '\'' +
                ", certificate2='" + certificate2 + '\'' +
                ", certificate3='" + certificate3 + '\'' +
                ", certificate4='" + certificate4 + '\'' +
                '}';
    }
}
