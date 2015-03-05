package jin.io;

public class JsonProcessingException extends Exception {

    public JsonProcessingException() {
        super();
    }

    public JsonProcessingException(String message) {
        super(message);
    }

    public JsonProcessingException(Exception innerException) {
        super(innerException);
    }
}
