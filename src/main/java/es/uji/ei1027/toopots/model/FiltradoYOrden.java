package es.uji.ei1027.toopots.model;

public class FiltradoYOrden {

    //Atributos
    private String filtroCriteri;
    private String filtroPatro;
    private String ordenCriteri;
    private String ordenOrientacio;

    //Constructor
    public FiltradoYOrden() {
    }

    //Getter
    public String getFiltroCriteri() {
        return filtroCriteri;
    }

    public String getFiltroPatro() {
        return filtroPatro;
    }

    public String getOrdenCriteri() {
        return ordenCriteri;
    }

    public String getOrdenOrientacio() {
        return ordenOrientacio;
    }

    //Setter
    public void setFiltroCriteri(String filtroCriteri) {
        this.filtroCriteri = filtroCriteri;
    }

    public void setFiltroPatro(String filtroPatro) {
        this.filtroPatro = filtroPatro;
    }

    public void setOrdenCriteri(String ordenCriteri) {
        this.ordenCriteri = ordenCriteri;
    }

    public void setOrdenOrientacio(String ordenOrientacio) {
        this.ordenOrientacio = ordenOrientacio;
    }
}
