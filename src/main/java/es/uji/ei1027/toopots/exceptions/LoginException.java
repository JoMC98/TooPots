package es.uji.ei1027.toopots.exceptions;

public class LoginException extends RuntimeException {

    String message;
    String name;

    public LoginException(String message, String name)
    {
        this.message=message;
        this.name=name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

