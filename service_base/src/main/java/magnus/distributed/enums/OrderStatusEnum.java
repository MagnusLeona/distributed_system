package magnus.distributed.enums;

public enum OrderStatusEnum {

    CREATED(0),
    HANGED(1),
    PAID(3),
    CANCELED(-1);

    int status;

    OrderStatusEnum(int status) {
        this.status = status;
    }

    public int get() {
        return status;
    }
}
