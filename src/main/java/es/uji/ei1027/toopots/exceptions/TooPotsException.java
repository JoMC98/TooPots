package es.uji.ei1027.toopots.exceptions;

public class TooPotsException extends RuntimeException {

    String message;
    String messageSecundari;
    String name;

    public TooPotsException(String message, String messageSecundari, String name)
    {
        this.message=message;
        this.messageSecundari=messageSecundari;
        this.name=name;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageSecundari() {
        return messageSecundari;
    }

    public void setMessageSecundari(String messageSecundari) {
        this.messageSecundari = messageSecundari;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
