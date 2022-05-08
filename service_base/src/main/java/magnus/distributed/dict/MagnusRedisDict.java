package magnus.distributed.dict;

public class MagnusRedisDict {
    // 还未到时间的任务队列
    public static final String REDIS_KEY_ORDER_TOBE_WAITING = "suspended-order-zset";
    // 已经失效，等待被处理的任务队列
    public static final String REDIS_KEY_ORDER_TOBE_WAITING_HANDLING = "suspended-order-list";
    public static final String REDIS_KEY_DELAYED_JOB_POOL = "delayed-job-pool";
    public static final String REDIS_KEY_SUSPENDED_ORDER_LOCK = "suspended-order-lock";
    // 正在处理的任务队列
    public static final String REDIS_KEY_SUSPENDED_ORDER_IN_EXECUTION = "suspended-order-in-execution";
}
