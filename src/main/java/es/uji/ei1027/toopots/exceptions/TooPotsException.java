package es.uji.ei1027.toopots.exceptions;

public class TooPotsException extends RuntimeException {

    String messagePrincipal;
    String messageSecundari;
    String name;

    public TooPotsException(String messagePrincipal, String messageSecundari, String name)
    {
        this.messagePrincipal=messagePrincipal;
        this.messageSecundari=messageSecundari;
        this.name=name;
    }

    public String getMessagePrincipal() {

        return messagePrincipal;
    }

    public void setMessagePrincipal(String messagePrincipal) {
        this.messagePrincipal = messagePrincipal;
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
