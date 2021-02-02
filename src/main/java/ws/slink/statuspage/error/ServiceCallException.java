package ws.slink.statuspage.error;

public class ServiceCallException extends StatusPageException {
    private int code;
    public ServiceCallException() {super();}
    public ServiceCallException(String message) {super(message);}
    public ServiceCallException(String message, int code) {super(message); this.code = code;}
    public int getCode() {return code;}
}
