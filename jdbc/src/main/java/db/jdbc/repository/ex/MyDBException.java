package db.jdbc.repository.ex;

public class MyDBException extends RuntimeException{    //RuntimeException을 extends 했기 때문에 언체크 예외이다.
    public MyDBException() {
    }

    public MyDBException(String message) {
        super(message);
    }

    public MyDBException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDBException(Throwable cause) {
        super(cause);
    }
}
