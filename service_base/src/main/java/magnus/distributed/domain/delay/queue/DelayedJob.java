package magnus.distributed.domain.delay.queue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DelayedJob {
    private String jobId;    // jobId
    private String topicId;       // topicId
    private Long delayTime;
    private String jobBody;
    private Integer retry;
    private String url;
}
