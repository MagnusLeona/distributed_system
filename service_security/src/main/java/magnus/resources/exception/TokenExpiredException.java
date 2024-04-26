package magnus.resources.exception;

public class TokenExpiredException extends Exception {

    public TokenExpiredException() {
        super();
    }


    public TokenExpiredException(String s) {
        super(s);
    }
}
