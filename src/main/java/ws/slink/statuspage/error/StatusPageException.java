package ws.slink.statuspage.error;

public abstract class StatusPageException extends RuntimeException {

    private Throwable cause;

    public StatusPageException() {super();}
    public StatusPageException(String message) {super(message);}
    public StatusPageException setCause(Throwable cause) {
        this.cause = cause;
        return this;
    }
}
