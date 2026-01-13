package fr.elikia.backend.bo;

public class LogicResult<T> {
    private String code;
    private String message;
    private T data;

    // Constructors
    public LogicResult(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public LogicResult(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // Getters & Setters
    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }
}
