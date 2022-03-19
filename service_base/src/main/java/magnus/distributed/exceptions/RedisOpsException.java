package magnus.distributed.exceptions;

public class RedisOpsException extends Exception {
    String value;

    public RedisOpsException(String value) {
        super(value);
    }

    public RedisOpsException() {
    }
}
