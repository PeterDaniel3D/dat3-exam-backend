package errorhandling;

public class ExceptionDTO {

    private int errorCode;
    private String message;

    public ExceptionDTO(int code, String description) {
        this.errorCode = code;
        this.message = description;
    }

    public int getCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
